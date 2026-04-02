package org.jwildfire.visualizer;

/**
 * Interface for components that want to receive unified audio data streams.
 */
public interface AudioListener {
    /**
     * Called when a new frame of audio data has been captured and processed.
     * 
     * @param pcmData       The raw PCM audio data (normalized to -1.0 to 1.0)
     * @param spectrumData  The computed FFT frequency spectrum magnitudes
     */
    void onAudioData(float[] pcmData, float[] spectrumData);
}
