package org.jwildfire.visualizer;

/**
 * Interface for music/audio visualizers.
 * This allows JWildfire to support multiple visualization backends (e.g., internal OpenGL, projectM, etc.).
 */
public interface Visualizer {
    /**
     * Initialize the visualizer.
     */
    void init();

    /**
     * Update the visualizer with audio data.
     * @param pcmData PCM audio data
     * @param spectrum Frequency spectrum data
     */
    void updateAudio(float[] pcmData, float[] spectrum);

    /**
     * Render a frame.
     * @param width Width of the render target
     * @param height Height of the render target
     */
    void render(int width, int height);

    /**
     * Clean up resources.
     */
    void dispose();

    /**
     * Get the name of the visualizer.
     * @return Name
     */
    String getName();
}
