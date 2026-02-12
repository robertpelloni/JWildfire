package org.jwildfire.create.tina.dance;

import org.jwildfire.create.tina.base.Flame;

public interface DancingFlamesUI {
    void refreshFlameImage(Flame flame, boolean drawTriangles, double fps, long frame, boolean drawFPS);
}
