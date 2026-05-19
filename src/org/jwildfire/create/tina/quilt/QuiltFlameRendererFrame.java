/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2026 Andreas Maschke

  This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser 
  General Public License as published by the Free Software Foundation; either version 2.1 of the 
  License, or (at your option) any later version.
 
  This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License along with this software; 
  if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jwildfire.create.tina.quilt;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.swing.JWFNumberField;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.swing.JWildfire;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class QuiltFlameRendererFrame extends JFrame {
  private static final long serialVersionUID = 1L;
  private TinaController tinaController;
  private QuiltFlameRendererController controller;

  public QuiltFlameRendererFrame() {
    super();
    initialize();
  }

  private void initialize() {
    this.setSize(900, 600);
    this.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
    this.setLocation(new Point(200 + JWildfire.DEFAULT_WINDOW_LEFT, 50 + JWildfire.DEFAULT_WINDOW_TOP));
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    this.setTitle("Quilt flame renderer");
    this.setVisible(false);
    this.setResizable(true);

    JFXPanel jfxPanel = new JFXPanel();
    this.setContentPane(jfxPanel);

    Platform.runLater(() -> {
        try {
            URL fxmlUrl = getClass().getResource("/org/jwildfire/create/tina/quilt/quilt_flame_renderer.fxml");
            if (fxmlUrl == null) {
                throw new RuntimeException("Cannot find quilt_flame_renderer.fxml");
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
                JOptionPane.showMessageDialog(this, "Error initializing JavaFX Quilt Renderer: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            });
        }
    });
  }

  public void setTinaController(TinaController tinaController) {
    this.tinaController = tinaController;
    if (controller != null) {
        Platform.runLater(() -> controller.setTinaController(tinaController));
    }
  }

  public QuiltFlameRendererController getController() {
      return controller;
  }

  // Legacy stubs to maintain compatibility
  public JButton getOpenFlameButton() { return null; }
  public JButton getImportFlameFromEditorButton() { return null; }
  public JButton getImportFlameFromClipboardButton() { return null; }
  public JWFNumberField getQualityEdit() { return null; }
  public JWFNumberField getXSegmentationLevelEdit() { return null; }
  public JWFNumberField getYSegmentationLevelEdit() { return null; }
  public JWFNumberField getRenderWidthEdit() { return null; }
  public JWFNumberField getRenderHeightEdit() { return null; }
  public JWFNumberField getSegmentWidthEdit() { return null; }
  public JWFNumberField getSegmentHeightEdit() { return null; }
  public JTextField getOutputFilenameEdit() { return null; }
  public JProgressBar getSegmentProgressBar() { return null; }
  public JButton getRenderButton() { return null; }
  public javax.swing.JPanel getPreviewRootPanel() { return null; }
  public JButton getResolution4KButton() { return null; }
  public JButton getResolution8KButton() { return null; }
  public JButton getResolution16KButton() { return null; }
  public JButton getResolution32KButton() { return null; }
}
