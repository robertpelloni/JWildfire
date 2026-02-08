package org.jwildfire.sheep;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.FlameReader;
import org.jwildfire.create.tina.swing.TinaController;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ElectricSheepController implements Initializable {
    @FXML private ListView<String> sheepList;
    @FXML private Label statusLabel;
    @FXML private ImageView previewImage;
    @FXML private ProgressBar progressBar;
    @FXML private Button downloadBtn;
    @FXML private Button renderBtn;
    @FXML private Button editBtn;

    @FXML private TextField nicknameField;
    @FXML private TextField serverUrlField;
    @FXML private TextField cacheDirField;
    @FXML private CheckBox highQualityCheck;

    @FXML private WebView helpWebView;

    private TinaController tinaController;
    private final SheepDownloader downloader;
    private final SheepRenderer renderer;

    public ElectricSheepController() {
        this.downloader = new SheepDownloader();
        this.renderer = new SheepRenderer(); // Headless/Image mode
    }

    public void setTinaController(TinaController tinaController) {
        this.tinaController = tinaController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load Settings
        nicknameField.setText(Prefs.getPrefs().get("electric_sheep.nickname", "jwildfire_user"));
        serverUrlField.setText(Prefs.getPrefs().get("electric_sheep.url", "https://community.sheepserver.net/query.php"));
        cacheDirField.setText(Prefs.getPrefs().get("electric_sheep.cache_dir", System.getProperty("java.io.tmpdir")));
        highQualityCheck.setSelected(Prefs.getPrefs().get("electric_sheep.high_quality", "false").equals("true"));

        // Apply settings to downloader
        downloader.setConfig(nicknameField.getText(), serverUrlField.getText());

        // Setup List
        sheepList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean selected = newVal != null;
            downloadBtn.setDisable(!selected);
            renderBtn.setDisable(!selected);
            editBtn.setDisable(!selected); // Can check file existence too
        });

        // Load Help
        WebEngine webEngine = helpWebView.getEngine();
        URL helpUrl = getClass().getResource("help.html");
        if (helpUrl != null) {
            webEngine.load(helpUrl.toExternalForm());
        } else {
            webEngine.loadContent("<html><body><h1>Help file not found</h1></body></html>");
        }

        refreshSheepList();
    }

    @FXML
    private void refreshSheepList() {
        statusLabel.setText("Fetching list...");
        Task<List<String>> task = new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                return downloader.listAvailableSheep();
            }
        };

        task.setOnSucceeded(e -> {
            List<String> result = task.getValue();
            ObservableList<String> items = FXCollections.observableArrayList(result);
            sheepList.setItems(items);
            statusLabel.setText("Found " + result.size() + " sheep.");
        });

        task.setOnFailed(e -> {
            statusLabel.setText("Error fetching list.");
            Throwable ex = task.getException();
            if (ex != null) ex.printStackTrace();
        });

        new Thread(task).start();
    }

    @FXML
    private void downloadSelectedSheep() {
        String selected = sheepList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        String id = parseId(selected);
        String filename = id.equals("RENDER_JOB") ? "render_job.flame" : id + ".xml";
        String path = new File(cacheDirField.getText(), filename).getAbsolutePath();

        statusLabel.setText("Downloading " + id + "...");
        progressBar.setVisible(true);
        progressBar.setProgress(-1); // Indeterminate

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                downloader.downloadSheep(id, path);
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            progressBar.setVisible(false);
            statusLabel.setText("Downloaded to " + path);
        });

        task.setOnFailed(e -> {
            progressBar.setVisible(false);
            statusLabel.setText("Download failed: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }

    @FXML
    private void renderSelectedSheep() {
        String selected = sheepList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        String id = parseId(selected);
        String filename = id.equals("RENDER_JOB") ? "render_job.flame" : id + ".xml";
        String path = new File(cacheDirField.getText(), filename).getAbsolutePath();

        if (!new File(path).exists()) {
            statusLabel.setText("Please download first.");
            return;
        }

        statusLabel.setText("Rendering preview...");
        progressBar.setVisible(true);
        progressBar.setProgress(-1);

        Task<BufferedImage> task = new Task<>() {
            @Override
            protected BufferedImage call() throws Exception {
                return renderer.renderSheepToImage(path);
            }
        };

        task.setOnSucceeded(e -> {
            progressBar.setVisible(false);
            BufferedImage img = task.getValue();
            if (img != null) {
                previewImage.setImage(SwingFXUtils.toFXImage(img, null));
                statusLabel.setText("Render complete.");
            } else {
                statusLabel.setText("Render failed or GPU unavailable.");
            }
        });

        task.setOnFailed(e -> {
            progressBar.setVisible(false);
            statusLabel.setText("Render error: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }

    @FXML
    private void editSelectedSheep() {
        if (tinaController == null) {
            statusLabel.setText("Editor not linked.");
            return;
        }

        String selected = sheepList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        String id = parseId(selected);
        String filename = id.equals("RENDER_JOB") ? "render_job.flame" : id + ".xml";
        String path = new File(cacheDirField.getText(), filename).getAbsolutePath();

        if (!new File(path).exists()) {
            statusLabel.setText("Please download first.");
            return;
        }

        try {
            FlameReader reader = new FlameReader(Prefs.getPrefs());
            List<Flame> flames = reader.readFlames(path);
            if (!flames.isEmpty()) {
                Flame flame = flames.get(0);
                tinaController.setCurrFlame(flame);
                statusLabel.setText("Loaded into JWildfire Editor.");
                // Bring main window to front if possible
            }
        } catch (Exception ex) {
            statusLabel.setText("Error loading flame.");
            ex.printStackTrace();
        }
    }

    @FXML
    private void browseCacheDir() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Cache Directory");
        File defaultDir = new File(cacheDirField.getText());
        if (defaultDir.exists()) chooser.setInitialDirectory(defaultDir);

        File selected = chooser.showDialog(null);
        if (selected != null) {
            cacheDirField.setText(selected.getAbsolutePath());
        }
    }

    @FXML
    private void saveSettings() {
        Prefs.getPrefs().put("electric_sheep.nickname", nicknameField.getText());
        Prefs.getPrefs().put("electric_sheep.url", serverUrlField.getText());
        Prefs.getPrefs().put("electric_sheep.cache_dir", cacheDirField.getText());
        Prefs.getPrefs().put("electric_sheep.high_quality", highQualityCheck.isSelected() ? "true" : "false");

        downloader.setConfig(nicknameField.getText(), serverUrlField.getText());
        statusLabel.setText("Settings saved.");
    }

    private String parseId(String displayString) {
        if (displayString.startsWith("RENDER_JOB")) return "RENDER_JOB";
        if (displayString.startsWith("Sheep ")) {
            int start = 6;
            int end = displayString.indexOf(" ", start);
            if (end > 0) {
                return displayString.substring(start, end);
            }
        }
        return displayString;
    }
}
