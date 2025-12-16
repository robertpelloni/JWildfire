package org.jwildfire.swing;

import java.awt.BorderLayout;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.jwildfire.base.Prefs;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class PreferencesFrame extends JFrame {
  private static final long serialVersionUID = 1L;
  private JWildfire desktop = null;
  private MainController mainController = null;
  private Prefs prefs = null;
  private JFXPanel jfxPanel;
  private PreferencesController controller;

  public PreferencesFrame() {
    super();
    setResizable(true);
    initialize();
  }

  private void initialize() {
    this.setSize(800, 600);
    this.setLocation(200, 80);
    this.setTitle("Preferences (JavaFX)");
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    
    jfxPanel = new JFXPanel();
    this.getContentPane().add(jfxPanel, BorderLayout.CENTER);
    
    Platform.runLater(this::initFX);
  }
  
  private void initFX() {
    try {
      URL resource = getClass().getResource("preferences.fxml");
      if (resource == null) {
        throw new RuntimeException("preferences.fxml not found");
      }
      FXMLLoader loader = new FXMLLoader(resource);
      Parent root = loader.load();
      controller = loader.getController();
      
      controller.setOnSave(() -> {
          javax.swing.SwingUtilities.invokeLater(() -> {
              setVisible(false);
              if (desktop != null) desktop.enableControls();
          });
      });
      
      controller.setOnCancel(() -> {
          javax.swing.SwingUtilities.invokeLater(() -> {
              setVisible(false);
              if (desktop != null) desktop.enableControls();
          });
      });
      
      if (prefs != null) {
          controller.setPrefs(prefs);
      }
      
      Scene scene = new Scene(root);
      jfxPanel.setScene(scene);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void initApp() {
    // No-op
  }

  public void setDesktop(JWildfire desktop) {
    this.desktop = desktop;
  }

  public void enableControls() {
    // No-op
  }

  public void setPrefs(Prefs pPrefs) {
    prefs = pPrefs;
    if (controller != null) {
        Platform.runLater(() -> controller.setPrefs(prefs));
    }
  }

  public void setMainController(MainController mainController) {
    this.mainController = mainController;
  }
}
