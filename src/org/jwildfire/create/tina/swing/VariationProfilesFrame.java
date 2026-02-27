package org.jwildfire.create.tina.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jwildfire.base.Prefs;
import org.jwildfire.swing.JWildfire;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class VariationProfilesFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private TinaController tinaController;
    private VariationProfilesControllerFX controller;

    public VariationProfilesFrame(TinaController tinaController) {
        super();
        this.tinaController = tinaController;
        initialize();
    }

    private void initialize() {
        this.setSize(1000, 650);
        this.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
        this.setLocation(new Point(200 + JWildfire.DEFAULT_WINDOW_LEFT, 50 + JWildfire.DEFAULT_WINDOW_TOP));
        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        this.setTitle("Variation profiles");
        this.setVisible(false);
        this.setResizable(true);

        JFXPanel jfxPanel = new JFXPanel();
        this.setContentPane(jfxPanel);

        Platform.runLater(() -> {
            try {
                URL fxmlUrl = getClass().getResource("/variation_profiles.fxml");
                if (fxmlUrl == null) {
                    throw new RuntimeException("Cannot find variation_profiles.fxml");
                }
                FXMLLoader loader = new FXMLLoader(fxmlUrl);
                Parent root = loader.load();

                controller = loader.getController();
                if (tinaController != null) {
                    controller.setTinaController(tinaController);
                }

                Scene scene = new Scene(root);
                jfxPanel.setScene(scene);
            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error initializing JavaFX Variation Profiles: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        });
    }

    public void setTinaController(TinaController tinaController) {
        this.tinaController = tinaController;
        if (controller != null) {
            controller.setTinaController(tinaController);
        }
    }

    // Dummy getters for legacy controller
    public javax.swing.JButton getNewProfileBtn() { return new javax.swing.JButton(); }
    public javax.swing.JButton getDuplicateProfileBtn() { return new javax.swing.JButton(); }
    public javax.swing.JButton getDeleteProfileBtn() { return new javax.swing.JButton(); }
    public javax.swing.JTable getProfilesTable() { return new javax.swing.JTable(); }
    public javax.swing.JTextField getProfileNameEdit() { return new javax.swing.JTextField(); }
    public javax.swing.JComboBox getProfileTypeCmb() { return new javax.swing.JComboBox(); }
    public javax.swing.JTextField getProfileStatusEdit() { return new javax.swing.JTextField(); }
    public javax.swing.JCheckBox getDefaultCheckbox() { return new javax.swing.JCheckBox(); }
    public javax.swing.JScrollPane getVariationsScrollPane() { return new javax.swing.JScrollPane(); }
}
