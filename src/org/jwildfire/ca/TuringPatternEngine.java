package org.jwildfire.ca;

import java.util.Random;

/**
 * Multi-scale Turing Patterns Cellular Automata.
 * Generates organic, coral-like, or skin-like patterns.
 * Based on the "Multi-scale Turing Patterns" by Jonathan McCabe.
 * A signature feature of Visions of Chaos (VoC).
 */
public class TuringPatternEngine implements CellularAutomataEngine {
    
    private int width;
    private int height;
    private float[] grid;
    private float[] nextGrid;
    private final Random random = new Random();

    // Scales: activator radius, inhibitor radius, and step size for each scale
    private final int numScales = 5;
    private int[] activatorRadii = {1, 2, 4, 8, 16};
    private int[] inhibitorRadii = {2, 4, 8, 16, 32};
    private float[] stepSizes = {0.05f, 0.04f, 0.03f, 0.02f, 0.01f};

    @Override
    public void init(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new float[width * height];
        this.nextGrid = new float[width * height];
        randomize();
    }

    @Override
    public void tick() {
        int[] bestScales = new int[width * height];
        float[] bestVariations = new float[width * height];
        float[] actualVariations = new float[width * height]; // (act - inh) not absolute
        
        for (int i = 0; i < width * height; i++) {
            bestVariations[i] = Float.MAX_VALUE;
        }

        for (int s = 0; s < numScales; s++) {
            int actRad = activatorRadii[s];
            int inhRad = inhibitorRadii[s];
            
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int index = y * width + x;
                    float actAvg = getAverage(x, y, actRad);
                    float inhAvg = getAverage(x, y, inhRad);
                    float diff = actAvg - inhAvg;
                    float absDiff = Math.abs(diff);
                    
                    if (absDiff < bestVariations[index]) {
                        bestVariations[index] = absDiff;
                        actualVariations[index] = diff;
                        bestScales[index] = s;
                    }
                }
            }
        }

        // Update grid based on the "best" scale's variation
        for (int i = 0; i < width * height; i++) {
            if (actualVariations[i] > 0) {
                grid[i] += stepSizes[bestScales[i]];
            } else {
                grid[i] -= stepSizes[bestScales[i]];
            }
        }

        // Normalize and clamp
        normalize();
    }

    private float getAverage(int x, int y, int radius) {
        float sum = 0;
        int count = 0;
        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                int nx = (x + dx + width) % width;
                int ny = (y + dy + height) % height;
                sum += grid[ny * width + nx];
                count++;
            }
        }
        return sum / count;
    }

    private void normalize() {
        float min = grid[0];
        float max = grid[0];
        for (float v : grid) {
            if (v < min) min = v;
            if (v > max) max = v;
        }
        
        float range = max - min;
        if (range < 0.0001f) range = 1.0f;
        
        for (int i = 0; i < grid.length; i++) {
            grid[i] = (grid[i] - min) / range;
            // Map to [-1, 1] for the algorithm's next iteration
            grid[i] = grid[i] * 2.0f - 1.0f;
        }
    }

    @Override
    public int[] getGridState() {
        // Map [-1, 1] to [0, 255] for display
        int[] displayGrid = new int[grid.length];
        for (int i = 0; i < grid.length; i++) {
            displayGrid[i] = (int) ((grid[i] + 1.0f) * 127.5f);
        }
        return displayGrid;
    }

    @Override
    public String getName() {
        return "Multi-scale Turing Patterns";
    }

    @Override
    public void randomize() {
        for (int i = 0; i < grid.length; i++) {
            grid[i] = random.nextFloat() * 2.0f - 1.0f;
        }
    }
}
