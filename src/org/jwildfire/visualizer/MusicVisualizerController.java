package org.jwildfire.visualizer;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.StringConverter;
import org.jwildfire.visualizer.projectm.ProjectMVisualizer;

import javax.sound.sampled.Mixer;
import javax.swing.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MusicVisualizerController implements Initializable {
    @FXML private Button startStopBtn;
    @FXML private ComboBox<Mixer.Info> deviceCombo;
    @FXML private Slider sensitivitySlider;
    @FXML private Slider gainSlider;
    @FXML private Canvas canvas;
    @FXML private Label statusLabel;
    @FXML private ComboBox<String> rayShaderCombo;
    @FXML private WebView helpWebView;

    private final AudioCapture audioCapture;
    private AnimationTimer visualizerLoop;

    public MusicVisualizerController() {
        this.audioCapture = new AudioCapture();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Populate devices
        List<Mixer.Info> devices = audioCapture.getCaptureDevices();
        deviceCombo.setItems(FXCollections.observableArrayList(devices));

        // Custom converter to show device name
        deviceCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Mixer.Info object) {
                return object == null ? "Default Device" : object.getName();
            }

            @Override
            public Mixer.Info fromString(String string) {
                return null; // Not needed
            }
        });

        if (!devices.isEmpty()) {
            deviceCombo.getSelectionModel().select(0);
        }

        // Raymarching options
        rayShaderCombo.setItems(FXCollections.observableArrayList("Mandelbulb", "Sphere"));
        rayShaderCombo.getSelectionModel().select(0);

        // Sliders
        sensitivitySlider.valueProperty().addListener((obs, oldVal, newVal) ->
            audioCapture.setSensitivity(newVal.floatValue()));
        gainSlider.valueProperty().addListener((obs, oldVal, newVal) ->
            audioCapture.setGain(newVal.floatValue()));

        // Help
        WebEngine webEngine = helpWebView.getEngine();
        URL helpUrl = getClass().getResource("help.html");
        if (helpUrl != null) {
            webEngine.load(helpUrl.toExternalForm());
        }

        // Visualizer Loop
        visualizerLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                drawVisualization();
            }
        };
    }

    @FXML
    private void toggleStartStop() {
        if (audioCapture.isCapturing()) {
            stopCapture();
        } else {
            startCapture();
        }
    }

    private void startCapture() {
        try {
            Mixer.Info selected = deviceCombo.getSelectionModel().getSelectedItem();
            audioCapture.start(selected);
            startStopBtn.setText("Stop Capture");
            statusLabel.setText("Capturing...");
            visualizerLoop.start();
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    private void stopCapture() {
        audioCapture.stop();
        visualizerLoop.stop();
        startStopBtn.setText("Start Capture");
        statusLabel.setText("Stopped.");

        // Clear canvas
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void drawVisualization() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        // Clear background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);

        float[] spectrum = audioCapture.getSpectrumData();
        if (spectrum == null) return;

        int bands = spectrum.length; // usually 512
        // Limit bands to display (too high freq is often noise)
        int displayBands = Math.min(bands, 256);
        double barWidth = width / displayBands;

        gc.setFill(Color.LIGHTBLUE);
        for (int i = 0; i < displayBands; i++) {
            float val = spectrum[i];
            // Logarithmic scaling or just linear for now
            double barHeight = Math.min(val * 100, height);

            // Color gradient based on intensity
            if (barHeight > height * 0.8) gc.setFill(Color.RED);
            else if (barHeight > height * 0.5) gc.setFill(Color.ORANGE);
            else gc.setFill(Color.LIGHTBLUE);

            gc.fillRect(i * barWidth, height - barHeight, barWidth - 1, barHeight);
        }

        // Draw waveform overlay (optional)
        float[] pcm = audioCapture.getPcmData();
        if (pcm != null) {
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(1.0);
            gc.beginPath();
            double xStep = width / pcm.length;
            for (int i = 0; i < pcm.length; i++) {
                double x = i * xStep;
                double y = (height / 2) + (pcm[i] * height / 2); // Center at mid-height
                if (i == 0) gc.moveTo(x, y);
                else gc.lineTo(x, y);
            }
            gc.stroke();
        }
    }

    @FXML
    private void launchOpenGL() {
        launchVisualizer(new SimpleGLVisualizer());
    }

    @FXML
    private void launchProjectM() {
        launchVisualizer(new ProjectMVisualizer());
    }

    @FXML
    private void launchRaymarching() {
        RaymarchingVisualizer viz = new RaymarchingVisualizer();
        String selected = rayShaderCombo.getSelectionModel().getSelectedItem();
        if ("Sphere".equals(selected)) {
            viz.setFragmentShader(RaymarchingVisualizer.SHADER_SPHERE);
        } else {
            viz.setFragmentShader(RaymarchingVisualizer.SHADER_MANDELBULB);
        }
        launchVisualizer(viz);
    }

    private void launchVisualizer(Visualizer viz) {
        // Ensure audio is running (start if not)
        if (!audioCapture.isCapturing()) {
            startCapture();
        }

        if (audioCapture.isCapturing()) {
            // Launch in a new thread
            Thread t = new Thread(new GLFWVisualizerRunner(viz, audioCapture));
            t.start();
            statusLabel.setText("Launched external visualizer.");
        }
    }
}
