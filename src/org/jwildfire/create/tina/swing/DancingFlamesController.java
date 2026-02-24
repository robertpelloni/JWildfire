package org.jwildfire.create.tina.swing;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.controlsfx.control.PropertySheet;
import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.base.fx.PropertySheetFactory;
import org.jwildfire.create.tina.audio.JLayerInterface;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.dance.DancingFlameProject;
import org.jwildfire.create.tina.dance.DancingFlamesUI;
import org.jwildfire.create.tina.dance.FlamePropertiesTreeServiceFX;
import org.jwildfire.create.tina.dance.FlamePropertyItem;
import org.jwildfire.create.tina.dance.RealtimeAnimRenderThread;
import org.jwildfire.create.tina.dance.action.ActionRecorder;
import org.jwildfire.create.tina.dance.action.PostRecordFlameGenerator;
import org.jwildfire.create.tina.dance.model.FlamePropertyPath;
import org.jwildfire.create.tina.dance.motion.Motion;
import org.jwildfire.create.tina.dance.motion.MotionCreator;
import org.jwildfire.create.tina.dance.motion.MotionCreatorType;
import org.jwildfire.create.tina.dance.motion.MotionLink;
import org.jwildfire.create.tina.dance.motion.MotionType;
import org.jwildfire.create.tina.io.FlameReader;
import org.jwildfire.create.tina.io.JWFDanceReader;
import org.jwildfire.create.tina.io.JWFDanceWriter;
import org.jwildfire.create.tina.randomflame.RandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorList;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorSampler;
import org.jwildfire.create.tina.randomgradient.RandomGradientGeneratorList;
import org.jwildfire.create.tina.randomsymmetry.RandomSymmetryGeneratorList;
import org.jwildfire.create.tina.randomweightingfield.RandomWeightingFieldGeneratorList;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.variation.Linear3DFunc;
import org.jwildfire.create.tina.dance.FlamePreparer;
import org.jwildfire.image.SimpleImage;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

public class DancingFlamesController implements Initializable, DancingFlamesUI {

    @FXML private Button loadProjectBtn;
    @FXML private Button saveProjectBtn;
    @FXML private ComboBox<String> randomGenCmb;
    @FXML private TextField randomCountField;
    @FXML private Button genRandomFlamesBtn;
    @FXML private TreeView<FlamePropertyItem> flamePropertiesTree;
    @FXML private StackPane poolFlamePreviewPane;
    @FXML private ImageView poolFlamePreviewView;
    @FXML private StackPane previewPane;
    @FXML private ImageView previewImageView;
    @FXML private StackPane graphPane;
    @FXML private TextField fpsField;
    @FXML private Slider borderSizeSlider;
    @FXML private CheckBox drawTrianglesCbx;
    @FXML private CheckBox drawFFTCbx;
    @FXML private CheckBox drawFPSCbx;
    @FXML private CheckBox mutedCbx;
    @FXML private ComboBox<String> projectFlameCmb;
    @FXML private TextField morphFramesField;
    @FXML private CheckBox recordShowCbx;
    @FXML private TableView<Motion> motionTable;
    @FXML private TableColumn<Motion, String> motionNameCol;
    @FXML private TableColumn<Motion, String> motionTypeCol;
    @FXML private ComboBox<MotionType> addMotionCmb;
    @FXML private ComboBox<MotionCreatorType> createMotionsCmb;
    @FXML private TableView<MotionLink> motionLinksTable;
    @FXML private TableColumn<MotionLink, String> linkPropertyCol;
    @FXML private TableColumn<MotionLink, String> linkFlameCol;
    @FXML private VBox motionPropertiesPane;

    private TinaController tinaController;
    private DancingFlameProject project;
    private Prefs prefs;
    private RealtimeAnimRenderThread renderThread;
    private ActionRecorder actionRecorder;
    private JLayerInterface soundPlayer;
    private FlamePropertiesTreeServiceFX treeService;
    private Canvas fftCanvas;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prefs = Prefs.getPrefs();
        project = new DancingFlameProject();
        soundPlayer = new JLayerInterface();
        treeService = new FlamePropertiesTreeServiceFX();

