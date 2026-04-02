package org.jwildfire.ca.fluid;

/**
 * Lattice Boltzmann Method (LBM) D2Q9 solver.
 * Simulates real fluid dynamics (gas/liquid flow).
 * Part of the Visions of Chaos (VoC) assimilation project.
 */
public class LatticeBoltzmannEngine implements FluidEngine {
    
    private int width;
    private int height;
    private float[][][] f;     // Distribution function [9][width][height]
    private float[][][] fNext; // Streaming buffer
    private boolean[][] solid; // Obstacles

    // D2Q9 velocity directions
    private final int[] ex = {0, 1, 0, -1, 0, 1, -1, -1, 1};
    private final int[] ey = {0, 0, 1, 0, -1, 1, 1, -1, -1};
    // D2Q9 weights
    private final float[] w = {4/9.0f, 1/9.0f, 1/9.0f, 1/9.0f, 1/9.0f, 1/36.0f, 1/36.0f, 1/36.0f, 1/36.0f};

    // Constants
    private float omega = 1.0f; // Relaxation parameter (related to viscosity)
    private float uInlet = 0.1f; // Inlet velocity

    @Override
    public void init(int width, int height) {
        this.width = width;
        this.height = height;
        this.f = new float[9][width][height];
        this.fNext = new float[9][width][height];
        this.solid = new boolean[width][height];
        
        // Initialize with equilibrium for zero velocity and unit density
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int i = 0; i < 9; i++) {
                    f[i][x][y] = w[i]; // Equilibrium for u=0, rho=1
                }
            }
        }
    }

    @Override
    public void step() {
        // 1. Streaming & Bounce-back
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int i = 0; i < 9; i++) {
                    int nx = (x + ex[i] + width) % width;
                    int ny = (y + ey[i] + height) % height;
                    
                    if (solid[nx][ny]) {
                        // Bounce back to same cell but reverse direction
                        int opp = opposite(i);
                        fNext[opp][x][y] = f[i][x][y];
                    } else {
                        fNext[i][nx][ny] = f[i][x][y];
                    }
                }
            }
        }
        
        // Swap buffers
        for (int i = 0; i < 9; i++) {
            for (int x = 0; x < width; x++) {
                System.arraycopy(fNext[i][x], 0, f[i][x], 0, height);
            }
        }

        // 2. Collision
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (solid[x][y]) continue;

                // Compute macroscopic rho and u
                float rho = 0;
                float ux = 0;
                float uy = 0;
                for (int i = 0; i < 9; i++) {
                    rho += f[i][x][y];
                    ux += f[i][x][y] * ex[i];
                    uy += f[i][x][y] * ey[i];
                }
                ux /= rho;
                uy /= rho;

                // Force inlet velocity on left side
                if (x == 0) {
                   ux = uInlet;
                   uy = 0;
                   rho = 1.0f;
                }

                // Compute equilibrium and relax
                for (int i = 0; i < 9; i++) {
                    float u_dot_e = ux * ex[i] + uy * ey[i];
                    float u_sq = ux * ux + uy * uy;
                    float feq = w[i] * rho * (1 + 3 * u_dot_e + 4.5f * u_dot_e * u_dot_e - 1.5f * u_sq);
                    f[i][x][y] = f[i][x][y] + omega * (feq - f[i][x][y]);
                }
            }
        }
    }

    private int opposite(int i) {
        switch (i) {
            case 1: return 3;
            case 2: return 4;
            case 3: return 1;
            case 4: return 2;
            case 5: return 7;
            case 6: return 8;
            case 7: return 5;
            case 8: return 6;
            default: return 0;
        }
    }

    @Override
    public float[] getDensity() {
        float[] density = new float[width * height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                float rho = 0;
                for (int i = 0; i < 9; i++) {
                    rho += f[i][x][y];
                }
                density[y * width + x] = rho;
            }
        }
        return density;
    }

    @Override
    public float[] getVelocity() {
        float[] velocity = new float[width * height * 2];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                float rho = 0;
                float ux = 0;
                float uy = 0;
                for (int i = 0; i < 9; i++) {
                    rho += f[i][x][y];
                    ux += f[i][x][y] * ex[i];
                    uy += f[i][x][y] * ey[i];
                }
                velocity[2 * (y * width + x)] = ux / rho;
                velocity[2 * (y * width + x) + 1] = uy / rho;
            }
        }
        return velocity;
    }

    @Override
    public void addObstacle(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            solid[x][y] = true;
        }
    }

    @Override
    public String getName() {
        return "Lattice Boltzmann (D2Q9)";
    }
}
