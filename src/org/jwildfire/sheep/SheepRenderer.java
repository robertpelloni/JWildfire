package org.jwildfire.sheep;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.FlameReader;
import org.jwildfire.create.tina.render.ProgressUpdater;
import org.jwildfire.create.tina.render.gpu.GPURenderer;
import org.jwildfire.create.tina.render.gpu.GPURendererFactory;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanelConfig;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class SheepRenderer {
    private static final Logger logger = LoggerFactory.getLogger(SheepRenderer.class);
    private final JPanel outputPanel;
    private JLabel imageLabel;

    public SheepRenderer(JPanel outputPanel) {
        this.outputPanel = outputPanel;
        this.imageLabel = new JLabel();
        this.imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        if (outputPanel != null) {
            outputPanel.removeAll();
            outputPanel.setLayout(new BorderLayout());
            outputPanel.add(new JScrollPane(imageLabel), BorderLayout.CENTER);
        }
    }

    public void renderSheep(String flameFilePath) {
        if (outputPanel == null) return;

        new Thread(() -> {
            try {
                setStatus("Loading flame...");
                FlameReader reader = new FlameReader(Prefs.getPrefs());
                List<Flame> flames = reader.readFlames(flameFilePath);
                renderFlames(flames);
            } catch (Exception e) {
                e.printStackTrace();
                setStatus("Error: " + e.getMessage());
            }
        }).start();
    }

    public void renderSheepFromXML(String xml) {
        if (outputPanel == null) return;

        new Thread(() -> {
            try {
                setStatus("Loading flame from XML...");
                FlameReader reader = new FlameReader(Prefs.getPrefs());
                List<Flame> flames = reader.readFlamesfromXML(xml);
                renderFlames(flames);
            } catch (Exception e) {
                e.printStackTrace();
                setStatus("Error: " + e.getMessage());
            }
        }).start();
    }

    private void renderFlames(List<Flame> flames) {
        if (flames.isEmpty()) {
            setStatus("No flames found.");
            return;
        }

        Flame sheep = flames.get(0);
        int width = outputPanel.getWidth();
        int height = outputPanel.getHeight();
        if (width <= 0) width = 800;
        if (height <= 0) height = 600;

        setStatus("Rendering...");
        
        SimpleImage renderedImage = null;
        if (GPURendererFactory.isAvailable()) {
            GPURenderer renderer = GPURendererFactory.getGPURenderer();
            renderedImage = renderer.renderPreview(
                sheep, 
                width, 
                height, 
                Prefs.getPrefs(), 
                new ProgressUpdater() {
                    @Override
                    public void initProgress(int maxSteps) {}
                    @Override
                    public void updateProgress(int currentStep) {}
                    @Override
                    public void cancel() {}
                }, 
                null, 
                null, 
                new FlamePanelConfig(), 
                new ErrorHandler() {
                    @Override
                    public void handleError(Throwable ex) {
                        ex.printStackTrace();
                    }
                    @Override
                    public void handleError(String msg, Throwable ex) {
                        System.err.println(msg);
                        ex.printStackTrace();
                    }
                }, 
                logger
            );
        } else {
            setStatus("GPU Renderer not available.");
            return;
        }

        if (renderedImage != null) {
            final BufferedImage img = renderedImage.getBufferedImg();
            SwingUtilities.invokeLater(() -> {
                imageLabel.setIcon(new ImageIcon(img));
                imageLabel.setText("");
                outputPanel.revalidate();
                outputPanel.repaint();
            });
        } else {
            setStatus("Render failed.");
        }
    }

    private void setStatus(String msg) {
        SwingUtilities.invokeLater(() -> {
            imageLabel.setIcon(null);
            imageLabel.setText(msg);
        });
    }
}
