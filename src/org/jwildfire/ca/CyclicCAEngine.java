package org.jwildfire.ca;

import java.util.Random;

/**
 * Cyclic Cellular Automata.
 * Creates complex spiraling patterns.
 * A core feature of Visions of Chaos (VoC).
 */
public class CyclicCAEngine implements CellularAutomataEngine {
    
    private int width;
    private int height;
    private int[] grid;
    private int[] nextGrid;
    private final Random random = new Random();

    // Parameters
    private int numStates = 16;
    private int threshold = 3;
    private boolean mooreNeighborhood = true;

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
                int nextState = (state + 1) % numStates;

                int count = 0;
                if (mooreNeighborhood) {
                    for (int i = -1; i <= 1; i++) {
                        for (int j = -1; j <= 1; j++) {
                            if (i == 0 && j == 0) continue;
                            int nx = (x + i + width) % width;
                            int ny = (y + j + height) % height;
                            if (grid[ny * width + nx] == nextState) count++;
                        }
                    }
                } else {
                    // von Neumann neighborhood
                    int[][] neighbors = {{0,1}, {0,-1}, {1,0}, {-1,0}};
                    for (int[] n : neighbors) {
                        int nx = (x + n[0] + width) % width;
                        int ny = (y + n[1] + height) % height;
                        if (grid[ny * width + nx] == nextState) count++;
                    }
                }

                if (count >= threshold) {
                    nextGrid[index] = nextState;
                } else {
                    nextGrid[index] = state;
                }
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
        return "Cyclic CA";
    }

    @Override
    public void randomize() {
        for (int i = 0; i < grid.length; i++) {
            grid[i] = random.nextInt(numStates);
        }
    }

    public void setParams(int numStates, int threshold, boolean moore) {
        this.numStates = numStates;
        this.threshold = threshold;
        this.mooreNeighborhood = moore;
    }
}
