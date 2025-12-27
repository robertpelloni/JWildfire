package org.jwildfire.visualizer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RaymarchingVisualizerTest {

    @Test
    public void testShaderConstants() {
        assertNotNull(RaymarchingVisualizer.SHADER_MANDELBULB);
        assertFalse(RaymarchingVisualizer.SHADER_MANDELBULB.isEmpty());
        
        assertNotNull(RaymarchingVisualizer.SHADER_SPHERE);
        assertFalse(RaymarchingVisualizer.SHADER_SPHERE.isEmpty());
        
        assertTrue(RaymarchingVisualizer.SHADER_MANDELBULB.contains("Mandelbulb"));
        assertTrue(RaymarchingVisualizer.SHADER_SPHERE.contains("Sphere"));
    }
    
    @Test
    public void testName() {
        RaymarchingVisualizer viz = new RaymarchingVisualizer();
        assertEquals("Raymarching Prototype", viz.getName());
    }
}
