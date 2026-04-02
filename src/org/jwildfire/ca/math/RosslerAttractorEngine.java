package org.jwildfire.ca.math;

/**
 * Rossler Attractor engine.
 * A core feature of Visions of Chaos (VoC) strange attractor library.
 */
public class RosslerAttractorEngine implements AttractorEngine {
    
    private double x, y, z;
    private final double dt = 0.05;
    private final double a = 0.2;
    private final double b = 0.2;
    private final double c = 5.7;

    @Override
    public void init(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void step() {
        double dx = (-y - z) * dt;
        double dy = (x + a * y) * dt;
        double dz = (b + z * (x - c)) * dt;
        
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
        return "Rossler Attractor";
    }
}
