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
import java.awt.Rectangle;
import java.awt.Window;
import java.net.URL;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jwildfire.create.tina.script.swing.JWFScriptUserNode;
import org.jwildfire.swing.ErrorHandler;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class ScriptEditDialog extends JDialog {
  private static final long serialVersionUID = 1L;
  private final ErrorHandler errorHandler;
  private final TinaController tinaController;
  private ScriptEditorController controller;
  private JWFScriptUserNode scriptNode;

  /**
   * @param pOwner
   */
  public ScriptEditDialog(TinaController pTinaController, Window pOwner, ErrorHandler pErrorHandler) {
    super(pOwner);
    errorHandler = pErrorHandler;
    tinaController = pTinaController;
    initialize();
    Rectangle rootBounds = pOwner.getBounds();
    Dimension size = getSize();
    setLocation(rootBounds.x + (rootBounds.width - size.width) / 2, rootBounds.y + (rootBounds.height - size.height) / 2);
  }

  private void initialize() {
    this.setSize(900, 700);
    this.setLayout(new BorderLayout());

    JFXPanel jfxPanel = new JFXPanel();
    this.add(jfxPanel, BorderLayout.CENTER);

    Platform.runLater(() -> {
        try {
            URL fxmlUrl = getClass().getResource("script_editor.fxml");
            if (fxmlUrl == null) {
                throw new RuntimeException("Cannot find script_editor.fxml");
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            controller = loader.getController();
            controller.setContext(this, tinaController, errorHandler);

            if (scriptNode != null) {
                controller.setScriptNode(scriptNode);
            }

            Scene scene = new Scene(root);
            jfxPanel.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "Error initializing JavaFX Script Editor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            });
        }
    });
  }

  public void setScriptNode(JWFScriptUserNode pScriptNode) {
    this.scriptNode = pScriptNode;
    String scriptname = pScriptNode.getUserObject().toString();
    setTitle("Editing " + scriptname);

    if (controller != null) {
        Platform.runLater(() -> controller.setScriptNode(pScriptNode));
    }
  }

  public void closeDialog() {
      SwingUtilities.invokeLater(() -> {
          setVisible(false);
          dispose();
      });
  }
}
