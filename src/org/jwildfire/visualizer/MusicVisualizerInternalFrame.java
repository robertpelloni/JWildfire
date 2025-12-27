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
        this.visualizer = new SwingVisualizer(); // Use SwingVisualizer
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

        // Canvas Panel
        canvasPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (visualizer instanceof SwingVisualizer) {
                    ((SwingVisualizer) visualizer).paint(g, getWidth(), getHeight());
                } else {
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(Color.WHITE);
                    g.drawString("Visualizer: " + visualizer.getName(), 10, 20);
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
            renderTimer = new Timer(30, e -> { // ~30 FPS
                visualizer.updateAudio(audioCapture.getPcmData(), audioCapture.getSpectrumData());
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
