package org.jwildfire.visualizer;

import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.List;
import org.jtransforms.fft.FloatFFT_1D;

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

    private float sensitivity = 1.0f;
    private float gain = 1.0f; // Not used directly on line gain, but post-processing scaling

    public List<Mixer.Info> getCaptureDevices() {
        List<Mixer.Info> devices = new ArrayList<>();
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for (Mixer.Info info : mixerInfos) {
            Mixer mixer = AudioSystem.getMixer(info);
            Line.Info[] lines = mixer.getTargetLineInfo();
            if (lines.length > 0) {
                // Check if it supports our format (optional, but good practice)
                // For now, just listing all capture devices
                devices.add(info);
            }
        }
        return devices;
    }

    public void start() throws LineUnavailableException {
        start(null);
    }

    public void start(Mixer.Info mixerInfo) throws LineUnavailableException {
        if (running) return;

        AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if (mixerInfo != null) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            if (!mixer.isLineSupported(info)) {
                throw new LineUnavailableException("Line not supported on selected mixer");
            }
            line = (TargetDataLine) mixer.getLine(info);
        } else {
            if (!AudioSystem.isLineSupported(info)) {
                throw new LineUnavailableException("Line not supported");
            }
            line = (TargetDataLine) AudioSystem.getLine(info);
        }

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
                // Convert bytes to floats and apply gain/sensitivity
                for (int i = 0, j = 0; i < bytesRead; i += 2, j++) {
                    int sample = (buffer[i] << 8) | (buffer[i + 1] & 0xFF);
                    pcmData[j] = (sample / 32768.0f) * sensitivity * gain;
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
                        spectrumData[k] = (float) Math.sqrt(re * re + im * im) * sensitivity; // Apply sensitivity to spectrum too
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

    public void setSensitivity(float sensitivity) {
        this.sensitivity = sensitivity;
    }

    public void setGain(float gain) {
        this.gain = gain;
    }
}
