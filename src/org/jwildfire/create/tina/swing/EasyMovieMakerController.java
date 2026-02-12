package org.jwildfire.create.tina.swing;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.QualityProfile;
import org.jwildfire.base.ResolutionProfile;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.animate.FlameMovie;
import org.jwildfire.create.tina.animate.FlameMoviePart;
import org.jwildfire.create.tina.animate.GlobalScript;
import org.jwildfire.create.tina.animate.GlobalScriptType;
import org.jwildfire.create.tina.animate.SWFAnimationRenderThread;
import org.jwildfire.create.tina.animate.SWFAnimationRenderThreadController;
import org.jwildfire.create.tina.animate.SequenceOutputType;
import org.jwildfire.create.tina.animate.XFormScript;
import org.jwildfire.create.tina.animate.XFormScriptType;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.motion.MotionCurve;
import org.jwildfire.create.tina.io.FlameReader;
import org.jwildfire.create.tina.randommovie.RandomMovieGeneratorList;
import org.jwildfire.create.tina.render.ProgressUpdater;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class EasyMovieMakerController implements Initializable, SWFAnimationRenderThreadController {

    @FXML private Button randomMoviesBtn;
    @FXML private ComboBox<String> randomGenCmb;
    @FXML private ComboBox<ResolutionProfile> resolutionProfileCmb;
    @FXML private ComboBox<QualityProfile> qualityProfileCmb;
    @FXML private ComboBox<SequenceOutputType> outputTypeCmb;
    @FXML private Button renderBtn;
    @FXML private Button cancelBtn;
    @FXML private ProgressBar progressBar;

    @FXML private ListView<String> flamesListView;
    @FXML private CheckBox compatibilityCbx;

    @FXML private ImageView previewImageView;
    @FXML private Slider timelineSlider;
    @FXML private Button playBtn;
    @FXML private Button copyToEditorBtn;
    @FXML private Button renderPreviewBtn;
    @FXML private TextField currentFrameField;

    @FXML private VBox globalScriptsContainer;
    @FXML private VBox xFormScriptsContainer;

    @FXML private TextField totalFramesField;
    @FXML private TextField fpsField;
    @FXML private TextField motionBlurLenField;
    @FXML private TextField motionBlurStepField;

    private TinaController tinaController;
    private FlameMovie currMovie;
    private EasyMovieMakerFrame ownerFrame;
    private SWFAnimatorProgressUpdater progressUpdater;
    private FlameMoviePart selectedPart;
    private SWFAnimationRenderThread renderThread;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currMovie = new FlameMovie(Prefs.getPrefs());
        progressUpdater = new SWFAnimatorProgressUpdater(null); // Fallback

        setupListListeners();

        // Random Generators
        randomGenCmb.getItems().addAll(RandomMovieGeneratorList.getNameList());
        randomGenCmb.getSelectionModel().select(RandomMovieGeneratorList.DEFAULT_GENERATOR_NAME);

        // Profiles
        resolutionProfileCmb.getItems().addAll(
            new ResolutionProfile(false, 320, 240),
            new ResolutionProfile(false, 640, 480),
            new ResolutionProfile(false, 800, 600),
            new ResolutionProfile(false, 1024, 768),
            new ResolutionProfile(false, 1280, 720),
            new ResolutionProfile(false, 1920, 1080)
        );
        resolutionProfileCmb.getSelectionModel().select(2);

        qualityProfileCmb.getItems().addAll(
            new QualityProfile(false, "Low", 10, false, false),
            new QualityProfile(false, "Medium", 50, false, false),
            new QualityProfile(false, "High", 200, false, false)
        );
        qualityProfileCmb.getSelectionModel().select(1);

        outputTypeCmb.getItems().addAll(SequenceOutputType.values());
        outputTypeCmb.getSelectionModel().select(SequenceOutputType.PNG_IMAGES);

        setupScriptsUI();
    }

    private void setupListListeners() {
        flamesListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.intValue() >= 0 && newVal.intValue() < currMovie.getParts().size()) {
                selectedPart = currMovie.getParts().get(newVal.intValue());
                refreshScriptUI();
            } else {
                selectedPart = null;
            }
        });
    }

    public void setTinaController(TinaController tinaController) {
        this.tinaController = tinaController;
    }

    public void setOwnerFrame(EasyMovieMakerFrame ownerFrame) {
        this.ownerFrame = ownerFrame;
        // The progress updater needs a Swing component if passing to legacy code,
        // but since I am modernizing, I will use a JavaFX updater wrapper if possible,
        // or just use the field I have.
    }

    public ProgressBar getFxProgressBar() {
        return progressBar;
    }

    @Override
    public void onRenderFinished() {
        Platform.runLater(() -> {
            renderBtn.setDisable(false);
            cancelBtn.setDisable(true);
            progressBar.setProgress(0);
            renderThread = null;
        });
    }

    @Override
    public javax.swing.JProgressBar getProgressBar() { return null; }

    @Override
    public ProgressUpdater getProgressUpdater() {
        return new ProgressUpdater() {
            @Override
            public void initProgress(int maxSteps) {
                Platform.runLater(() -> progressBar.setProgress(0));
            }
            @Override
            public void updateProgress(int step) {
                 // Hack: maxSteps isn't passed here.
                 // Assuming maxSteps was set in initProgress or we treat step as percent if < 100?
                 // Legacy code usually passes actual step count.
                 // I'll ignore precise progress for now or map it if I knew the max.
                 // Actually SWFAnimationRenderThread calls initProgress(totalFrames).
            }
        };
    }

    // Better progress updater that captures max
    private class JavaFXSWFProgressUpdater implements ProgressUpdater {
        private int max = 100;
        @Override
        public void initProgress(int maxSteps) {
            this.max = maxSteps;
            Platform.runLater(() -> progressBar.setProgress(0));
        }
        @Override
        public void updateProgress(int step) {
            Platform.runLater(() -> {
                if(max > 0) progressBar.setProgress((double)step/max);
            });
        }
    }

    @Override
    public Prefs getPrefs() { return Prefs.getPrefs(); }

    private void setupScriptsUI() {
        // Just empty containers initially
        globalScriptsContainer.getChildren().clear();
        xFormScriptsContainer.getChildren().clear();
    }

    private void refreshScriptUI() {
        globalScriptsContainer.getChildren().clear();
        xFormScriptsContainer.getChildren().clear();

        if (selectedPart == null) return;

        // Ensure scripts exist (lazy init)
        while (selectedPart.getGlobalScripts().size() < 12) {
            selectedPart.getGlobalScripts().add(new GlobalScript());
        }
        while (selectedPart.getXFormScripts().size() < 12) {
            selectedPart.getXFormScripts().add(new XFormScript());
        }

        for (int i = 0; i < 12; i++) {
            GlobalScript gs = selectedPart.getGlobalScripts().get(i);
            globalScriptsContainer.getChildren().add(createGlobalScriptRow(i, gs));
        }

        for (int i = 0; i < 12; i++) {
            XFormScript xs = selectedPart.getXFormScripts().get(i);
            xFormScriptsContainer.getChildren().add(createXFormScriptRow(i, xs));
        }
    }

    private HBox createGlobalScriptRow(int index, GlobalScript script) {
        HBox row = new HBox(5);
        row.setAlignment(Pos.CENTER_LEFT);
        Label idxLabel = new Label(String.format("%02d", index + 1));

        ComboBox<GlobalScriptType> scriptCmb = new ComboBox<>();
        scriptCmb.getItems().addAll(GlobalScriptType.values());
        scriptCmb.setValue(script.getScriptType());
        scriptCmb.valueProperty().addListener((obs, o, n) -> script.setScriptType(n));

        Button editBtn = new Button("Curve");
        editBtn.setOnAction(e -> openCurveEditor(script.getAmplitudeCurve()));
        if (script.getAmplitudeCurve() != null && script.getAmplitudeCurve().isEnabled()) {
            editBtn.setStyle("-fx-base: #aaffaa;");
        }

        row.getChildren().addAll(idxLabel, scriptCmb, editBtn);
        return row;
    }

    private HBox createXFormScriptRow(int index, XFormScript script) {
        HBox row = new HBox(5);
        row.setAlignment(Pos.CENTER_LEFT);
        Label idxLabel = new Label(String.format("%02d", index + 1));

        ComboBox<XFormScriptType> scriptCmb = new ComboBox<>();
        scriptCmb.getItems().addAll(XFormScriptType.values());
        scriptCmb.setValue(script.getScriptType());
        scriptCmb.valueProperty().addListener((obs, o, n) -> script.setScriptType(n));

        Button editBtn = new Button("Curve");
        editBtn.setOnAction(e -> openCurveEditor(script.getAmplitudeCurve()));
        if (script.getAmplitudeCurve() != null && script.getAmplitudeCurve().isEnabled()) {
            editBtn.setStyle("-fx-base: #aaffaa;");
        }

        row.getChildren().addAll(idxLabel, scriptCmb, editBtn);
        return row;
    }

    private void openCurveEditor(MotionCurve curve) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Edit Motion Curve");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);

        CheckBox enabledCbx = new CheckBox("Enabled");
        enabledCbx.setSelected(curve.isEnabled());

        TextArea pointsArea = new TextArea();
        pointsArea.setPromptText("Frame, Value (one per line)");
        StringBuilder sb = new StringBuilder();
        int[] x = curve.getX();
        double[] y = curve.getY();
        if (x != null) {
            for(int i=0; i<x.length; i++) {
                sb.append(x[i]).append(", ").append(y[i]).append("\n");
            }
        }
        pointsArea.setText(sb.toString());

        grid.add(enabledCbx, 0, 0);
        grid.add(new Label("Points (Frame, Value):"), 0, 1);
        grid.add(pointsArea, 0, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                curve.setEnabled(enabledCbx.isSelected());
                String[] lines = pointsArea.getText().split("\n");
                curve.clear();
                for (String line : lines) {
                    if (line.trim().isEmpty()) continue;
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        try {
                            int frame = Integer.parseInt(parts[0].trim());
                            double value = Double.parseDouble(parts[1].trim());
                            if (!curve.hasKeyFrame(frame)) {
                                curve.addKeyFrame(frame, value);
                            } else {
                                curve.updateKeyFrame(frame, value);
                            }
                        } catch (Exception e) {
                            // Ignore bad lines
                        }
                    }
                }
                refreshScriptUI(); // Update button color
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML private void onRandomMovies(ActionEvent event) {
        // Placeholder for random generator
        showAlert("Not implemented yet.");
    }

    @FXML private void onRender(ActionEvent event) {
        try {
            updateMovieFields();
            File file = selectOutputFile();
            if (file != null) {
                Prefs.getPrefs().setLastOutputMovieFlamesFile(file);

                // Override progress updater in controller to use JavaFX one
                renderThread = new SWFAnimationRenderThread(this, currMovie, file.getAbsolutePath()) {
                     @Override
                    public ProgressUpdater getProgressUpdater() {
                        return new JavaFXSWFProgressUpdater();
                    }
                };

                renderBtn.setDisable(true);
                cancelBtn.setDisable(false);
                new Thread(renderThread).start();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Error rendering: " + ex.getMessage());
        }
    }

    @FXML private void onCancel(ActionEvent event) {
        if (renderThread != null) {
            renderThread.setCancelSignalled(true);
        }
    }

    private void updateMovieFields() {
        ResolutionProfile resProfile = resolutionProfileCmb.getValue();
        if (resProfile != null) {
            currMovie.setFrameWidth(resProfile.getWidth());
            currMovie.setFrameHeight(resProfile.getHeight());
        }
        QualityProfile qualProfile = qualityProfileCmb.getValue();
        if (qualProfile != null) {
            currMovie.setQuality(qualProfile.getQuality());
        }
        SequenceOutputType outType = outputTypeCmb.getValue();
        if (outType != null) {
            currMovie.setSequenceOutputType(outType);
        }
    }

    private File selectOutputFile() {
         if (tinaController == null) return null;
         return FileDialogTools.selectFlameSequenceFileForSave(tinaController.getMainEditorFrame(), null);
    }

    @FXML private void onAddFlameFromEditor(ActionEvent event) {
        if (tinaController != null) {
            Flame flame = tinaController.exportFlame();
            if (flame != null) {
                addFlame(flame);
            }
        }
    }

    @FXML private void onAddFlameFromDisc(ActionEvent event) {
        if (tinaController == null) return;
        File file = FileDialogTools.selectFlameFileForOpen(tinaController.getMainEditorFrame(), null, null);
        if (file != null) {
            try {
                List<Flame> flames = new FlameReader(prefs).readFlames(file.getAbsolutePath());
                if (!flames.isEmpty()) addFlame(flames.get(0));
            } catch(Exception e) {
                showAlert("Error loading flame: " + e.getMessage());
            }
        }
    }

    private void addFlame(Flame flame) {
        FlameMoviePart part = new FlameMoviePart();
        part.setFlame(flame);
        part.setFrameCount(120);
        part.setFrameMorphCount(60);
        currMovie.addPart(part);
        refreshUI();
    }

    @FXML private void onRemoveFlame(ActionEvent event) {
        int idx = flamesListView.getSelectionModel().getSelectedIndex();
        if (idx >= 0) {
            currMovie.getParts().remove(idx);
            refreshUI();
        }
    }

    @FXML private void onMoveFlameUp(ActionEvent event) {
        int idx = flamesListView.getSelectionModel().getSelectedIndex();
        if (idx > 0) {
            FlameMoviePart part = currMovie.getParts().remove(idx);
            currMovie.getParts().add(idx - 1, part);
            refreshUI();
            flamesListView.getSelectionModel().select(idx - 1);
        }
    }

    @FXML private void onMoveFlameDown(ActionEvent event) {
        int idx = flamesListView.getSelectionModel().getSelectedIndex();
        if (idx >= 0 && idx < currMovie.getParts().size() - 1) {
            FlameMoviePart part = currMovie.getParts().remove(idx);
            currMovie.getParts().add(idx + 1, part);
            refreshUI();
            flamesListView.getSelectionModel().select(idx + 1);
        }
    }

    private void refreshUI() {
        int selected = flamesListView.getSelectionModel().getSelectedIndex();
        flamesListView.getItems().clear();
        for (FlameMoviePart part : currMovie.getParts()) {
            flamesListView.getItems().add((part.getFlame().getName() != null ? part.getFlame().getName() : "Untitled") + " [" + part.getFrameCount() + " frames]");
        }
        if (selected >= 0 && selected < flamesListView.getItems().size()) {
            flamesListView.getSelectionModel().select(selected);
        }
    }

    @FXML private void onPlay(ActionEvent event) {
        // TODO: Implement preview play
    }

    @FXML private void onAddFlameFromClipboard(ActionEvent event) {
        // Placeholder
    }

    @FXML private void onRemoveAllFlames(ActionEvent event) {
        currMovie.getParts().clear();
        refreshUI();
    }

    @FXML private void onCopyToEditor(ActionEvent event) {
        if (selectedPart != null && tinaController != null) {
            tinaController.importFlame(selectedPart.getFlame(), true);
        }
    }

    @FXML private void onRenderPreview(ActionEvent event) {
        // Placeholder
    }

    public org.jwildfire.create.tina.base.Flame getFlame() {
        if (currMovie == null || currMovie.getParts().isEmpty()) return null;
        return currMovie.getParts().get(0).getFlame();
    }

    public void importFlameFromEditor(org.jwildfire.create.tina.base.Flame flame) {
        addFlame(flame);
    }

    private void showAlert(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setContentText(msg);
            alert.show();
        });
    }
}
