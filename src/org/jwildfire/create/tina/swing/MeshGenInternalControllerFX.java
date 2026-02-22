package org.jwildfire.create.tina.swing;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.meshgen.SequenceFilenameGen;
import org.jwildfire.create.tina.meshgen.filter.PreFilter;
import org.jwildfire.create.tina.meshgen.filter.PreFilterType;
import org.jwildfire.create.tina.meshgen.GenerateMeshThread;
import org.jwildfire.create.tina.meshgen.MeshGenGenerateThreadFinishEvent;
import org.jwildfire.create.tina.meshgen.render.MeshGenRenderOutputType;
import org.jwildfire.create.tina.meshgen.render.MeshGenRenderThread;
import org.jwildfire.create.tina.meshgen.render.RenderPointCloudThread;
import org.jwildfire.create.tina.meshgen.render.RenderSlicesThread;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.render.ProgressUpdater;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

public class MeshGenInternalControllerFX implements Initializable {

    @FXML private TextField resolutionXField;
    @FXML private TextField resolutionYField;
    @FXML private TextField resolutionZField; // Not used directly in logic but good for metadata
    @FXML private TextField qualityField;
    @FXML private ComboBox<MeshGenRenderOutputType> outputTypeCmb;
    @FXML private ComboBox<PreFilterType> preFilter1Cmb;
    @FXML private ComboBox<PreFilterType> preFilter2Cmb;
    @FXML private TextField filenameField;
    @FXML private TextField sliceCountField;
    @FXML private TextField thresholdField;
    @FXML private ProgressBar progressBar;
    @FXML private Button generateSlicesBtn;
    @FXML private Button generateMeshBtn;
    @FXML private Button cancelBtn;

    private TinaController tinaController;
    private Prefs prefs;

    // State
    private MeshGenRenderThread renderSlicesThread;
    private GenerateMeshThread generateMeshThread;
    private String lastRenderedSequenceOutFilePattern;
    private String lastGeneratedMeshFilename;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prefs = Prefs.getPrefs();
        outputTypeCmb.getItems().addAll(MeshGenRenderOutputType.values());
        outputTypeCmb.getSelectionModel().select(MeshGenRenderOutputType.VOXELSTACK);

        preFilter1Cmb.getItems().addAll(PreFilterType.values());
        preFilter1Cmb.getSelectionModel().select(PreFilterType.NONE);

        preFilter2Cmb.getItems().addAll(PreFilterType.values());
        preFilter2Cmb.getSelectionModel().select(PreFilterType.NONE);

        // Defaults
        resolutionXField.setText("512");
        resolutionYField.setText("512");
        qualityField.setText("100");
        sliceCountField.setText("100");

