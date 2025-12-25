package org.jwildfire.visualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jwildfire.base.Prefs;
import org.jwildfire.swing.JWildfire;

public class MusicVisualizerInternalFrame extends JInternalFrame {
    private final JWildfire desktop;
    private final Visualizer visualizer;
    private final AudioCapture audioCapture;
    private Timer renderTimer;
    private JPanel canvasPanel;

    public MusicVisualizerInternalFrame(JWildfire desktop) {
        super("Music Visualizer", true, true, true, true);
        this.desktop = desktop;
        this.visualizer = new SimpleGLVisualizer();
        this.audioCapture = new AudioCapture();
        
        initUI();
    }

    private void initUI() {
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Control Panel
        JPanel controlPanel = new JPanel();
        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");
        
        startButton.addActionListener(e -> startVisualization());
        stopButton.addActionListener(e -> stopVisualization());
        
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        add(controlPanel, BorderLayout.NORTH);

        // Canvas Panel (Placeholder for LWJGL Canvas)
        // Note: Integrating LWJGL 3 directly into Swing requires specific setup (JAWT).
        // For this prototype, we will use a custom JPanel that the Visualizer *could* draw to,
        // or we launch a separate window. 
        // Given SimpleGLVisualizer uses pure LWJGL OpenGL calls, it might expect a GLFW window.
        // We'll simulate the integration here.
        
        canvasPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.GREEN);
                g.drawString("Visualizer Output (Placeholder)", 10, 20);
                
                // Draw simple waveform from audio data
                if (audioCapture.getPcmData() != null) {
                    float[] data = audioCapture.getPcmData();
                    int midY = getHeight() / 2;
                    for (int i = 0; i < data.length - 1 && i < getWidth(); i++) {
                        int y1 = midY + (int)(data[i] * 100);
                        int y2 = midY + (int)(data[i+1] * 100);
                        g.drawLine(i, y1, i+1, y2);
                    }
                }
            }
        };
        add(canvasPanel, BorderLayout.CENTER);
    }

    private void startVisualization() {
        try {
            audioCapture.start();
            visualizer.init();
            
            // Simple timer to repaint the Swing panel (simulating a render loop)
            renderTimer = new Timer(16, e -> {
                visualizer.updateAudio(audioCapture.getPcmData(), audioCapture.getSpectrumData());
                // visualizer.render(canvasPanel.getWidth(), canvasPanel.getHeight()); // This would be for GL
                canvasPanel.repaint();
            });
            renderTimer.start();
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error starting audio: " + e.getMessage());
        }
    }

    private void stopVisualization() {
        if (renderTimer != null) renderTimer.stop();
        audioCapture.stop();
        visualizer.dispose();
    }
}
