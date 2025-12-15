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
package org.jwildfire.swing;

import java.awt.BorderLayout;
import java.awt.Point;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

@SuppressWarnings("serial")
public class AIPostDenoiserInfoFrame extends JFrame {
  private JFXPanel jfxPanel;

  public AIPostDenoiserInfoFrame() {
    super();
    initialize();
  }

  private void initialize() {
    this.setSize(1188, 740);
    this.setLocation(new Point(JWildfire.DEFAULT_WINDOW_LEFT, JWildfire.DEFAULT_WINDOW_TOP));
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    this.setTitle("AI Post Denoiser Info (JavaFX)");
    this.setVisible(false);
    this.setResizable(true);
    
    jfxPanel = new JFXPanel();
    this.getContentPane().add(jfxPanel, BorderLayout.CENTER);
    
    Platform.runLater(this::initFX);
  }

  private void initFX() {
    try {
      URL resource = getClass().getResource("ai_post_denoiser_info.fxml");
      if (resource == null) {
        throw new RuntimeException("ai_post_denoiser_info.fxml not found");
      }
      FXMLLoader loader = new FXMLLoader(resource);
      Parent root = loader.load();
      Scene scene = new Scene(root);
      jfxPanel.setScene(scene);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // Deprecated methods for compatibility
  public void initOptiXInfoPane() { }
  public void initOIDNInfoPane() { }
}