        enableControls();
    }

    public void setTinaController(TinaController tinaController) {
        this.tinaController = tinaController;
    }

    @FXML private void onBrowseFilename(ActionEvent event) {
        if (tinaController == null) return;
        File file = FileDialogTools.selectImageFileForSave(tinaController.getMainEditorFrame(), null, Tools.FILEEXT_PNG);
        if (file != null) {
            filenameField.setText(file.getAbsolutePath());
        }
    }

    @FXML private void onGenerateSlices(ActionEvent event) {
        if (tinaController == null) return;

        // Validation
        if (filenameField.getText().isEmpty()) {
            showError("Please select an output filename first.");
            return;
        }

        try {
            File file = new File(filenameField.getText());
            prefs.setLastOutputImageFile(file);

            MeshGenGenerateThreadFinishEvent finishEvent = new MeshGenGenerateThreadFinishEvent() {
                @Override
                public void succeeded(double elapsedTime) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setContentText("Slice generation finished in " + elapsedTime + "s");
                        alert.show();
                        renderSlicesThread = null;
                        enableControls();
                    });
                }

                @Override
                public void failed(Throwable exception) {
                    Platform.runLater(() -> {
                        showError("Slice generation failed: " + exception.getMessage());
                        renderSlicesThread = null;
                        enableControls();
                    });
                }
            };

            Flame flame = tinaController.getCurrFlame();
            if (flame == null) {
                showError("No flame loaded in editor.");
                return;
            }
            flame = flame.makeCopy(); // Defensive copy

            MeshGenRenderOutputType type = outputTypeCmb.getValue();
            if (type == MeshGenRenderOutputType.VOXELSTACK) {
                String outfilenamePattern = SequenceFilenameGen.createFilenamePattern(file);

                // Prepare flame for grayscale/structure rendering
                Flame grayFlame = flame.makeCopy();
                RGBPalette gradient = new RGBPalette();
                for (int i = 0; i < RGBPalette.PALETTE_SIZE; i++) {
                    gradient.setColor(i, 225, 225, 225);
                }
                grayFlame.getFirstLayer().setPalette(gradient);

                int width = Integer.parseInt(resolutionXField.getText());
                int height = Integer.parseInt(resolutionYField.getText());
                int slices = Integer.parseInt(sliceCountField.getText());
                int quality = Integer.parseInt(qualityField.getText());

                renderSlicesThread = new RenderSlicesThread(
                    prefs, grayFlame, outfilenamePattern, finishEvent,
                    new JavaFXProgressUpdater(progressBar),
                    width, height, slices, 32, quality, 0.0, 1.0 // Z-min, Z-max hardcoded for now or add fields
                );
                lastRenderedSequenceOutFilePattern = outfilenamePattern;
            } else {
                 renderSlicesThread = new RenderPointCloudThread(
                     prefs, flame, file.getAbsolutePath(), finishEvent,
                     new JavaFXProgressUpdater(progressBar),
                     Integer.parseInt(resolutionXField.getText()),
                     Integer.parseInt(resolutionYField.getText()),
                     Integer.parseInt(qualityField.getText()),
                     0.0, 1.0, 0.01 // Z-min, Z-max, cell-size
                 );
            }

            enableControls();
            new Thread(renderSlicesThread).start();

        } catch (Exception e) {
            showError("Error starting slice generation: " + e.getMessage());
        }
    }

    @FXML private void onGenerateMesh(ActionEvent event) {
        if (lastRenderedSequenceOutFilePattern == null) {
            showError("Please generate slices first.");
            return;
        }

        try {
            File outFile = FileDialogTools.selectMeshFileForSave(tinaController.getMainEditorFrame(), null);
            if (outFile == null) return;

            prefs.setLastMeshFile(outFile);

            MeshGenGenerateThreadFinishEvent finishEvent = new MeshGenGenerateThreadFinishEvent() {
                @Override
                public void succeeded(double elapsedTime) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setContentText("Mesh generation finished in " + elapsedTime + "s");
                        alert.show();
                        lastGeneratedMeshFilename = outFile.getAbsolutePath();
                        generateMeshThread = null;
                        enableControls();
                    });
                }
                @Override
                public void failed(Throwable exception) {
                    Platform.runLater(() -> {
                        showError("Mesh generation failed: " + exception.getMessage());
                        generateMeshThread = null;
                        enableControls();
                    });
                }
            };

            int slices = Integer.parseInt(sliceCountField.getText());
            int threshold = Integer.parseInt(thresholdField.getText());

            generateMeshThread = new GenerateMeshThread(
                outFile.getAbsolutePath(), finishEvent,
                new JavaFXProgressUpdater(progressBar),
                lastRenderedSequenceOutFilePattern,
                slices, 1, threshold, 0.25, 2, true, // Defaults for step, radius, downsample
                getPreFilterList(),
                false, 20, 0.84, -0.90 // Smoothing defaults
            );

            enableControls();
            new Thread(generateMeshThread).start();

        } catch (Exception e) {
            showError("Error starting mesh generation: " + e.getMessage());
        }
    }

    @FXML private void onCancel(ActionEvent event) {
        if (renderSlicesThread != null) {
            renderSlicesThread.setForceAbort();
            // Wait logic would block UI, so we trust the thread to check abort flag
        }
        if (generateMeshThread != null) {
            generateMeshThread.setForceAbort();
        }
        enableControls();
    }

    private void enableControls() {
        boolean rendering = (renderSlicesThread != null && !renderSlicesThread.isFinished()) ||
                            (generateMeshThread != null && !generateMeshThread.isFinished());

        Platform.runLater(() -> {
            generateSlicesBtn.setDisable(rendering);
            generateMeshBtn.setDisable(rendering || lastRenderedSequenceOutFilePattern == null);
            cancelBtn.setDisable(!rendering);

            resolutionXField.setDisable(rendering);
            resolutionYField.setDisable(rendering);
            qualityField.setDisable(rendering);
            sliceCountField.setDisable(rendering);
            thresholdField.setDisable(rendering);
            outputTypeCmb.setDisable(rendering);
            filenameField.setDisable(rendering);
        });
    }

    private List<PreFilter> getPreFilterList() {
        List<PreFilter> list = new ArrayList<>();
        if (preFilter1Cmb.getValue() != null && preFilter1Cmb.getValue() != PreFilterType.NONE) {
            list.add(preFilter1Cmb.getValue().getFilter());
        }
        if (preFilter2Cmb.getValue() != null && preFilter2Cmb.getValue() != PreFilterType.NONE) {
            list.add(preFilter2Cmb.getValue().getFilter());
        }
        return list;
    }

    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }

    private class JavaFXProgressUpdater implements ProgressUpdater {
        private final ProgressBar bar;
        private int max = 100;

        public JavaFXProgressUpdater(ProgressBar bar) {
            this.bar = bar;
        }

        @Override
        public void initProgress(int maxSteps) {
            this.max = maxSteps;
            Platform.runLater(() -> bar.setProgress(0));
        }

        @Override
        public void updateProgress(int step) {
            Platform.runLater(() -> {
                if (max > 0) bar.setProgress((double) step / max);
            });
        }
    }
}
