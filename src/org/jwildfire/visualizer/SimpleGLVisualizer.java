package org.jwildfire.visualizer;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

/**
 * A basic visualizer implementation using LWJGL OpenGL.
 */
public class SimpleGLVisualizer implements Visualizer {
    private boolean initialized = false;
    private float[] spectrum;

    @Override
    public void init() {
        // In a real scenario, we would create a window or context here if not already present.
        // Assuming an OpenGL context is already current.
        try {
            GL.createCapabilities();
            initialized = true;
            System.out.println("SimpleGLVisualizer initialized.");
        } catch (Exception e) {
            System.err.println("Failed to initialize OpenGL capabilities: " + e.getMessage());
        }
    }

    @Override
    public void updateAudio(float[] pcmData, float[] spectrum) {
        this.spectrum = spectrum;
    }

    @Override
    public void render(int width, int height) {
        if (!initialized) return;

        GL11.glViewport(0, 0, width, height);
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        if (spectrum != null && spectrum.length > 0) {
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();

            GL11.glBegin(GL11.GL_QUADS);
            float barWidth = 2.0f / spectrum.length;
            for (int i = 0; i < spectrum.length; i++) {
                float x = -1.0f + i * barWidth;
                float h = spectrum[i] * 2.0f; // Scale factor
                
                // Color based on frequency
                GL11.glColor3f(i / (float)spectrum.length, 1.0f - i / (float)spectrum.length, 0.5f);
                
                GL11.glVertex2f(x, -1.0f);
                GL11.glVertex2f(x + barWidth, -1.0f);
                GL11.glVertex2f(x + barWidth, -1.0f + h);
                GL11.glVertex2f(x, -1.0f + h);
            }
            GL11.glEnd();
        } else {
            // Draw a simple test triangle if no audio
            GL11.glBegin(GL11.GL_TRIANGLES);
                GL11.glColor3f(1.0f, 0.0f, 0.0f);
                GL11.glVertex2f(0.0f, 0.5f);
                GL11.glColor3f(0.0f, 1.0f, 0.0f);
                GL11.glVertex2f(-0.5f, -0.5f);
                GL11.glColor3f(0.0f, 0.0f, 1.0f);
                GL11.glVertex2f(0.5f, -0.5f);
            GL11.glEnd();
        }
    }

    @Override
    public void dispose() {
        initialized = false;
    }

    @Override
    public String getName() {
        return "Simple GL Visualizer";
    }
}
