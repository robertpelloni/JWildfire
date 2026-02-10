package org.jwildfire.create.tina.swing;

import java.net.URL;
import java.util.ResourceBundle;

import org.jwildfire.base.QualityProfile;
import org.jwildfire.base.ResolutionProfile;
import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.animate.FlameMovie;
import org.jwildfire.create.tina.animate.FlameMoviePart;
import org.jwildfire.create.tina.animate.GlobalScriptType;
import org.jwildfire.create.tina.animate.SequenceOutputType;
import org.jwildfire.create.tina.animate.SWFAnimationRenderThreadController;
import org.jwildfire.create.tina.animate.XFormScriptType;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.randommovie.RandomMovieGeneratorList;
import org.jwildfire.create.tina.render.ProgressUpdater;
import org.jwildfire.create.tina.animate.SWFAnimationRenderThread;
import org.jwildfire.base.Tools;

import java.io.File;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currMovie = new FlameMovie(Prefs.getPrefs());
        setupScriptsUI();

        // Random Generators
        randomGenCmb.getItems().addAll(RandomMovieGeneratorList.getNameList());
        randomGenCmb.getSelectionModel().select(RandomMovieGeneratorList.DEFAULT_GENERATOR_NAME);

        // Resolution Profiles
        resolutionProfileCmb.getItems().add(new ResolutionProfile(false, 320, 240));
        resolutionProfileCmb.getItems().add(new ResolutionProfile(false, 640, 480));
        resolutionProfileCmb.getItems().add(new ResolutionProfile(false, 800, 600));
        resolutionProfileCmb.getItems().add(new ResolutionProfile(false, 1024, 768));
        resolutionProfileCmb.getItems().add(new ResolutionProfile(false, 1280, 720));
        resolutionProfileCmb.getItems().add(new ResolutionProfile(false, 1920, 1080));
        resolutionProfileCmb.getSelectionModel().select(2); // 800x600 default

        // Quality Profiles
        qualityProfileCmb.getItems().add(new QualityProfile(false, "Low", 10, false, false));
        qualityProfileCmb.getItems().add(new QualityProfile(false, "Medium", 50, false, false));
        qualityProfileCmb.getItems().add(new QualityProfile(false, "High", 200, false, false));
        qualityProfileCmb.getSelectionModel().select(1); // Medium

        // Output Type
        outputTypeCmb.getItems().addAll(SequenceOutputType.values());
        outputTypeCmb.getSelectionModel().select(SequenceOutputType.PNG_IMAGES);
    }

    public void setTinaController(TinaController tinaController) {
        this.tinaController = tinaController;
    }

    public void setOwnerFrame(EasyMovieMakerFrame ownerFrame) {
        this.ownerFrame = ownerFrame;
        this.progressUpdater = new SWFAnimatorProgressUpdater(ownerFrame);
    }

    public ProgressBar getFxProgressBar() {
        return progressBar;
    }

    // SWFAnimationRenderThreadController implementation
    @Override
    public void onRenderFinished() {
        javafx.application.Platform.runLater(() -> {
            renderBtn.setDisable(false);
            cancelBtn.setDisable(true);
            progressBar.setProgress(0);
        });
    }

    @Override
    public javax.swing.JProgressBar getProgressBar() { // Legacy return type
        return null;
    }

    @Override
    public ProgressUpdater getProgressUpdater() {
        return progressUpdater;
    }

    @Override
    public Prefs getPrefs() {
        return Prefs.getPrefs();
    }

    private void setupScriptsUI() {
        // Create 12 rows for Global Scripts
        for (int i = 1; i <= 12; i++) {
            globalScriptsContainer.getChildren().add(createScriptRow(i, GlobalScriptType.values()));
        }

        // Create 12 rows for XForm Scripts
        for (int i = 1; i <= 12; i++) {
            xFormScriptsContainer.getChildren().add(createScriptRow(i, XFormScriptType.values()));
        }
    }

    private <T extends Enum<T>> HBox createScriptRow(int index, T[] values) {
        HBox row = new HBox(5);
        row.setAlignment(Pos.CENTER_LEFT);

        Label idxLabel = new Label(String.format("%02d", index));
        idxLabel.setPrefWidth(20);

        ComboBox<T> scriptCmb = new ComboBox<>();
        scriptCmb.setPrefWidth(150);
        scriptCmb.getItems().addAll(values);
        HBox.setHgrow(scriptCmb, Priority.ALWAYS);

        TextField valueField = new TextField("1.0");
        valueField.setPrefWidth(60);

        // TODO: Add motion curve handling via context menu or click

        row.getChildren().addAll(idxLabel, scriptCmb, valueField);
        return row;
    }

    @FXML private void onRandomMovies(ActionEvent event) {
        // TODO
    }

    @FXML private void onRender(ActionEvent event) {
        try {
            updateMovieFields();
            File file = selectOutputFile();
            if (file != null) {
                Prefs.getPrefs().setLastOutputMovieFlamesFile(file);
                SWFAnimationRenderThread renderThread = new SWFAnimationRenderThread(this, currMovie, file.getAbsolutePath());
                renderBtn.setDisable(true);
                cancelBtn.setDisable(false);
                new Thread(renderThread).start();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML private void onCancel(ActionEvent event) {
        // Logic to cancel render thread would require keeping a reference to it
        // For now, just a placeholder
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
        if (ownerFrame == null) return null;
        switch (currMovie.getSequenceOutputType()) {
            case ANB:
                return FileDialogTools.selectAnbFileForSave(ownerFrame, ownerFrame);
            case MP4:
                return FileDialogTools.selectMp4FileForSave(ownerFrame, ownerFrame);
            case PNG_IMAGES:
                return FileDialogTools.selectImageFileForSave(ownerFrame, ownerFrame, Tools.FILEEXT_PNG);
            default:
                return FileDialogTools.selectFlameSequenceFileForSave(ownerFrame, ownerFrame);
        }
    }

    @FXML private void onAddFlameFromEditor(ActionEvent event) {
        if (tinaController != null) {
            try {
                Flame flame = tinaController.exportFlame();
                if (flame != null) {
                    addFlame(flame);
                }
            } catch (Exception ex) {
                ex.printStackTrace(); // TODO: Show alert
            }
        }
    }

    private void addFlame(Flame flame) {
        FlameMoviePart part = new FlameMoviePart();
        part.setFlame(flame);

        int frameCount = 120; // Default
        int frameMorphCount = 60; // Default
        if (!currMovie.getParts().isEmpty()) {
            FlameMoviePart prev = currMovie.getParts().get(currMovie.getParts().size() - 1);
            frameCount = prev.getFrameCount();
            frameMorphCount = prev.getFrameMorphCount();
        }
        part.setFrameCount(frameCount);
        part.setFrameMorphCount(frameMorphCount);

        currMovie.addPart(part);
        refreshUI();
    }

    private void refreshUI() {
        flamesListView.getItems().clear();
        for (FlameMoviePart part : currMovie.getParts()) {
            flamesListView.getItems().add("Flame Part: " + part.getFrameCount() + " frames");
        }
        // TODO: Update other UI elements (total frames, etc.)
    }

    @FXML private void onAddFlameFromClipboard(ActionEvent event) {
        // TODO
    }

    @FXML private void onAddFlameFromDisc(ActionEvent event) {
        // TODO
    }

    @FXML private void onMoveFlameUp(ActionEvent event) {
        // TODO
    }

    @FXML private void onMoveFlameDown(ActionEvent event) {
        // TODO
    }

    @FXML private void onRemoveFlame(ActionEvent event) {
        // TODO
    }

    @FXML private void onRemoveAllFlames(ActionEvent event) {
        // TODO
    }

    @FXML private void onPlay(ActionEvent event) {
        // TODO
    }

    @FXML private void onCopyToEditor(ActionEvent event) {
        // TODO
    }

    @FXML private void onRenderPreview(ActionEvent event) {
        // TODO
    }

    public org.jwildfire.create.tina.base.Flame getFlame() {
        // Logic to get current flame at timeline position
        // For now return null if empty
        if (currMovie == null || currMovie.getParts().isEmpty()) {
            return null;
        }
        // TODO: Implement proper extraction
        return currMovie.getParts().get(0).getFlame();
    }

    public void importFlameFromEditor(org.jwildfire.create.tina.base.Flame flame) {
        addFlame(flame);
    }
}
