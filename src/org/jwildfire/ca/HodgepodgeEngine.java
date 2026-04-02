package org.jwildfire.ca;

import java.util.Random;

/**
 * Hodgepodge Machine Cellular Automata.
 * Simulates chemical reactions like the Belousov-Zhabotinsky reaction.
 * A core feature of Visions of Chaos (VoC).
 */
public class HodgepodgeEngine implements CellularAutomataEngine {
    
    private int width;
    private int height;
    private int[] grid;
    private int[] nextGrid;
    private final Random random = new Random();

    // Parameters
    private int maxState = 100;
    private int k1 = 2; // infected constant
    private int k2 = 3; // ill constant
    private int g = 5;  // growth constant

    @Override
    public void init(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new int[width * height];
        this.nextGrid = new int[width * height];
        randomize();
    }

    @Override
    public void tick() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = y * width + x;
                int state = grid[index];

                if (state == 0) {
                    // Healthy
                    int infected = 0;
                    int ill = 0;
                    for (int i = -1; i <= 1; i++) {
                        for (int j = -1; j <= 1; j++) {
                            if (i == 0 && j == 0) continue;
                            int nx = (x + i + width) % width;
                            int ny = (y + j + height) % height;
                            int ns = grid[ny * width + nx];
                            if (ns > 0 && ns < maxState) infected++;
                            else if (ns == maxState) ill++;
                        }
                    }
                    nextGrid[index] = (infected / k1) + (ill / k2);
                } else if (state == maxState) {
                    // Ill
                    nextGrid[index] = 0;
                } else {
                    // Infected
                    int sum = 0;
                    int count = 0;
                    for (int i = -1; i <= 1; i++) {
                        for (int j = -1; j <= 1; j++) {
                            int nx = (x + i + width) % width;
                            int ny = (y + j + height) % height;
                            sum += grid[ny * width + nx];
                            count++;
                        }
                    }
                    nextGrid[index] = (sum / count) + g;
                }

                // Clamp
                if (nextGrid[index] > maxState) nextGrid[index] = maxState;
            }
        }
        
        // Swap buffers
        System.arraycopy(nextGrid, 0, grid, 0, grid.length);
    }

    @Override
    public int[] getGridState() {
        return grid;
    }

    @Override
    public String getName() {
        return "Hodgepodge Machine";
    }

    @Override
    public void randomize() {
        for (int i = 0; i < grid.length; i++) {
            grid[i] = random.nextInt(maxState + 1);
        }
    }

    public void setParams(int k1, int k2, int g) {
        this.k1 = k1;
        this.k2 = k2;
        this.g = g;
    }
}
