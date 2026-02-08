package org.jwildfire.visualizer;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.jwildfire.swing.JWildfire;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class MusicVisualizerInternalFrame extends JInternalFrame {
    private final JWildfire desktop;
    private MusicVisualizerController controller;

    public MusicVisualizerInternalFrame(JWildfire desktop) {
        super("Music Visualizer", true, true, true, true);
        this.desktop = desktop;
        initUI();
    }

    private void initUI() {
        setSize(800, 600);
        setLayout(new BorderLayout());

        JFXPanel jfxPanel = new JFXPanel();
        add(jfxPanel, BorderLayout.CENTER);

        Platform.runLater(() -> {
            try {
                URL fxmlUrl = getClass().getResource("music_visualizer.fxml");
                if (fxmlUrl == null) {
                    throw new IOException("Cannot find music_visualizer.fxml");
                }
                FXMLLoader loader = new FXMLLoader(fxmlUrl);
                Parent root = loader.load();

                controller = loader.getController();

                Scene scene = new Scene(root);
                jfxPanel.setScene(scene);
            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error initializing JavaFX UI: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        });
    }
}
