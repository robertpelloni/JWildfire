/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2012 Andreas Maschke

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
import java.net.URL;

import javax.swing.JFrame;

import org.jwildfire.base.Tools;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class WelcomeFrame extends JFrame {
  private static final long serialVersionUID = 1L;
  private JFXPanel jfxPanel;
  private WelcomeController controller;

  public WelcomeFrame() {
    setTitle("Welcome to " + Tools.APP_TITLE + " " + Tools.getAppVersion());
    setBounds(440, 140, 520, 520);
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    setResizable(true);

    jfxPanel = new JFXPanel();
    getContentPane().add(jfxPanel, BorderLayout.CENTER);

    Platform.runLater(this::initFX);
  }

  private void initFX() {
    try {
      URL resource = getClass().getResource("welcome.fxml");
      if (resource == null) {
        throw new RuntimeException("welcome.fxml not found");
      }
      FXMLLoader loader = new FXMLLoader(resource);
      Parent root = loader.load();
      controller = loader.getController();
      controller.setSwingFrame(this);
      Scene scene = new Scene(root);
      jfxPanel.setScene(scene);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
