package org.jwildfire.ca;

import java.util.Random;

/**
 * 3D Cellular Automata: Conway's Life in 3D.
 * Standard Moore neighborhood (26 neighbors).
 * A core feature of Visions of Chaos (VoC) for 3D exploration.
 */
public class Life3DEngine implements CellularAutomata3DEngine {
    
    private int width;
    private int height;
    private int depth;
    private int[] grid;
    private int[] nextGrid;
    private final Random random = new Random();

    // Standard 3D Life rules: B56/S45 (Carter Bays)
    private int[] birth = {5, 6};
    private int[] survival = {4, 5};

    @Override
    public void init(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.grid = new int[width * height * depth];
        this.nextGrid = new int[width * height * depth];
        randomize();
    }

    @Override
    public void tick() {
        for (int z = 0; z < depth; z++) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int index = z * width * height + y * width + x;
                    int state = grid[index];
                    int neighbors = countNeighbors(x, y, z);

                    if (state == 1) {
                        if (contains(survival, neighbors)) {
                            nextGrid[index] = 1;
                        } else {
                            nextGrid[index] = 0;
                        }
                    } else {
                        if (contains(birth, neighbors)) {
                            nextGrid[index] = 1;
                        } else {
                            nextGrid[index] = 0;
                        }
                    }
                }
            }
        }
        
        System.arraycopy(nextGrid, 0, grid, 0, grid.length);
    }

    private int countNeighbors(int x, int y, int z) {
        int sum = 0;
        for (int dz = -1; dz <= 1; dz++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    
                    int nz = (z + dz + depth) % depth;
                    int ny = (y + dy + height) % height;
                    int nx = (x + dx + width) % width;
                    
                    sum += grid[nz * width * height + ny * width + nx];
                }
            }
        }
        return sum;
    }

    private boolean contains(int[] arr, int val) {
        for (int a : arr) if (a == val) return true;
        return false;
    }

    @Override
    public int[] getGridState() {
        return grid;
    }

    @Override
    public String getName() {
        return "3D Game of Life";
    }

    @Override
    public void randomize() {
        for (int i = 0; i < grid.length; i++) {
            grid[i] = random.nextFloat() < 0.1f ? 1 : 0; // 10% density
        }
    }
}
