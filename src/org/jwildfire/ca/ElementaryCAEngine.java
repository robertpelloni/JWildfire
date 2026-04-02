package org.jwildfire.ca;

import java.util.Random;

/**
 * 1D Cellular Automata: Wolfram's Elementary Cellular Automaton.
 * VoC Integration feature.
 */
public class ElementaryCAEngine implements CellularAutomataEngine {
    private int width;
    private int height;
    private int[] grid;
    private int currentRow = 0;
    private int rule = 30; // Rule 30 by default
    private final Random random = new Random();

    public void setRule(int rule) {
        if (rule >= 0 && rule <= 255) {
            this.rule = rule;
        }
    }

    @Override
    public void init(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new int[width * height];
        randomize();
    }

    @Override
    public void tick() {
        if (currentRow >= height - 1) {
            // Scroll everything up
            System.arraycopy(grid, width, grid, 0, width * (height - 1));
            currentRow = height - 2;
        }

        int nextRow = currentRow + 1;
        for (int x = 0; x < width; x++) {
            int left = (x == 0) ? grid[currentRow * width + (width - 1)] : grid[currentRow * width + (x - 1)];
            int center = grid[currentRow * width + x];
            int right = (x == width - 1) ? grid[currentRow * width + 0] : grid[currentRow * width + (x + 1)];

            int index = (left << 2) | (center << 1) | right;
            int nextState = (rule >> index) & 1;
            grid[nextRow * width + x] = nextState;
        }
        
        currentRow++;
    }

    @Override
    public int[] getGridState() {
        return grid;
    }

    @Override
    public String getName() {
        return "1D Elementary CA (Rule " + rule + ")";
    }

    @Override
    public void randomize() {
        // Clear grid
        for (int i = 0; i < grid.length; i++) {
            grid[i] = 0;
        }
        currentRow = 0;
        
        // Single pixel in middle
        grid[width / 2] = 1;
    }
}
