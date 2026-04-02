package org.jwildfire.ca.math;

/**
 * Lorenz Attractor engine.
 * A core feature of Visions of Chaos (VoC) strange attractor library.
 */
public class LorenzAttractorEngine implements AttractorEngine {
    
    private double x, y, z;
    private final double dt = 0.01;
    private final double sigma = 10.0;
    private final double rho = 28.0;
    private final double beta = 8.0/3.0;

    @Override
    public void init(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void step() {
        double dx = (sigma * (y - x)) * dt;
        double dy = (x * (rho - z) - y) * dt;
        double dz = (x * y - beta * z) * dt;
        
        x += dx;
        y += dy;
        z += dz;
    }

    @Override
    public double getX() { return x; }

    @Override
    public double getY() { return y; }

    @Override
    public double getZ() { return z; }

    @Override
    public String getName() {
        return "Lorenz Attractor";
    }
}
