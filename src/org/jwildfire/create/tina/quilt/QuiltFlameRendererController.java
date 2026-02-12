package org.jwildfire.create.tina.quilt;

import java.awt.Rectangle;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.Toolkit;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.FlameReader;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.ProgressUpdater;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.swing.FileDialogTools;
import org.jwildfire.create.tina.swing.FlamePreparer;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.transform.RectangleTransformer;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

public class QuiltFlameRendererController implements Initializable {

    @FXML private TextField qualityField;
    @FXML private TextField renderWidthField;
    @FXML private TextField renderHeightField;
    @FXML private TextField xSegmentationField;
    @FXML private TextField ySegmentationField;
    @FXML private TextField segmentWidthField;
    @FXML private TextField segmentHeightField;
    @FXML private TextField outputFilenameField;
    @FXML private StackPane previewPane;
    @FXML private ImageView previewImageView;
    @FXML private ProgressBar segmentProgressBar;
    @FXML private ProgressBar totalProgressBar;
    @FXML private Button renderBtn;
    @FXML private Button openFlameBtn;
    @FXML private Button importFromEditorBtn;
    @FXML private Button importFromClipboardBtn;

    private TinaController tinaController;
    private Prefs prefs;
    private Flame currFlame;
    private QuiltRenderThread currRenderThread;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prefs = Prefs.getPrefs();
        // Initialize defaults
        qualityField.setText("100");
        renderWidthField.setText("3840");
        renderHeightField.setText("2160");
        xSegmentationField.setText("2");
        ySegmentationField.setText("2");

        // Add listeners to text fields to trigger recalc
        renderWidthField.textProperty().addListener((obs, oldVal, newVal) -> recalcSegmentSize());
        renderHeightField.textProperty().addListener((obs, oldVal, newVal) -> recalcSegmentSize());
        xSegmentationField.textProperty().addListener((obs, oldVal, newVal) -> recalcSegmentSize());
        ySegmentationField.textProperty().addListener((obs, oldVal, newVal) -> recalcSegmentSize());
        qualityField.textProperty().addListener((obs, oldVal, newVal) -> recalcSegmentSize());

