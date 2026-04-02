package org.jwildfire.visualizer.projectm;

import org.jwildfire.visualizer.Visualizer;
import java.lang.foreign.MemorySegment;

public class ProjectMVisualizer implements Visualizer {
    private MemorySegment instance;
    private boolean initialized = false;

    @Override
    public void init() {
        try {
            // Attempt to load the library. 
            // In a real deployment, we would search for libprojectM.so or projectM.dll
            // ProjectMBinding.init("projectM"); 
            System.out.println("ProjectMVisualizer: Native library not loaded (requires libprojectM).");
            // initialized = true;
        } catch (Exception e) {
            System.err.println("Failed to initialize projectM: " + e.getMessage());
        }
    }

    @Override
    public void updateAudio(float[] pcmData, float[] spectrum) {
        if (!initialized || instance == null) return;
        // Assuming mono audio for now. Channels = 1
        ProjectMBinding.addPCM(instance, pcmData, 1);
    }

    @Override
    public void render(int width, int height) {
        if (!initialized) return;
        
        if (instance == null) {
             instance = ProjectMBinding.create(width, height);
        }
        
        ProjectMBinding.renderFrame(instance);
    }

    @Override
    public void dispose() {
        if (instance != null) {
            ProjectMBinding.destroy(instance);
            instance = null;
        }
    }

    @Override
    public String getName() {
        return "projectM (Native)";
    }
}
