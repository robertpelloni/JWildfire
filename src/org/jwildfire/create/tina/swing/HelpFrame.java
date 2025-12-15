/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2022 Andreas Maschke

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
import java.awt.Point;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;

import org.jwildfire.swing.JWildfire;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class HelpFrame extends JFrame {
  private static final long serialVersionUID = 1L;
  private JFXPanel jfxPanel;
  private HelpController controller;
  
  // Legacy components kept for compatibility with TinaController
  private JTextPane helpPane;
  private JTextPane meshGenHintPane;
  private JTextPane apophysisHintsPane;
  private JTextPane colorTypesPane;

  public HelpFrame() {
    super();
    initialize();
  }

  private void initialize() {
    this.setSize(1188, 740);
    this.setLocation(new Point(JWildfire.DEFAULT_WINDOW_LEFT, JWildfire.DEFAULT_WINDOW_TOP));
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    this.setTitle("Hints, Help and About (JavaFX)");
    this.setVisible(false);
    this.setResizable(true);
    
    jfxPanel = new JFXPanel();
    this.getContentPane().add(jfxPanel, BorderLayout.CENTER);
    
    // Initialize legacy panes so controllers can populate them
    helpPane = new JTextPane();
    meshGenHintPane = new JTextPane();
    apophysisHintsPane = new JTextPane();
    colorTypesPane = new JTextPane();
    
    Platform.runLater(this::initFX);
  }

  private void initFX() {
    try {
      URL resource = getClass().getResource("help.fxml");
      if (resource == null) {
        throw new RuntimeException("help.fxml not found");
      }
      FXMLLoader loader = new FXMLLoader(resource);
      Parent root = loader.load();
      controller = loader.getController();
      
      // Update the controller with content from the legacy panes
      // We do this here, assuming TinaController populated them by the time initFX runs.
      // If not, we might need a refresh mechanism.
      updateControllerContent();
      
      Scene scene = new Scene(root);
      jfxPanel.setScene(scene);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  private void updateControllerContent() {
      if (controller != null) {
          controller.setHelpContent(helpPane.getText());
          controller.setApophysisHintsContent(apophysisHintsPane.getText());
          controller.setMeshGenHintsContent(meshGenHintPane.getText());
          controller.setColorTypesContent(colorTypesPane.getText());
      }
  }
  
  @Override
  public void setVisible(boolean b) {
      if (b) {
          // Ensure content is up to date when showing
          Platform.runLater(this::updateControllerContent);
      }
      super.setVisible(b);
  }

  // Legacy getters
  JTextPane getHelpPane() { return helpPane; }
  JTextPane getMeshGenHintPane() { return meshGenHintPane; }
  JTextPane getApophysisHintsPane() { return apophysisHintsPane; }
  JTextPane getColorTypesPane() { return colorTypesPane; }

  public void setTinaController(TinaController tinaController) {
  }

}
