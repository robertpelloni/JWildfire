package org.jwildfire.visualizer;

/**
 * A dummy implementation of the Visualizer interface for testing purposes.
 */
public class NullVisualizer implements Visualizer {

    @Override
    public void init() {
        System.out.println("NullVisualizer initialized.");
    }

    @Override
    public void updateAudio(float[] pcmData, float[] spectrum) {
        // Do nothing
    }

    @Override
    public void render(int width, int height) {
        // Do nothing
    }

    @Override
    public void dispose() {
        System.out.println("NullVisualizer disposed.");
    }

    @Override
    public String getName() {
        return "Null Visualizer";
    }
}
