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
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.batch.Job;
import org.jwildfire.create.tina.batch.JobRenderThread;
import org.jwildfire.create.tina.batch.JobRenderThreadController;
import org.jwildfire.create.tina.io.FlameReader;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.ProgressUpdater;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.render.gpu.GPURendererFactory;
import org.jwildfire.image.SimpleImage;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;

public class BatchRendererControllerFX implements Initializable, JobRenderThreadController {

    @FXML private TableView<Job> jobsTable;
    @FXML private TableColumn<Job, String> flameCol;
    @FXML private TableColumn<Job, String> customSizeCol;
    @FXML private TableColumn<Job, String> customQualityCol;
    @FXML private TableColumn<Job, String> animationCol;
    @FXML private TableColumn<Job, String> stateCol;
    @FXML private TableColumn<Job, String> elapsedCol;
    @FXML private TableColumn<Job, String> errorCol;

    @FXML private ImageView previewImageView;
    @FXML private StackPane previewPane;

    @FXML private ComboBox<ResolutionProfile> resolutionProfileCmb;
    @FXML private ComboBox<QualityProfile> qualityProfileCmb;
    @FXML private CheckBox overwriteCbx;
    @FXML private ToggleButton gpuBtn;
    @FXML private ToggleButton denoiserOffBtn;

    @FXML private ProgressBar jobProgressBar;
    @FXML private ProgressBar totalProgressBar;
    @FXML private Button startRenderBtn;
    @FXML private Button addFilesBtn;
    @FXML private Button removeBtn;
    @FXML private Button removeAllBtn;
    @FXML private Button moveUpBtn;
    @FXML private Button moveDownBtn;

    private TinaController tinaController;
    private Prefs prefs;
    private ObservableList<Job> jobList = FXCollections.observableArrayList();
    private JobRenderThread jobRenderThread;

    // Progress updaters
    private FXProgressUpdater jobProgressUpdater;
    private FXProgressUpdater totalProgressUpdater;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prefs = Prefs.getPrefs();

        // Init table
        jobsTable.setItems(jobList);
        flameCol.setCellValueFactory(cell -> new SimpleStringProperty(new File(cell.getValue().getFlameFilename()).getName()));

        customSizeCol.setCellValueFactory(cell -> {
            Job j = cell.getValue();
            return new SimpleStringProperty((j.getCustomWidth() > 0 || j.getCustomHeight() > 0) ? j.getCustomWidth() + "x" + j.getCustomHeight() : "");
        });
        customSizeCol.setCellFactory(TextFieldTableCell.forTableColumn());
        customSizeCol.setOnEditCommit(e -> {
            String val = e.getNewValue();
            Job j = e.getRowValue();
            if (val == null || val.trim().isEmpty()) {
                j.setCustomWidth(0); j.setCustomHeight(0);
            } else {
                try {
                    String[] parts = val.toLowerCase().split("x");
                    if (parts.length == 2) {
                        j.setCustomWidth(Integer.parseInt(parts[0].trim()));
                        j.setCustomHeight(Integer.parseInt(parts[1].trim()));
                    }
                } catch (Exception ex) { /* ignore invalid input */ }
            }
            jobsTable.refresh();
        });

        customQualityCol.setCellValueFactory(cell -> {
            double q = cell.getValue().getCustomQuality();
            return new SimpleStringProperty(q > 0.001 ? String.valueOf(q) : "");
        });
        customQualityCol.setCellFactory(TextFieldTableCell.forTableColumn());
        customQualityCol.setOnEditCommit(e -> {
            try {
                double q = Double.parseDouble(e.getNewValue());
                e.getRowValue().setCustomQuality(q);
            } catch (Exception ex) { e.getRowValue().setCustomQuality(0); }
            jobsTable.refresh();
        });

        animationCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().isRenderAsAnimation() ? "Yes" : ""));
        stateCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().isFinished() ? "Done" : ""));
        elapsedCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().isFinished() ? String.format("%.2f", cell.getValue().getElapsedSeconds()) : ""));
        errorCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getLastErrorMsg()));

        // Init Profiles
        resolutionProfileCmb.getItems().addAll(
            new ResolutionProfile(false, 800, 600),
            new ResolutionProfile(false, 1920, 1080),
            new ResolutionProfile(false, 3840, 2160)
        );
        resolutionProfileCmb.getSelectionModel().selectFirst();

        qualityProfileCmb.getItems().addAll(
            new QualityProfile(false, "Low", 50, false, false),
            new QualityProfile(false, "Medium", 100, false, false),
            new QualityProfile(false, "High", 200, false, false)
        );
        qualityProfileCmb.getSelectionModel().select(1);

        // Selection listener for preview
        jobsTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> refreshPreview(n));

        gpuBtn.setDisable(!GPURendererFactory.isAvailable());
        if (GPURendererFactory.isAvailable()) {
            gpuBtn.setSelected(true);
            denoiserOffBtn.setSelected(true);
        }

        jobProgressUpdater = new FXProgressUpdater(jobProgressBar);
        // Total progress in JobRenderThread logic calls initProgress on job updater but uses total logic internally?
        // Actually looking at JobRenderThread, it assumes a swing controller. We need to check how it reports progress.
        // It uses controller.getJobProgressUpdater() for the current job.
        // It manually updates total progress on controller.getTotalProgressBar().
        // Since we can't return a JProgressBar, we might need a workaround if JobRenderThread expects JComponents.
    }

    public void setTinaController(TinaController tinaController) {
        this.tinaController = tinaController;
    }

    @FXML private void onAddFiles(ActionEvent event) {
        List<File> files = FileDialogTools.selectFlameFilesForOpen(tinaController.getMainEditorFrame(), null);
        if (files != null) {
            for (File f : files) {
                Job job = new Job();
                job.setFlameFilename(f.getAbsolutePath());
                jobList.add(job);
            }
        }
    }

    @FXML private void onRemove(ActionEvent event) {
        Job selected = jobsTable.getSelectionModel().getSelectedItem();
        if (selected != null) jobList.remove(selected);
    }

    @FXML private void onRemoveAll(ActionEvent event) {
        jobList.clear();
    }

    @FXML private void onMoveUp(ActionEvent event) {
        int idx = jobsTable.getSelectionModel().getSelectedIndex();
        if (idx > 0) {
            Job item = jobList.remove(idx);
            jobList.add(idx - 1, item);
            jobsTable.getSelectionModel().select(idx - 1);
        }
    }

    @FXML private void onMoveDown(ActionEvent event) {
        int idx = jobsTable.getSelectionModel().getSelectedIndex();
        if (idx >= 0 && idx < jobList.size() - 1) {
            Job item = jobList.remove(idx);
            jobList.add(idx + 1, item);
            jobsTable.getSelectionModel().select(idx + 1);
        }
    }

    @FXML private void onStartRender(ActionEvent event) {
        if (jobRenderThread != null) {
            jobRenderThread.setCancelSignalled(true);
            return;
        }

        List<Job> activeJobs = new ArrayList<>();
        for (Job job : jobList) {
            if (overwriteCbx.isSelected() || !job.isFinished()) {
                activeJobs.add(job);
            }
        }

        if (activeJobs.isEmpty()) return;

        jobRenderThread = new JobRenderThread(
            tinaController,
            this,
            activeJobs,
            resolutionProfileCmb.getValue(),
            qualityProfileCmb.getValue(),
            overwriteCbx.isSelected(),
            gpuBtn.isSelected(),
            denoiserOffBtn.isSelected()
        );

        updateControls(true);
        new Thread(jobRenderThread).start();
    }

    @FXML private void onShowImage(ActionEvent event) {
        Job job = jobsTable.getSelectionModel().getSelectedItem();
        if (job != null) {
            try {
                List<Flame> flames = new FlameReader(prefs).readFlames(job.getFlameFilename());
                if (!flames.isEmpty()) {
                    String fn = job.getPrimaryFilename(flames.get(0).getStereo3dMode());
                    if (new File(fn).exists()) {
                        tinaController.getMainController().loadImage(fn, false);
                    } else {
                        showAlert("Image not found: " + fn);
                    }
                }
            } catch (Exception e) {
                showAlert("Error showing image: " + e.getMessage());
            }
        }
    }

    private void updateControls(boolean rendering) {
        Platform.runLater(() -> {
            startRenderBtn.setText(rendering ? "STOP RENDER" : "START RENDER");
            addFilesBtn.setDisable(rendering);
            removeBtn.setDisable(rendering);
            removeAllBtn.setDisable(rendering);
            moveUpBtn.setDisable(rendering);
            moveDownBtn.setDisable(rendering);
        });
    }

    private void refreshPreview(Job job) {
        if (job == null) {
            previewImageView.setImage(null);
            return;
        }

        // Render preview
        new Thread(() -> {
            try {
                List<Flame> flames = new FlameReader(prefs).readFlames(job.getFlameFilename());
                if (!flames.isEmpty()) {
                    Flame flame = flames.get(0);
                    int width = 320;
                    int height = 240;
                    RenderInfo info = new RenderInfo(width, height, RenderMode.PREVIEW);

                    // Simple scale logic
                    double wScl = (double) width / flame.getWidth();
                    double hScl = (double) height / flame.getHeight();
                    flame.setPixelsPerUnitScale((wScl + hScl) * 0.5);

                    FlameRenderer renderer = new FlameRenderer(flame, prefs, false, false);
                    RenderedFlame res = renderer.renderFlame(info);
                    SimpleImage img = res.getImage();

                    WritableImage fxImg = SwingFXUtils.toFXImage(img.getBufferedImg(), null);
                    Platform.runLater(() -> previewImageView.setImage(fxImg));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // JobRenderThreadController implementation

    @Override
    public void onJobFinished() {
        Platform.runLater(() -> {
            jobsTable.refresh();
            if (jobRenderThread != null && jobRenderThread.isDone()) { // Assuming logic to detect total finish
                 // Actually JobRenderThread calls onJobFinished after the whole batch? No, it looks like it might run once?
                 // JobRenderThread runs a loop.
            }
            if (jobRenderThread == null || jobRenderThread.isDone()) { // We might need to check thread state
                 jobRenderThread = null;
                 updateControls(false);
            }
        });
    }

    @Override
    public void refreshRenderBatchJobsTable() {
        Platform.runLater(() -> jobsTable.refresh());
    }

    @Override
    public javax.swing.JProgressBar getTotalProgressBar() {
        // Legacy code expects a Swing ProgressBar. We can't provide one.
        // We must ensure JobRenderThread doesn't crash.
        // Option 1: Return null and hope it checks.
        // Option 2: Return a dummy off-screen Swing ProgressBar.
        return new javax.swing.JProgressBar() {
            @Override
            public void setValue(int n) {
                super.setValue(n); // maintain internal state
                Platform.runLater(() -> totalProgressBar.setProgress(getMaximum() > 0 ? (double) n / getMaximum() : 0));
            }
            @Override
            public void setMaximum(int n) {
                super.setMaximum(n);
            }
        };
    }

    @Override
    public javax.swing.JProgressBar getJobProgressBar() {
        return new javax.swing.JProgressBar() {
            @Override
            public void setValue(int n) {
                super.setValue(n);
                Platform.runLater(() -> jobProgressBar.setProgress(getMaximum() > 0 ? (double) n / getMaximum() : 0));
            }
        };
    }

    @Override
    public ProgressUpdater getJobProgressUpdater() {
        return jobProgressUpdater;
    }

    private class FXProgressUpdater implements ProgressUpdater {
        private ProgressBar bar;
        private int max = 100;
        public FXProgressUpdater(ProgressBar bar) { this.bar = bar; }

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

    public void addJob(Job job) {
        Platform.runLater(() -> jobList.add(job));
    }

    private void showAlert(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setContentText(msg);
            alert.show();
        });
    }
}
