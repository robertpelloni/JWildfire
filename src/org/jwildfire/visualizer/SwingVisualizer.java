package org.jwildfire.visualizer;

import java.awt.*;

public class SwingVisualizer implements Visualizer {
    private float[] pcmData;
    private float[] spectrumData;

    @Override
    public void init() {
    }

    @Override
    public void updateAudio(float[] pcmData, float[] spectrum) {
        this.pcmData = pcmData;
        this.spectrumData = spectrum;
    }

    @Override
    public void render(int width, int height) {
        // No-op for Swing, painting happens in paint()
    }

    public void paint(Graphics g, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Background
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);

        // Draw Spectrum
        if (spectrumData != null && spectrumData.length > 0) {
            int numBars = Math.min(spectrumData.length, width / 2); // Limit bars
            int barWidth = width / numBars;
            for (int i = 0; i < numBars; i++) {
                float val = spectrumData[i];
                // Logarithmic scaling for better visualization
                int barHeight = (int) (Math.log10(val * 100 + 1) * height * 0.5); 
                
                g2d.setColor(Color.getHSBColor((float)i / numBars, 0.8f, 0.9f));
                g2d.fillRect(i * barWidth, height - barHeight, barWidth - 1, barHeight);
            }
        }

        // Draw Waveform overlay
        if (pcmData != null && pcmData.length > 0) {
            g2d.setColor(new Color(255, 255, 255, 180));
            g2d.setStroke(new BasicStroke(1.5f));
            int midY = height / 2;
            int step = Math.max(1, pcmData.length / width);
            
            for (int i = 0; i < width - 1; i++) {
                int idx1 = i * step;
                int idx2 = (i + 1) * step;
                if (idx1 >= pcmData.length || idx2 >= pcmData.length) break;
                
                int y1 = midY + (int)(pcmData[idx1] * height * 0.4);
                int y2 = midY + (int)(pcmData[idx2] * height * 0.4);
                g2d.drawLine(i, y1, i+1, y2);
            }
        }
    }

    @Override
    public void dispose() {
    }

    @Override
    public String getName() {
        return "Swing Visualizer";
    }
}
