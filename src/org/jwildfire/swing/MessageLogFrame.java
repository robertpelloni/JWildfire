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

import ch.qos.logback.classic.spi.ILoggingEvent;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

@SuppressWarnings("serial")
public class MessageLogFrame extends JFrame implements MessageLogEventObserver {
  private JFXPanel jfxPanel;

  public MessageLogFrame() {
    super();
    initialize();
  }

  private void initialize() {
    this.setSize(1188, 740);
    this.setLocation(new Point(JWildfire.DEFAULT_WINDOW_LEFT, JWildfire.DEFAULT_WINDOW_TOP));
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    this.setTitle("Message log (JavaFX)");
    this.setVisible(false);
    this.setResizable(true);
    
    jfxPanel = new JFXPanel();
    this.getContentPane().add(jfxPanel, BorderLayout.CENTER);
    
    Platform.runLater(this::initFX);
  }

  private void initFX() {
    try {
      URL resource = getClass().getResource("message_log.fxml");
      if (resource == null) {
        throw new RuntimeException("message_log.fxml not found");
      }
      FXMLLoader loader = new FXMLLoader(resource);
      Parent root = loader.load();
      Scene scene = new Scene(root);
      jfxPanel.setScene(scene);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // Deprecated method for compatibility - Controller handles registration now
  public void initLogMessagePane() {
    // No-op
  }

  @Override
  public void update(ILoggingEvent event) {
    // No-op, logic moved to Controller
  }
}