        recalcSegmentSize();
    }

    public void setTinaController(TinaController tinaController) {
        this.tinaController = tinaController;
    }

    private void recalcSegmentSize() {
        try {
            int width = Integer.parseInt(renderWidthField.getText());
            int height = Integer.parseInt(renderHeightField.getText());
            int xSeg = Integer.parseInt(xSegmentationField.getText());
            int ySeg = Integer.parseInt(ySegmentationField.getText());

            if (xSeg > 0 && ySeg > 0) {
                segmentWidthField.setText(String.valueOf(width / xSeg));
                segmentHeightField.setText(String.valueOf(height / ySeg));
            }
            refreshPreviewImage();
        } catch (NumberFormatException e) {
            // Ignore invalid input during typing
        }
    }

    @FXML private void onOpenFlame(ActionEvent event) {
        if (tinaController == null) return;
        File file = FileDialogTools.selectFlameFileForOpen(tinaController.getMainEditorFrame(), null, null);
        if (file != null) {
            try {
                List<Flame> flames = new FlameReader(prefs).readFlames(file.getAbsolutePath());
                if (!flames.isEmpty()) {
                    currFlame = flames.get(0);
                    refreshOutputFilename();
                    refreshPreviewImage();
                }
            } catch (Exception e) {
                showError("Error loading flame", e);
            }
        }
    }

    @FXML private void onFromEditor(ActionEvent event) {
        if (tinaController != null) {
            Flame newFlame = tinaController.exportFlame();
            if (newFlame != null) {
                currFlame = newFlame;
                refreshOutputFilename();
                refreshPreviewImage();
            }
        }
    }

    @FXML private void onFromClipboard(ActionEvent event) {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable clipData = clipboard.getContents(clipboard);
            if (clipData != null && clipData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                String xml = (String) (clipData.getTransferData(DataFlavor.stringFlavor));
                List<Flame> flames = new FlameReader(prefs).readFlamesfromXML(xml);
                if (!flames.isEmpty()) {
                    currFlame = flames.get(0);
                    refreshOutputFilename();
                    refreshPreviewImage();
                } else {
                    showError("No flame found in clipboard", null);
                }
            }
        } catch (Exception e) {
            showError("Error importing from clipboard", e);
        }
    }

    @FXML private void onSetSize4K(ActionEvent event) {
        renderWidthField.setText("3840");
        renderHeightField.setText("2160");
    }

    @FXML private void onSetSize8K(ActionEvent event) {
        renderWidthField.setText("7680");
        renderHeightField.setText("4320");
    }

    @FXML private void onSetSize16K(ActionEvent event) {
        renderWidthField.setText("15360");
        renderHeightField.setText("8640");
    }

    @FXML private void onSetSize32K(ActionEvent event) {
        renderWidthField.setText("30720");
        renderHeightField.setText("17280");
    }

    @FXML private void onRender(ActionEvent event) {
        if (currRenderThread != null && !currRenderThread.isDone()) {
            // Cancel action
            currRenderThread.cancel();
            renderBtn.setDisable(true); // Disable until cancelled
        } else {
            // Start action
            if (currFlame == null) {
                showError("No flame loaded", null);
                return;
            }
            try {
                currRenderThread = new QuiltRenderThread();
                Thread thread = new Thread(currRenderThread);
                thread.setDaemon(true);
                thread.start();
                enableControls(true);
            } catch (Exception e) {
                showError("Failed to start render", e);
            }
        }
    }

    private void enableControls(boolean isRendering) {
        Platform.runLater(() -> {
            renderBtn.setText(isRendering ? "Cancel" : "Render");
            renderBtn.setDisable(false);

            boolean disabled = isRendering;
            renderWidthField.setDisable(disabled);
            renderHeightField.setDisable(disabled);
            xSegmentationField.setDisable(disabled);
            ySegmentationField.setDisable(disabled);
            qualityField.setDisable(disabled);
            outputFilenameField.setDisable(disabled);
            openFlameBtn.setDisable(disabled);
            importFromEditorBtn.setDisable(disabled);
            importFromClipboardBtn.setDisable(disabled);
        });
    }

    private void refreshOutputFilename() {
        int extension = 0;
        String pathname = null;
        while (pathname == null) {
          String flamename = currFlame != null ? currFlame.getName() : null;
          if (flamename != null) {
            flamename = flamename.replaceAll("[ \\/:]", "");
          }
          if (flamename == null || flamename.isEmpty()) {
            flamename = "quilt";
          }

          String imagename = (extension++ > 0 ? flamename + "_" + extension : flamename) + ".png";

          String folder = prefs.getOutputImagePath();
          if (folder == null || folder.isEmpty()) {
            pathname = imagename;
          } else {
            pathname = new File(folder, imagename).getAbsolutePath();
          }
          if (new File(pathname).exists()) {
            pathname = null;
          }
        }
        outputFilenameField.setText(pathname);
    }

    private void refreshPreviewImage() {
        if (currFlame == null) {
            previewImageView.setImage(null);
            return;
        }

        try {
            int width = (int) Math.max(previewPane.getWidth(), 320);
            int height = (int) Math.max(previewPane.getHeight(), 240);

            Flame flame = currFlame.makeCopy();
            RenderInfo info = new RenderInfo(width, height, RenderMode.PREVIEW);

            double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
            double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
            flame.setPixelsPerUnitScale((wScl + hScl) * 0.5);

            FlameRenderer renderer = new FlameRenderer(flame, prefs, false, false);
            flame.setSampleDensity(Math.min(prefs.getTinaRenderRealtimeQuality(), 5.0));
            flame.setSpatialFilterRadius(0.0);
            flame.setDeRadius(0.0);

            RenderedFlame res = renderer.renderFlame(info);
            SimpleImage image = res.getImage();

            addSegmentBorders(image);

            Platform.runLater(() -> {
                previewImageView.setImage(SwingFXUtils.toFXImage(image.getBufferedImg(), null));
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addSegmentBorders(SimpleImage image) {
        RectangleTransformer rect = new RectangleTransformer();
        int xLevel = Integer.parseInt(xSegmentationField.getText());
        int yLevel = Integer.parseInt(ySegmentationField.getText());
        String filename = outputFilenameField.getText();
        int destWidth = Integer.parseInt(renderWidthField.getText());
        int destHeight = Integer.parseInt(renderHeightField.getText());
        int qualityLevel = (int) (Double.parseDouble(qualityField.getText()) + 0.5);

        QuiltFlameRenderer renderer = new QuiltFlameRenderer();
        int spentHeight = 0;

        for (int i = 0; i < yLevel; i++) {
          int height = (int) ((double) image.getImageHeight() / (double) yLevel + 0.5);
          int spentWidth = 0;
          for (int j = 0; j < xLevel; j++) {
            int width = (int) ((double) image.getImageWidth() / (double) xLevel + 0.5);

            // Check if segment exists (mocking the check for preview purposes)
            // Real check requires full filename logic which we have
            String segmentFilename = renderer.getSegmentFilename(
                filename, destWidth, destHeight, xLevel, yLevel, qualityLevel, j, i);
            boolean isRendered = new File(segmentFilename).exists();

            if (isRendered) {
              rect.setColor(new java.awt.Color(128, 255, 32));
              rect.setThickness(7);
            } else {
              rect.setColor(new java.awt.Color(255, 128, 32));
              rect.setThickness(2);
            }

            rect.setLeft(spentWidth);
            rect.setTop(spentHeight);
            rect.setWidth(j < xLevel - 1 ? width + 1 : image.getImageWidth() - spentWidth);
            rect.setHeight(i < yLevel - 1 ? height + 1 : image.getImageHeight() - spentHeight);
            rect.transformImage(image);
            spentWidth += width;
          }
          spentHeight += height;
        }
    }

    private void showError(String header, Exception e) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText(header);
            if (e != null) {
                alert.setContentText(e.getMessage());
            }
            alert.showAndWait();
        });
    }

    private class JavaFXProgressUpdater implements ProgressUpdater {
        private final ProgressBar progressBar;
        private final boolean refreshPreview;

        public JavaFXProgressUpdater(ProgressBar progressBar, boolean refreshPreview) {
            this.progressBar = progressBar;
            this.refreshPreview = refreshPreview;
        }

        @Override
        public void initProgress(int maxSteps) {
            Platform.runLater(() -> progressBar.setProgress(0));
        }

        @Override
        public void updateProgress(int step) {
             // Assuming step is 0 to maxSteps, we assume maxSteps is known or we treat step as percentage if normalized?
             // Legacy interface: initProgress(maxSteps), updateProgress(currentStep).
             // We need to store maxSteps to calculate percentage for JavaFX ProgressBar (0.0 to 1.0)
             // But the interface doesn't enforce storing state.
             // However, checking QuiltFlameRenderer.java, it calls initProgress(x*y).
             // And updateProgress(count).
             // So we should capture maxSteps in initProgress.
        }

        // Custom method to handle the stateful nature of JavaFX ProgressBar
        public void updateProgress(int step, int maxSteps) {
             Platform.runLater(() -> {
                 if (maxSteps > 0) {
                     progressBar.setProgress((double) step / maxSteps);
                 }
                 if (refreshPreview) {
                     refreshPreviewImage();
                 }
             });
        }
    }

    // Since the interface is void updateProgress(int), we need to handle the max value.
    // I'll create a smart wrapper.
    private class SmartProgressUpdater implements ProgressUpdater {
        private final ProgressBar progressBar;
        private final boolean refreshPreview;
        private int maxSteps = 100;

        public SmartProgressUpdater(ProgressBar progressBar, boolean refreshPreview) {
            this.progressBar = progressBar;
            this.refreshPreview = refreshPreview;
        }

        @Override
        public void initProgress(int maxSteps) {
            this.maxSteps = maxSteps;
            Platform.runLater(() -> progressBar.setProgress(0));
        }

        @Override
        public void updateProgress(int step) {
             Platform.runLater(() -> {
                 if (maxSteps > 0) {
                     progressBar.setProgress((double) step / maxSteps);
                 }
                 if (refreshPreview) {
                     refreshPreviewImage();
                 }
             });
        }
    }

    private class QuiltRenderThread implements Runnable {
        private QuiltFlameRenderer renderer;
        private boolean done = false;

        @Override
        public void run() {
            try {
                renderer = new QuiltFlameRenderer();
                Flame flame = currFlame.makeCopy();

                int destWidth = Integer.parseInt(renderWidthField.getText());
                int destHeight = Integer.parseInt(renderHeightField.getText());
                double quality = Double.parseDouble(qualityField.getText());
                int xSeg = Integer.parseInt(xSegmentationField.getText());
                int ySeg = Integer.parseInt(ySegmentationField.getText());
                int qualityLevel = (int) (quality + 0.5);
                String outputFilename = outputFilenameField.getText();

                double wScl = (double) destWidth / (double) flame.getWidth();
                double hScl = (double) destHeight / (double) flame.getHeight();
                flame.setPixelsPerUnitScale((wScl + hScl) * 0.5);
                flame.setSampleDensity(quality);

                renderer.renderFlame(
                    flame, destWidth, destHeight, xSeg, ySeg, qualityLevel, outputFilename,
                    new SmartProgressUpdater(totalProgressBar, true),
                    new SmartProgressUpdater(segmentProgressBar, false)
                );

                Platform.runLater(() -> {
                   Alert alert = new Alert(AlertType.INFORMATION);
                   alert.setContentText("Render finished successfully.\nSaved to: " + outputFilename);
                   alert.show();
                });

            } catch (Exception e) {
                if (!renderer.isDone()) { // If not just cancelled
                    showError("Render Failed", e);
                }
            } finally {
                done = true;
                enableControls(false);
            }
        }

        public void cancel() {
            if (renderer != null) renderer.cancel();
        }

        public boolean isDone() { return done; }
    }
}
