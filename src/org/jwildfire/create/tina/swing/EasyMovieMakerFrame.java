package org.jwildfire.create.tina.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jwildfire.base.Prefs;
import org.jwildfire.swing.JWildfire;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class EasyMovieMakerFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private TinaController tinaController;
    private EasyMovieMakerController controller;

    public EasyMovieMakerFrame() {
        super();
        initialize();
    }

    private void initialize() {
        this.setSize(1220, 700);
        this.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
        this.setLocation(new Point(JWildfire.DEFAULT_WINDOW_LEFT, JWildfire.DEFAULT_WINDOW_TOP + 80));
        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        this.setTitle("Easy movie maker");
        this.setVisible(false);
        this.setResizable(true);

        JFXPanel jfxPanel = new JFXPanel();
        this.setContentPane(jfxPanel);

        Platform.runLater(() -> {
            try {
                URL fxmlUrl = getClass().getResource("/org/jwildfire/create/tina/swing/easy_movie_maker.fxml");
                if (fxmlUrl == null) {
                    throw new RuntimeException("Cannot find easy_movie_maker.fxml");
                }
                FXMLLoader loader = new FXMLLoader(fxmlUrl);
                Parent root = loader.load();

                controller = loader.getController();
                controller.setOwnerFrame(this);
                if (tinaController != null) {
                    controller.setTinaController(tinaController);
                    tinaController.setSwfAnimatorCtrl(controller);
                }

                Scene scene = new Scene(root);
                jfxPanel.setScene(scene);
            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error initializing JavaFX Easy Movie Maker: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        });
    }

    public void setTinaController(TinaController tinaController) {
        this.tinaController = tinaController;
        if (controller != null) {
            Platform.runLater(() -> {
                controller.setTinaController(tinaController);
                tinaController.setSwfAnimatorCtrl(controller);
            });
        }
    }

    public EasyMovieMakerController getController() {
        return controller;
    }
}
