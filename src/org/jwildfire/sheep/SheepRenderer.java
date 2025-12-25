package org.jwildfire.sheep;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.render.FlameRenderer;

/**
 * Specialized renderer for Electric Sheep flames.
 * Enforces constraints like looping and specific resolutions.
 */
public class SheepRenderer {
    private final FlameRenderer internalRenderer;

    public SheepRenderer(FlameRenderer renderer) {
        this.internalRenderer = renderer;
    }

    public void renderSheep(Flame sheepFlame) {
        // TODO: Apply sheep-specific constraints
        // 1. Check for loop parameters
        // 2. Set resolution if required
        
        System.out.println("Rendering sheep: " + sheepFlame.getName());
        
        // Delegate to internal renderer (simplified)
        // internalRenderer.renderFlame(sheepFlame); 
    }
}
