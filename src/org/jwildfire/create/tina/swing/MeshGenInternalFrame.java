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
package org.jwildfire.create.tina.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.net.URL;

import javax.swing.JComboBox;
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

public class MeshGenInternalFrame extends JFrame {
  private static final long serialVersionUID = 1L;
  private TinaController tinaController;
  private MeshGenInternalControllerFX controller;

  public MeshGenInternalFrame() {
    super();
    initialize();
  }

  private void initialize() {
    this.setSize(600, 400);
    this.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
    this.setLocation(new Point(200 + JWildfire.DEFAULT_WINDOW_LEFT, 50 + JWildfire.DEFAULT_WINDOW_TOP));
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    this.setTitle("Mesh Generator");
    this.setVisible(false);
    this.setResizable(true);

    JFXPanel jfxPanel = new JFXPanel();
    this.setContentPane(jfxPanel);

    Platform.runLater(() -> {
        try {
            URL fxmlUrl = getClass().getResource("/org/jwildfire/create/tina/swing/mesh_gen_internal.fxml");
            if (fxmlUrl == null) {
                throw new RuntimeException("Cannot find mesh_gen_internal.fxml");
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
                JOptionPane.showMessageDialog(this, "Error initializing JavaFX Mesh Generator: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

  public MeshGenInternalControllerFX getController() {
      return controller;
  }

  // Legacy stubs for compatibility
  public JComboBox getMeshGenOutputTypeCmb() { return null; }
  public JComboBox getMeshGenPreFilter1Cmb() { return null; }
  public JComboBox getMeshGenPreFilter2Cmb() { return null; }
}
