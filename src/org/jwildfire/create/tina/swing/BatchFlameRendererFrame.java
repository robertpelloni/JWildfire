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

public class BatchFlameRendererFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private TinaController tinaController;
    private BatchRendererControllerFX controller;

    public BatchFlameRendererFrame() {
        super();
        initialize();
    }

    public BatchFlameRendererFrame(TinaController tinaController) {
        super();
        this.tinaController = tinaController;
        initialize();
    }

    private void initialize() {
        this.setSize(1100, 750);
        this.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
        this.setLocation(new Point(200 + JWildfire.DEFAULT_WINDOW_LEFT, 50 + JWildfire.DEFAULT_WINDOW_TOP));
        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        this.setTitle("Batch flame renderer");
        this.setVisible(false);
        this.setResizable(true);

        JFXPanel jfxPanel = new JFXPanel();
        this.setContentPane(jfxPanel);

        Platform.runLater(() -> {
            try {
                URL fxmlUrl = getClass().getResource("/batch_renderer.fxml");
                if (fxmlUrl == null) {
                    throw new RuntimeException("Cannot find batch_renderer.fxml");
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
                    JOptionPane.showMessageDialog(this, "Error initializing JavaFX Batch Renderer: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    public BatchRendererControllerFX getController() {
        return controller;
    }

    // Legacy support methods
    public javax.swing.JTable getRenderBatchJobsTable() { return new javax.swing.JTable(); }
    public javax.swing.JPanel getBatchPreviewRootPanel() { return new javax.swing.JPanel(); }
    public javax.swing.JProgressBar getBatchRenderTotalProgressBar() { return new javax.swing.JProgressBar(); }
    public javax.swing.JProgressBar getBatchRenderJobProgressBar() { return new javax.swing.JProgressBar(); } // Duplicate? Check below

    public javax.swing.JButton getBatchRenderAddFilesButton() { return new javax.swing.JButton(); }
    public javax.swing.JButton getBatchRenderFilesMoveDownButton() { return new javax.swing.JButton(); }
    public javax.swing.JButton getBatchRenderFilesMoveUpButton() { return new javax.swing.JButton(); }
    public javax.swing.JButton getBatchRenderFilesRemoveButton() { return new javax.swing.JButton(); }
    public javax.swing.JButton getBatchRenderFilesRemoveAllButton() { return new javax.swing.JButton(); }
    public javax.swing.JButton getBatchRenderStartButton() { return new javax.swing.JButton(); }
    public javax.swing.JButton getBatchRenderShowImageBtn() { return new javax.swing.JButton(); }

    public javax.swing.JComboBox getBatchQualityProfileCmb() { return new javax.swing.JComboBox(); }
    public javax.swing.JComboBox getBatchResolutionProfileCmb() { return new javax.swing.JComboBox(); }

    public javax.swing.JCheckBox getBatchRenderOverrideCBx() { return new javax.swing.JCheckBox(); }
    public javax.swing.JToggleButton getEnableOpenClBtn() { return new javax.swing.JToggleButton(); }
    public javax.swing.JToggleButton getDisablePostDenoiserBtn() { return new javax.swing.JToggleButton(); }

}
