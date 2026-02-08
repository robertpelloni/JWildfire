package org.jwildfire.sheep;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.swing.JWildfire;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class ElectricSheepFrame extends JFrame {
    private final JWildfire desktop;
    private TinaController tinaController;
    private ElectricSheepController controller;

    public ElectricSheepFrame() {
        super("Electric Sheep");
        this.desktop = JWildfire.getInstance();
        initUI();
    }

    public void setTinaController(TinaController tinaController) {
        this.tinaController = tinaController;
        if (controller != null) {
            controller.setTinaController(tinaController);
        }
    }

    private void initUI() {
        setSize(900, 600);
        setLayout(new BorderLayout());

        JFXPanel jfxPanel = new JFXPanel();
        add(jfxPanel, BorderLayout.CENTER);

        Platform.runLater(() -> {
            try {
                URL fxmlUrl = getClass().getResource("electric_sheep.fxml");
                if (fxmlUrl == null) {
                    throw new IOException("Cannot find electric_sheep.fxml");
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
                    JOptionPane.showMessageDialog(this, "Error initializing JavaFX UI: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        });
    }
}
