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

public class FlamesGPURenderFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private TinaController tinaController;
    private FlamesGPURenderControllerFX controller;

    public FlamesGPURenderFrame() {
        super();
        initialize();
    }

    public FlamesGPURenderFrame(TinaController tinaController) {
        super();
        this.tinaController = tinaController;
        initialize();
    }

    private void initialize() {
        this.setSize(1100, 700);
        this.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
        this.setLocation(new Point(200 + JWildfire.DEFAULT_WINDOW_LEFT, 50 + JWildfire.DEFAULT_WINDOW_TOP));
        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        this.setTitle("GPU renderer");
        this.setVisible(false);
        this.setResizable(true);

        JFXPanel jfxPanel = new JFXPanel();
        this.setContentPane(jfxPanel);

        Platform.runLater(() -> {
            try {
                URL fxmlUrl = getClass().getResource("/flames_gpu_render.fxml");
                if (fxmlUrl == null) {
                    throw new RuntimeException("Cannot find flames_gpu_render.fxml");
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
                    JOptionPane.showMessageDialog(this, "Error initializing JavaFX GPU Renderer: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    // Dummy components for legacy controller compatibility
    public javax.swing.JButton getInteractiveLoadFlameFromMainButton() { return new javax.swing.JButton(); }
    public javax.swing.JCheckBox getAiPostDenoiserDisableCheckbox() { return new javax.swing.JCheckBox(); }
    public javax.swing.JTextArea getFlameParamsTextArea() { return new javax.swing.JTextArea(); }
    public javax.swing.JCheckBox getAutoSyncCheckbox() { return new javax.swing.JCheckBox(); }
    public javax.swing.JCheckBox getAutoRenderCBx() { return new javax.swing.JCheckBox(); }

    public javax.swing.JButton getInteractiveLoadFlameButton() { return new javax.swing.JButton(); }
    public javax.swing.JButton getInteractiveLoadFlameFromClipboardButton() { return new javax.swing.JButton(); }
    public javax.swing.JButton getInteractiveFlameToClipboardButton() { return new javax.swing.JButton(); }
    public javax.swing.JButton getInteractiveSaveImageButton() { return new javax.swing.JButton(); }
    public javax.swing.JButton getInteractiveSaveFlameButton() { return new javax.swing.JButton(); }
    public javax.swing.JButton getInteractiveFlameToEditorButton() { return new javax.swing.JButton(); }

    public javax.swing.JPanel getInteractiveCenterTopPanel() { return new javax.swing.JPanel(); }
    public javax.swing.JTextArea getInteractiveStatsTextArea() { return new javax.swing.JTextArea(); }

    public javax.swing.JToggleButton getInteractiveHalveSizeButton() { return new javax.swing.JToggleButton(); }
    public javax.swing.JToggleButton getInteractiveQuarterSizeButton() { return new javax.swing.JToggleButton(); }
    public javax.swing.JToggleButton getInteractiveFullSizeButton() { return new javax.swing.JToggleButton(); }

    public javax.swing.JComboBox getInteractiveResolutionProfileCmb() { return new javax.swing.JComboBox(); }
    public javax.swing.JComboBox getInteractiveQualityProfileCmb() { return new javax.swing.JComboBox(); }

    public javax.swing.JLabel getLblGpuRenderInfo() { return new javax.swing.JLabel(); }
    public javax.swing.JPanel getProgressIndicatorPnl() { return new javax.swing.JPanel(); }
}
