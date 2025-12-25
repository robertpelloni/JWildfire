package org.jwildfire.visualizer;

import javax.sound.sampled.*;

/**
 * Captures audio from the default input device (microphone/line-in).
 */
public class AudioCapture {
    private TargetDataLine line;
    private boolean running = false;
    private final int sampleRate = 44100;
    private final int bufferSize = 1024;
    private byte[] buffer;
    private float[] pcmData;
    private float[] spectrumData; // Placeholder for FFT data

    public void start() throws LineUnavailableException {
        AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
            throw new LineUnavailableException("Line not supported");
        }

        line = (TargetDataLine) AudioSystem.getLine(info);
        line.open(format);
        line.start();
        
        buffer = new byte[bufferSize * 2]; // 16-bit samples
        pcmData = new float[bufferSize];
        spectrumData = new float[bufferSize / 2];
        
        running = true;
        
        Thread captureThread = new Thread(this::captureLoop);
        captureThread.setDaemon(true);
        captureThread.start();
    }

    private void captureLoop() {
        while (running) {
            int bytesRead = line.read(buffer, 0, buffer.length);
            if (bytesRead > 0) {
                // Convert bytes to floats
                for (int i = 0, j = 0; i < bytesRead; i += 2, j++) {
                    int sample = (buffer[i] << 8) | (buffer[i + 1] & 0xFF);
                    pcmData[j] = sample / 32768.0f;
                }
                // TODO: Perform FFT to populate spectrumData
            }
        }
    }

    public void stop() {
        running = false;
        if (line != null) {
            line.stop();
            line.close();
        }
    }

    public float[] getPcmData() {
        return pcmData;
    }

    public float[] getSpectrumData() {
        return spectrumData;
    }
}
