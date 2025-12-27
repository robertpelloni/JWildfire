package org.jwildfire.visualizer;

import javax.sound.sampled.*;
import edu.emory.mathcs.jtransforms.fft.FloatFFT_1D;

/**
 * Captures audio from the default input device (microphone/line-in).
 */
public class AudioCapture {
    private TargetDataLine line;
    private volatile boolean running = false;
    private final int sampleRate = 44100;
    private final int bufferSize = 1024;
    private byte[] buffer;
    private float[] pcmData;
    private float[] spectrumData;
    private FloatFFT_1D fft;

    public void start() throws LineUnavailableException {
        if (running) return;

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
        fft = new FloatFFT_1D(bufferSize);
        
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
                
                // Perform FFT
                float[] fftData = new float[bufferSize];
                System.arraycopy(pcmData, 0, fftData, 0, bufferSize);
                fft.realForward(fftData);
                
                // Calculate magnitude
                if (spectrumData != null) {
                    spectrumData[0] = Math.abs(fftData[0]);
                    for (int k = 1; k < bufferSize / 2; k++) {
                        float re = fftData[2 * k];
                        float im = fftData[2 * k + 1];
                        spectrumData[k] = (float) Math.sqrt(re * re + im * im);
                    }
                }
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

    public boolean isCapturing() {
        return running;
    }

    public float[] getPcmData() {
        return pcmData;
    }

    public float[] getSpectrumData() {
        return spectrumData;
    }
}
