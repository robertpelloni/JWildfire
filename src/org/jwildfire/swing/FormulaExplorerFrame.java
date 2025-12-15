/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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
import java.awt.Rectangle;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class FormulaExplorerFrame extends JFrame {
  private static final long serialVersionUID = 1L;
  private JFXPanel jfxPanel;

  public FormulaExplorerFrame() {
    super();
    initialize();
  }

  private void initialize() {
    this.setSize(600, 600);
    this.setBounds(new Rectangle(650, 36, 600, 600));
    this.setResizable(true);
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    this.setVisible(false);
    this.setTitle("Formula Explorer (JavaFX)");
    
    jfxPanel = new JFXPanel();
    this.getContentPane().add(jfxPanel, BorderLayout.CENTER);
    
    Platform.runLater(this::initFX);
  }

  private void initFX() {
    try {
      URL resource = getClass().getResource("formula_explorer.fxml");
      if (resource == null) {
        throw new RuntimeException("formula_explorer.fxml not found");
      }
      FXMLLoader loader = new FXMLLoader(resource);
      Parent root = loader.load();
      Scene scene = new Scene(root);
      jfxPanel.setScene(scene);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // Stubs for backward compatibility with MainController/JWildfire
  
  public void initApp() {
    // No-op
  }

  public void setMainController(MainController pMainController) {
    // No-op, new controller manages itself
  }

  public void setFormulaExplorerController(FormulaExplorerController pFormulaExplorerController) {
    // No-op
  }
  
  // Stubs for getters
  public Object getFormulaPanel() { return null; }
  public JTextField getFormulaExplorerFormula1REd() { return null; }
  public JTextField getFormulaExplorerFormula2REd() { return null; }
  public JTextField getFormulaExplorerFormula3REd() { return null; }
  public JTextField getFormulaExplorerFormulaXMinREd() { return null; }
  public JTextField getFormulaExplorerFormulaXMaxREd() { return null; }
  public JTextField getFormulaExplorerFormulaXCountREd() { return null; }
  public JTextArea getFormulaExplorerValuesTextArea() { return null; }

} //  @jve:decl-index=0:visual-constraint="10,10"