        randomGenCmb.getItems().addAll(RandomFlameGeneratorList.getNameList());
        randomGenCmb.getSelectionModel().select(RandomFlameGeneratorList.DEFAULT_GENERATOR_NAME);

        flamePropertiesTree.setRoot(new TreeItem<>(new FlamePropertyItem("Project Flames", null, false)));
        flamePropertiesTree.setShowRoot(false);

        flamePropertiesTree.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.getValue() != null) {
                FlamePropertyItem item = newVal.getValue();
                if (item.getData() instanceof Flame) {
                    refreshPoolPreview((Flame) item.getData());
                } else {
                    // Try to find parent flame
                    FlamePropertyPath path = treeService.getSelectedPropertyPath(newVal);
                    if (path != null) {
                        refreshPoolPreview(path.getFlame());
                    }
                }
            }
        });

        // Initialize Motion Table
        motionNameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDisplayLabel()));
        motionTypeCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getClass().getSimpleName().replace("Motion", "")));
        motionTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            refreshMotionProperties(n);
            refreshMotionLinksTable();
        });

        // Initialize Links Table
        linkPropertyCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getProperyPath().getPath()));
        linkFlameCol.setCellValueFactory(cell -> {
            Flame f = cell.getValue().getProperyPath().getFlame();
            return new SimpleStringProperty(f != null ? f.getName() : "?");
        });

        addMotionCmb.getItems().addAll(MotionType.values());
        addMotionCmb.getSelectionModel().selectFirst();

        createMotionsCmb.getItems().addAll(MotionCreatorType.values());
        createMotionsCmb.getSelectionModel().selectFirst();

        // Initialize FFT Canvas
        fftCanvas = new Canvas();
        graphPane.getChildren().add(fftCanvas);
        fftCanvas.widthProperty().bind(graphPane.widthProperty());
        fftCanvas.heightProperty().bind(graphPane.heightProperty());

        projectFlameCmb.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && renderThread != null && renderThread.isRunning()) {
                Flame flame = findFlameByName(newVal);
                if (flame != null) {
                    int morphFrames = 0;
                    try {
                        morphFrames = Integer.parseInt(morphFramesField.getText());
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                    renderThread.getFlameStack().addFlame(flame, morphFrames, project.getMotions(flame));
                    if (actionRecorder != null) {
                        actionRecorder.recordFlameChange(flame, morphFrames);
                    }
                }
            }
        });
    }

    public void setTinaController(TinaController tinaController) {
        this.tinaController = tinaController;
    }

    @FXML private void onLoadProject(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open JWildfire Dance Project");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JWildfire Dance Project (*.jwfdance)", "*.jwfdance"));
        File initialDir = new File(prefs.getInputJWFMoviePath());
        if (initialDir.exists() && initialDir.isDirectory()) fileChooser.setInitialDirectory(initialDir);

        File file = fileChooser.showOpenDialog(loadProjectBtn.getScene().getWindow());
        if (file != null) {
            prefs.setLastInputJWFMovieFile(file);
            try {
                project = new JWFDanceReader().readProject(file.getAbsolutePath());
                refreshProjectFlames();
                refreshMotionTable();
            } catch (Exception e) {
                showError("Error loading project", e);
            }
        }
    }

    @FXML private void onSaveProject(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save JWildfire Dance Project");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JWildfire Dance Project (*.jwfdance)", "*.jwfdance"));
        File initialDir = new File(prefs.getOutputJWFMoviePath());
        if (initialDir.exists() && initialDir.isDirectory()) fileChooser.setInitialDirectory(initialDir);

        File file = fileChooser.showSaveDialog(saveProjectBtn.getScene().getWindow());
        if (file != null) {
            if (!file.getName().toLowerCase().endsWith(".jwfdance")) {
                file = new File(file.getParentFile(), file.getName() + ".jwfdance");
            }
            try {
                new JWFDanceWriter().writeProject(project, file.getAbsolutePath());
                prefs.setLastOutputJWFMovieFile(file);
            } catch (Exception e) {
                showError("Error saving project", e);
            }
        }
    }

    @FXML private void onGenRandomFlames(ActionEvent event) {
        try {
            final int IMG_WIDTH = 80;
            final int IMG_HEIGHT = 60;
            int count = Integer.parseInt(randomCountField.getText());

            String generatorName = randomGenCmb.getValue();
            if (generatorName == null) generatorName = RandomFlameGeneratorList.DEFAULT_GENERATOR_NAME;

            RandomFlameGenerator randGen = RandomFlameGeneratorList.getRandomFlameGeneratorInstance(generatorName, true);

            for (int i = 0; i < count; i++) {
                int palettePoints = 3 + Tools.randomInt(68);
                boolean fadePaletteColors = Math.random() > 0.33;
                boolean uniformSize = Math.random() > 0.75;

                RandomFlameGeneratorSampler sampler = new RandomFlameGeneratorSampler(
                    IMG_WIDTH, IMG_HEIGHT, prefs, randGen,
                    RandomSymmetryGeneratorList.NONE,
                    RandomGradientGeneratorList.DEFAULT,
                    RandomWeightingFieldGeneratorList.NONE,
                    palettePoints, fadePaletteColors, uniformSize,
                    RandomBatchQuality.NORMAL
                );

                Flame flame = sampler.createSample().getFlame();
                project.getFlames().add(validateDancingFlame(flame));
            }
            refreshProjectFlames();
        } catch (Exception e) {
            showError("Error generating random flames", e);
        }
    }

    private void refreshProjectFlames() {
        treeService.refreshFlamePropertiesTree(flamePropertiesTree.getRoot(), project);
        projectFlameCmb.getItems().clear();
        for (Flame flame : project.getFlames()) {
            projectFlameCmb.getItems().add(flame.getName());
        }
        if (!project.getFlames().isEmpty()) {
            projectFlameCmb.getSelectionModel().select(0);
        }
    }

    private void refreshMotionTable() {
        motionTable.setItems(FXCollections.observableArrayList(project.getMotions()));
    }

    private void refreshMotionProperties(Motion motion) {
        motionPropertiesPane.getChildren().clear();
        if (motion != null) {
            PropertySheet propertySheet = new PropertySheet(PropertySheetFactory.createItems(motion));
            motionPropertiesPane.getChildren().add(propertySheet);
        }
    }

    private void refreshMotionLinksTable() {
        Motion motion = motionTable.getSelectionModel().getSelectedItem();
        if (motion != null) {
            motionLinksTable.setItems(FXCollections.observableArrayList(motion.getMotionLinks()));
        } else {
            motionLinksTable.setItems(FXCollections.emptyObservableList());
        }
    }

    private Flame findFlameByName(String name) {
        for (Flame f : project.getFlames()) {
            if (name.equals(f.getName()) || (f.getName() == null && "Untitled Flame".equals(name))) {
                return f;
            }
        }
        return null;
    }

    private void refreshPoolPreview(Flame flame) {
        if (flame == null) {
            poolFlamePreviewView.setImage(null);
            return;
        }
        refreshFlameImage(flame, false, 0, 0, false, poolFlamePreviewView);
    }

    private Flame validateDancingFlame(Flame pFlame) {
        for (Layer layer : pFlame.getLayers()) {
          if (layer.getFinalXForms().size() == 0) {
            XForm xForm = new XForm();
            xForm.addVariation(1.0, new Linear3DFunc());
            layer.getFinalXForms().add(xForm);
          }
        }
        return pFlame;
    }

    @Override
    public void refreshFlameImage(Flame flame, boolean drawTriangles, double fps, long frame, boolean drawFPS) {
        refreshFlameImage(flame, drawTriangles, fps, frame, drawFPS, previewImageView);
    }

    public void refreshFlameImage(Flame flame, boolean drawTriangles, double fps, long frame, boolean drawFPS, ImageView targetView) {
        if (flame != null && targetView != null) {
            // Use current size or default
            double w = targetView.getFitWidth();
            double h = targetView.getFitHeight();
            if (w <= 0) w = 320;
            if (h <= 0) h = 240;

            int width = (int) w;
            int height = (int) h;

            RenderInfo info = new RenderInfo(width, height, RenderMode.PREVIEW);

            double oldSpatialFilterRadius = flame.getSpatialFilterRadius();
            double oldSampleDensity = flame.getSampleDensity();
            double oldDeRadius = flame.getDeRadius();

            try {
                double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
                double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
                flame.setPixelsPerUnitScale((wScl + hScl) * 0.5);

                Flame renderFlame = new FlamePreparer(prefs).createRenderFlame(flame);
                FlameRenderer renderer = new FlameRenderer(renderFlame, prefs, false, false);
                renderer.setProgressUpdater(null);
                RenderedFlame res = renderer.renderFlame(info);
                SimpleImage img = res.getImage();

                Platform.runLater(() -> {
                    targetView.setImage(SwingFXUtils.toFXImage(img.getBufferedImg(), null));
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                flame.setSpatialFilterRadius(oldSpatialFilterRadius);
                flame.setSampleDensity(oldSampleDensity);
                flame.setDeRadius(oldDeRadius);
            }
        }
    }

    @FXML private void onAddFromEditor(ActionEvent event) {
        if (tinaController != null) {
            Flame flame = tinaController.exportFlame();
            if (flame != null) {
                project.getFlames().add(validateDancingFlame(flame));
                refreshProjectFlames();
            }
        }
    }

    @FXML private void onAddFromClipboard(ActionEvent event) {
        try {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            if (clipboard.hasString()) {
                String xml = clipboard.getString();
                List<Flame> newFlames = new FlameReader(prefs).readFlamesfromXML(xml);
                if (newFlames != null && !newFlames.isEmpty()) {
                    for (Flame flame : newFlames) {
                        project.getFlames().add(validateDancingFlame(flame));
                    }
                    refreshProjectFlames();
                } else {
                    showError("No valid flame found in clipboard", null);
                }
            }
        } catch (Exception e) {
            showError("Error importing from clipboard", e);
        }
    }

    @FXML private void onAddFromDisc(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Flame");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Flame files (*.flame)", "*.flame"));
        File initialDir = new File(prefs.getInputFlamePath());
        if (initialDir.exists() && initialDir.isDirectory()) fileChooser.setInitialDirectory(initialDir);

        List<File> files = fileChooser.showOpenMultipleDialog(loadProjectBtn.getScene().getWindow());
        if (files != null && !files.isEmpty()) {
            prefs.setLastInputFlameFile(files.get(0));
            try {
                for (File file : files) {
                    List<Flame> newFlames = new FlameReader(prefs).readFlames(file.getAbsolutePath());
                    if (newFlames != null) {
                        for (Flame flame : newFlames) {
                            project.getFlames().add(validateDancingFlame(flame));
                        }
                    }
                }
                refreshProjectFlames();
            } catch (Exception e) {
                showError("Error loading flame", e);
            }
        }
    }

    @FXML private void onToEditor(ActionEvent event) {
        TreeItem<FlamePropertyItem> selected = flamePropertiesTree.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getValue().getData() instanceof Flame) {
            Flame flame = (Flame) selected.getValue().getData();
            if (tinaController != null) {
                tinaController.importFlame(flame, true);
            }
        }
    }

    @FXML private void onReplaceFromEditor(ActionEvent event) {
        TreeItem<FlamePropertyItem> selected = flamePropertiesTree.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getValue().getData() instanceof Flame && tinaController != null) {
            Flame oldFlame = (Flame) selected.getValue().getData();
            Flame newFlame = tinaController.exportFlame();
            if (newFlame != null) {
                int index = project.getFlames().indexOf(oldFlame);
                if (index >= 0) {
                    project.getFlames().set(index, validateDancingFlame(newFlame));
                    refreshProjectFlames();
                }
            }
        }
    }

    @FXML private void onRenameFlame(ActionEvent event) {
        TreeItem<FlamePropertyItem> selected = flamePropertiesTree.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getValue().getData() instanceof Flame) {
            Flame flame = (Flame) selected.getValue().getData();
            TextInputDialog dialog = new TextInputDialog(flame.getName());
            dialog.setHeaderText("Rename Flame");
            dialog.showAndWait().ifPresent(name -> {
                flame.setName(name);
                refreshProjectFlames();
            });
        }
    }

    @FXML private void onDeleteFlame(ActionEvent event) {
        TreeItem<FlamePropertyItem> selected = flamePropertiesTree.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getValue().getData() instanceof Flame) {
            Flame flame = (Flame) selected.getValue().getData();
            project.getFlames().remove(flame);
            refreshProjectFlames();
        }
    }

    @FXML private void onLoadSound(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Sound File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 Files (*.mp3)", "*.mp3"));
        File initialDir = new File(prefs.getInputSoundFilePath());
        if (initialDir.exists() && initialDir.isDirectory()) fileChooser.setInitialDirectory(initialDir);

        File file = fileChooser.showOpenDialog(loadProjectBtn.getScene().getWindow());
        if (file != null) {
            prefs.setLastInputSoundFile(file);
            try {
                 project.setSoundFilename(soundPlayer, file.getAbsolutePath());
            } catch (Exception e) {
                showError("Error loading sound", e);
            }
        }
    }

    @FXML private void onStartShow(ActionEvent event) {
        if (renderThread != null && renderThread.isRunning()) return;

        try {
            Flame startFlame = null;
            String selName = projectFlameCmb.getValue();
            if (selName != null) {
                startFlame = findFlameByName(selName);
            }
            if (startFlame == null && !project.getFlames().isEmpty()) {
                startFlame = project.getFlames().get(0);
            }

            renderThread = new RealtimeAnimRenderThread(this, project);
            if (startFlame != null) {
                renderThread.getFlameStack().addFlame(startFlame, 0, project.getMotions(startFlame));
            }

            renderThread.setMusicPlayer(soundPlayer);
            renderThread.setFFTData(project.getFFT());
            // Configure render thread
            renderThread.setDrawTriangles(drawTrianglesCbx.isSelected());
            renderThread.setDrawFFT(drawFFTCbx.isSelected());
            renderThread.setDrawFPS(drawFPSCbx.isSelected());
            try {
                renderThread.setFramesPerSecond(Integer.parseInt(fpsField.getText()));
            } catch (Exception ex) {
                renderThread.setFramesPerSecond(12);
            }

            renderThread.setFFTVisualizer(fftData -> Platform.runLater(() -> drawFFT(fftData)));

            if (recordShowCbx.isSelected()) {
                actionRecorder = new ActionRecorder(renderThread);
                if (startFlame != null) {
                    actionRecorder.recordStart(startFlame);
                }
            } else {
                actionRecorder = null;
            }

            // Start audio if loaded
            if (project.getSoundFilename() != null) {
                soundPlayer.play(project.getSoundFilename());
            }

            new Thread(renderThread).start();
            enableControls(false);
        } catch (Exception e) {
            showError("Error starting show", e);
        }
    }

    @FXML private void onStopShow(ActionEvent event) {
        if (renderThread != null) {
            if (actionRecorder != null) {
                actionRecorder.recordStop();
            }
            renderThread.setForceAbort(true);

            if (actionRecorder != null) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Recorded Flame Sequence");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Flame files (*.flame)", "*.flame"));
                File initialDir = new File(prefs.getOutputFlamePath());
                if (initialDir.exists() && initialDir.isDirectory()) fileChooser.setInitialDirectory(initialDir);

                File file = fileChooser.showSaveDialog(saveProjectBtn.getScene().getWindow());
                if (file != null) {
                    try {
                        PostRecordFlameGenerator generator = new PostRecordFlameGenerator(
                            prefs, project, actionRecorder, renderThread, project.getFFT());
                        generator.createRecordedFlameFiles(file.getAbsolutePath());
                    } catch (Exception e) {
                        showError("Error saving recording", e);
                    }
                }
            }
            renderThread = null;
            actionRecorder = null;
        }
        try {
            soundPlayer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        enableControls(true);
    }

    private void enableControls(boolean enabled) {
        loadProjectBtn.setDisable(!enabled);
        saveProjectBtn.setDisable(!enabled);
        genRandomFlamesBtn.setDisable(!enabled);
        motionTable.setDisable(!enabled);
        // Add other controls as needed
    }

    @FXML private void onAddMotion(ActionEvent event) {
        MotionType type = addMotionCmb.getValue();
        if (type != null) {
            try {
                Motion motion = type.getMotionClass().newInstance();
                motion.setCaption("New " + type.name() + " Motion");
                project.getMotions().add(motion);
                refreshMotionTable();
                motionTable.getSelectionModel().select(motion);
            } catch (Exception e) {
                showError("Error adding motion", e);
            }
        }
    }

    @FXML private void onRenameMotion(ActionEvent event) {
        Motion motion = motionTable.getSelectionModel().getSelectedItem();
        if (motion != null) {
            TextInputDialog dialog = new TextInputDialog(motion.getDisplayLabel());
            dialog.setHeaderText("Rename Motion");
            dialog.showAndWait().ifPresent(name -> {
                motion.setCaption(name);
                refreshMotionTable();
            });
        }
    }

    @FXML private void onDeleteMotion(ActionEvent event) {
        Motion motion = motionTable.getSelectionModel().getSelectedItem();
        if (motion != null) {
            project.getMotions().remove(motion);
            refreshMotionTable();
        }
    }

    @FXML private void onCreateMotions(ActionEvent event) {
        MotionCreatorType type = createMotionsCmb.getValue();
        if (type != null) {
            try {
                MotionCreator creator = type.getMotionCreatorClass().newInstance();
                creator.createMotions(project);
                refreshMotionTable();
            } catch (Exception e) {
                showError("Error creating motions", e);
            }
        }
    }

    @FXML private void onClearMotions(ActionEvent event) {
        project.getMotions().clear();
        refreshMotionTable();
    }

    @FXML private void onAddLink(ActionEvent event) {
        Motion motion = motionTable.getSelectionModel().getSelectedItem();
        TreeItem<FlamePropertyItem> selectedProp = flamePropertiesTree.getSelectionModel().getSelectedItem();

        if (motion != null && selectedProp != null && treeService.isPlainPropertySelected(selectedProp)) {
            FlamePropertyPath path = treeService.getSelectedPropertyPath(selectedProp);
            if (path != null) {
                if (!motion.hasLink(path)) {
                    motion.getMotionLinks().add(new MotionLink(path));
                    refreshMotionLinksTable();
                } else {
                    showError("Link already exists", null);
                }
            }
        } else {
            showError("Please select a motion and a flame property", null);
        }
    }

    @FXML private void onDeleteLink(ActionEvent event) {
        Motion motion = motionTable.getSelectionModel().getSelectedItem();
        MotionLink link = motionLinksTable.getSelectionModel().getSelectedItem();
        if (motion != null && link != null) {
            motion.getMotionLinks().remove(link);
            refreshMotionLinksTable();
        }
    }

    private void showError(String header, Exception e) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText(header);
            if (e != null) alert.setContentText(e.getMessage());
            alert.showAndWait();
        });
    }

    private void drawFFT(short[] buffer) {
        if (fftCanvas == null || buffer == null) return;
        GraphicsContext gc = fftCanvas.getGraphicsContext2D();
        double width = fftCanvas.getWidth();
        double height = fftCanvas.getHeight();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);

        final double hScale = 1.75;
        double blockSize = width / (double)(buffer.length + 1);
        gc.setFill(Color.RED);

        for (int i = 0; i < buffer.length; i++) {
            short val = buffer[i];
            double dVal = (double) val / (double) Short.MAX_VALUE * height * hScale;
            if (dVal < 0) dVal = 0;
            else if (dVal >= height) dVal = height - 1;

            gc.fillRect(i * blockSize, height - 1 - dVal, blockSize, dVal);
        }
    }
}
