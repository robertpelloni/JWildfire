package org.jwildfire.create.tina.swing;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.audio.JLayerInterface;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.dance.DancingFlameProject;
import org.jwildfire.create.tina.dance.DancingFlamesUI;
import org.jwildfire.create.tina.dance.RealtimeAnimRenderThread;
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
import org.jwildfire.image.SimpleImage;

import javafx.application.Platform;
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
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class DancingFlamesController implements Initializable, DancingFlamesUI {

    @FXML private Button loadProjectBtn;
    @FXML private Button saveProjectBtn;
    @FXML private ComboBox<String> randomGenCmb;
    @FXML private TextField randomCountField;
    @FXML private Button genRandomFlamesBtn;
    @FXML private TreeView<String> flamePropertiesTree;
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
    @FXML private TableView<?> motionTable;
    @FXML private ComboBox<String> addMotionCmb;
    @FXML private ComboBox<String> createMotionsCmb;
    @FXML private TableView<?> motionLinksTable;

    private TinaController tinaController;
    private DancingFlameProject project;
    private Prefs prefs;
    private RealtimeAnimRenderThread renderThread;
    private JLayerInterface soundPlayer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prefs = Prefs.getPrefs();
        project = new DancingFlameProject();
        soundPlayer = new JLayerInterface();

        randomGenCmb.getItems().addAll(RandomFlameGeneratorList.getNameList());
        randomGenCmb.getSelectionModel().select(RandomFlameGeneratorList.DEFAULT_GENERATOR_NAME);

        flamePropertiesTree.setRoot(new TreeItem<>("Project Flames"));
        flamePropertiesTree.setShowRoot(false);

        flamePropertiesTree.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.getValue() != null) {
                // Find flame by name (simplistic approach, ideally store Flame in TreeItem)
                // But TreeView<String> was used in my field definition. I'll stick to it for now or refactor.
                // Refactoring to TreeView<Object> or TreeView<FlameWrapper> would be better.
                // For now, assume leaf nodes are flames.
                refreshPoolPreview(findFlameByName(newVal.getValue()));
            }
        });
    }

    public void setTinaController(TinaController tinaController) {
        this.tinaController = tinaController;
    }

    @FXML private void onLoadProject(ActionEvent event) {
        if (tinaController != null && tinaController.getMainEditorFrame() != null) {
            File file = FileDialogTools.selectJWFDanceFileForOpen(tinaController.getMainEditorFrame(), null);
            if (file != null) {
                try {
                    project = new JWFDanceReader().readProject(file.getAbsolutePath());
                    refreshProjectFlames();
                } catch (Exception e) {
                    showError("Error loading project", e);
                }
            }
        }
    }

    @FXML private void onSaveProject(ActionEvent event) {
        if (tinaController != null && tinaController.getMainEditorFrame() != null) {
            File file = FileDialogTools.selectJWFDanceFileForSave(tinaController.getMainEditorFrame(), null);
            if (file != null) {
                try {
                    new JWFDanceWriter().writeProject(project, file.getAbsolutePath());
                    prefs.setLastOutputJWFMovieFile(file);
                } catch (Exception e) {
                    showError("Error saving project", e);
                }
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
        flamePropertiesTree.getRoot().getChildren().clear();
        projectFlameCmb.getItems().clear();

        for (Flame flame : project.getFlames()) {
            TreeItem<String> item = new TreeItem<>(flame.getName() != null ? flame.getName() : "Untitled Flame");
            flamePropertiesTree.getRoot().getChildren().add(item);
            projectFlameCmb.getItems().add(flame.getName());
        }
        if (!project.getFlames().isEmpty()) {
            projectFlameCmb.getSelectionModel().select(0);
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
        // Render thumbnail logic here
        // ... (Similar to refreshFlameImage but for pool view)
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
        if (flame != null) {
            int width = (int) Math.max(previewPane.getWidth(), 320);
            int height = (int) Math.max(previewPane.getHeight(), 240);

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
                    previewImageView.setImage(SwingFXUtils.toFXImage(img.getBufferedImg(), null));
                    if (drawFPS) {
                        // Overlay FPS logic if needed or just update label
                    }
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
        // Implementation for clipboard import would go here
    }

    @FXML private void onAddFromDisc(ActionEvent event) {
        if (tinaController == null) return;
        File file = FileDialogTools.selectFlameFileForOpen(tinaController.getMainEditorFrame(), null, null);
        if (file != null) {
             // Load flame logic
        }
    }

    @FXML private void onToEditor(ActionEvent event) {
        TreeItem<String> selected = flamePropertiesTree.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Flame flame = findFlameByName(selected.getValue());
            if (flame != null && tinaController != null) {
                tinaController.importFlame(flame, true);
            }
        }
    }

    @FXML private void onReplaceFromEditor(ActionEvent event) {
        TreeItem<String> selected = flamePropertiesTree.getSelectionModel().getSelectedItem();
        if (selected != null && tinaController != null) {
            Flame newFlame = tinaController.exportFlame();
            if (newFlame != null) {
                int index = project.getFlames().indexOf(findFlameByName(selected.getValue()));
                if (index >= 0) {
                    project.getFlames().set(index, validateDancingFlame(newFlame));
                    refreshProjectFlames();
                }
            }
        }
    }

    @FXML private void onRenameFlame(ActionEvent event) {
        TreeItem<String> selected = flamePropertiesTree.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Flame flame = findFlameByName(selected.getValue());
            if (flame != null) {
                TextInputDialog dialog = new TextInputDialog(flame.getName());
                dialog.setHeaderText("Rename Flame");
                dialog.showAndWait().ifPresent(name -> {
                    flame.setName(name);
                    refreshProjectFlames();
                });
            }
        }
    }

    @FXML private void onDeleteFlame(ActionEvent event) {
        TreeItem<String> selected = flamePropertiesTree.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Flame flame = findFlameByName(selected.getValue());
            if (flame != null) {
                project.getFlames().remove(flame);
                refreshProjectFlames();
            }
        }
    }

    @FXML private void onLoadSound(ActionEvent event) {
        if (tinaController == null) return;
        File file = FileDialogTools.selectSoundFileForOpen(tinaController.getMainEditorFrame(), null);
        if (file != null) {
            try {
                // JLayerInterface needs to be passed to project to record FFT
                // Note: The legacy code does this differently (passing JLayerInterface to setSoundFilename)
                // Let's assume we can just store the filename for now or use the helper
                 project.setSoundFilename(soundPlayer, file.getAbsolutePath());
            } catch (Exception e) {
                showError("Error loading sound", e);
            }
        }
    }

    @FXML private void onStartShow(ActionEvent event) {
        if (renderThread != null && renderThread.isRunning()) return;

        try {
            renderThread = new RealtimeAnimRenderThread(this, project);
            renderThread.setMusicPlayer(soundPlayer);
            // Configure render thread
            renderThread.setDrawTriangles(drawTrianglesCbx.isSelected());
            renderThread.setDrawFFT(drawFFTCbx.isSelected());
            renderThread.setDrawFPS(drawFPSCbx.isSelected());

            // Start audio if loaded
            if (project.getSoundFilename() != null) {
                soundPlayer.play(project.getSoundFilename());
            }

            new Thread(renderThread).start();
        } catch (Exception e) {
            showError("Error starting show", e);
        }
    }

    @FXML private void onStopShow(ActionEvent event) {
        if (renderThread != null) {
            renderThread.setForceAbort(true);
        }
        try {
            soundPlayer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void onAddMotion(ActionEvent event) {
        // TODO: Implement motion logic
    }

    @FXML private void onRenameMotion(ActionEvent event) {
        // TODO
    }

    @FXML private void onDeleteMotion(ActionEvent event) {
        // TODO
    }

    @FXML private void onCreateMotions(ActionEvent event) {
        // TODO
    }

    @FXML private void onClearMotions(ActionEvent event) {
        // TODO
    }

    @FXML private void onAddLink(ActionEvent event) {
        // TODO
    }

    @FXML private void onDeleteLink(ActionEvent event) {
        // TODO
    }

    private void showError(String header, Exception e) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText(header);
            if (e != null) alert.setContentText(e.getMessage());
            alert.showAndWait();
        });
    }
}
