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

import org.jwildfire.create.tina.base.Flame;

public interface ITinaInteractiveRendererController {

  public void importFlame(Flame pFlame);
  public boolean isRendering();
  public void enableControls();

  // Methods needed by InteractiveRendererFrame (legacy Swing UI)
  public void nextButton_clicked();
  public void fromClipboardButton_clicked();
  public void loadFlameButton_clicked();
  public void toClipboardButton_clicked();
  public void stopButton_clicked();
  public void saveFlameButton_clicked();
  public void saveImageButton_clicked();
  public void resolutionProfile_changed();
  public void fromEditorButton_clicked();
  public void halveRenderSizeButton_clicked();
  public void pauseBtn_clicked();
  public void toEditorButton_clicked();
  public void resumeBtn_clicked();
  public void showStatsBtn_changed();
  public void showPreviewBtn_changed();
  public void fullRenderSizeButton_clicked();
  public void quarterRenderSizeButton_clicked();
  public void saveZBufferButton_clicked();
}
