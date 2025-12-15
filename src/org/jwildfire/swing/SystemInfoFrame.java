/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class SystemInfoFrame extends JFrame {
  private static final long serialVersionUID = 1L;
  private JFXPanel jfxPanel;
  private SystemInfoController controller;

  /**
   * give components names so we can test them
   * This frame displays system info from the help menu, and should disappear when ok is clicked
   */
  public SystemInfoFrame() {

    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    setResizable(true);
    setName("siif");
    setTitle("System Information (JavaFX)");
    setBounds(320, 140, 400, 300);

    jfxPanel = new JFXPanel();
    getContentPane().add(jfxPanel, BorderLayout.CENTER);

    Platform.runLater(this::initFX);
    
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowActivated(WindowEvent e) {
        refresh();
      }
    });
  }

  private void initFX() {
    try {
      URL resource = getClass().getResource("system_info.fxml");
      if (resource == null) {
        throw new RuntimeException("system_info.fxml not found");
      }
      FXMLLoader loader = new FXMLLoader(resource);
      Parent root = loader.load();
      controller = loader.getController();
      Scene scene = new Scene(root);
      jfxPanel.setScene(scene);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void refresh() {
    if (controller != null) {
      Platform.runLater(() -> controller.refresh());
    }
  }

  // Deprecated/Legacy accessor - kept for compatibility if needed, but returns null now
  public JButton getClearCacheButton() {
    return null; 
  }
}
