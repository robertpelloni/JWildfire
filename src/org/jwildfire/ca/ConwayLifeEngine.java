package org.jwildfire.ca;

import java.util.Random;

/**
 * Standard 2D Cellular Automata: Conway's Game of Life.
 * A proof-of-concept for the Visions of Chaos (VoC) integration.
 */
public class ConwayLifeEngine implements CellularAutomataEngine {
    
    private int width;
    private int height;
    private int[] grid;
    private int[] nextGrid;
    private final Random random = new Random();

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
                int neighbors = countNeighbors(x, y);

                // Rules: B3/S23
                if (state == 1) {
                    if (neighbors < 2 || neighbors > 3) {
                        nextGrid[index] = 0; // Dies
                    } else {
                        nextGrid[index] = 1; // Survives
                    }
                } else {
                    if (neighbors == 3) {
                        nextGrid[index] = 1; // Born
                    } else {
                        nextGrid[index] = 0;
                    }
                }
            }
        }
        
        // Swap buffers
        System.arraycopy(nextGrid, 0, grid, 0, grid.length);
    }

    private int countNeighbors(int x, int y) {
        int sum = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                
                // Wrap around (toroidal array)
                int col = (x + i + width) % width;
                int row = (y + j + height) % height;
                
                sum += grid[row * width + col];
            }
        }
        return sum;
    }

    @Override
    public int[] getGridState() {
        return grid;
    }

    @Override
    public String getName() {
        return "Conway's Game of Life";
    }

    @Override
    public void randomize() {
        for (int i = 0; i < grid.length; i++) {
            // ~15% chance to be alive initially
            grid[i] = random.nextFloat() < 0.15f ? 1 : 0;
        }
    }
}
