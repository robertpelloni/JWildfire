package org.jwildfire.base;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Proof of concept for Hybrid Swing/JavaFX architecture.
 * This class creates a Swing JFrame and embeds a JavaFX Scene using JFXPanel.
 */
public class HybridUISample extends JFrame {

    private final JFXPanel jfxPanel;

    public HybridUISample() {
        super("JWildfire Hybrid UI Sample");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Create the JFXPanel
        jfxPanel = new JFXPanel();
        add(jfxPanel, BorderLayout.CENTER);

        // Initialize JavaFX content
        Platform.runLater(this::initFX);
    }

    private void initFX() {
        try {
            // Load the FXML file
            URL resource = getClass().getResource("sample.fxml");
            if (resource == null) {
                throw new RuntimeException("sample.fxml not found");
            }
            Parent root = FXMLLoader.load(resource);
            
            // Create the Scene
            Scene scene = new Scene(root);
            
            // Set the Scene to the JFXPanel
            jfxPanel.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HybridUISample().setVisible(true);
        });
    }
}
