package org.jwildfire.ca;

import java.util.Random;

/**
 * Gray-Scott Reaction-Diffusion engine.
 * Simulates the interaction of two chemical substances (U and V) diffusing and reacting.
 * Produces organic patterns like spots, stripes, and mazes.
 * A signature feature of Visions of Chaos (VoC).
 */
public class GrayScottEngine implements CellularAutomataEngine {
    
    private int width;
    private int height;
    private float[] u; // Substance U
    private float[] v; // Substance V
    private float[] nextU;
    private float[] nextV;
    private final Random random = new Random();

    // Parameters (Standard "Coral" or "Mitosis" patterns)
    private float Du = 0.16f;  // Diffusion rate of U
    private float Dv = 0.08f;  // Diffusion rate of V
    private float f = 0.035f;  // Feed rate
    private float k = 0.060f;  // Kill rate
    private float dt = 1.0f;

    @Override
    public void init(int width, int height) {
        this.width = width;
        this.height = height;
        this.u = new float[width * height];
        this.v = new float[width * height];
        this.nextU = new float[width * height];
        this.nextV = new float[width * height];
        
        // Initial state: U = 1.0 everywhere
        for (int i = 0; i < u.length; i++) {
            u[i] = 1.0f;
            v[i] = 0.0f;
        }
        
        randomize();
    }

    @Override
    public void tick() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = y * width + x;
                float uVal = u[index];
                float vVal = v[index];
                
                float lapU = laplacian(x, y, u);
                float lapV = laplacian(x, y, v);
                
                float reaction = uVal * vVal * vVal;
                
                nextU[index] = uVal + (Du * lapU - reaction + f * (1.0f - uVal)) * dt;
                nextV[index] = vVal + (Dv * lapV + reaction - (k + f) * vVal) * dt;
                
                // Clamp
                if (nextU[index] < 0) nextU[index] = 0;
                if (nextU[index] > 1) nextU[index] = 1;
                if (nextV[index] < 0) nextV[index] = 0;
                if (nextV[index] > 1) nextV[index] = 1;
            }
        }
        
        // Swap buffers
        System.arraycopy(nextU, 0, u, 0, u.length);
        System.arraycopy(nextV, 0, v, 0, v.length);
    }

    private float laplacian(int x, int y, float[] grid) {
        float sum = 0;
        
        // 3x3 kernel weights: center -1, adjacent 0.2, diagonal 0.05
        int xm1 = (x - 1 + width) % width;
        int xp1 = (x + 1 + width) % width;
        int ym1 = (y - 1 + height) % height;
        int yp1 = (y + 1 + height) % height;
        
        sum += grid[y * width + x] * -1.0f;
        
        sum += grid[y * width + xm1] * 0.2f;
        sum += grid[y * width + xp1] * 0.2f;
        sum += grid[ym1 * width + x] * 0.2f;
        sum += grid[yp1 * width + x] * 0.2f;
        
        sum += grid[ym1 * width + xm1] * 0.05f;
        sum += grid[ym1 * width + xp1] * 0.05f;
        sum += grid[yp1 * width + xm1] * 0.05f;
        sum += grid[yp1 * width + xp1] * 0.05f;
        
        return sum;
    }

    @Override
    public int[] getGridState() {
        // Visualize the density of V
        int[] displayGrid = new int[v.length];
        for (int i = 0; i < v.length; i++) {
            displayGrid[i] = (int) (v[i] * 255.0f);
        }
        return displayGrid;
    }

    @Override
    public String getName() {
        return "Gray-Scott Reaction Diffusion";
    }

    @Override
    public void randomize() {
        // Clear V
        for (int i = 0; i < v.length; i++) {
            v[i] = 0.0f;
            u[i] = 1.0f;
        }
        
        // Add a few random blobs of V
        for (int k = 0; k < 10; k++) {
            int centerX = random.nextInt(width);
            int centerY = random.nextInt(height);
            int radius = 5 + random.nextInt(10);
            
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dx = -radius; dx <= radius; dx++) {
                    if (dx * dx + dy * dy < radius * radius) {
                        int nx = (centerX + dx + width) % width;
                        int ny = (centerY + dy + height) % height;
                        v[ny * width + nx] = 1.0f;
                    }
                }
            }
        }
    }
}
