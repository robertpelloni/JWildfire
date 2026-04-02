package org.jwildfire.ca.math;

/**
 * Interface for Strange Attractor solvers (Visions of Chaos integration).
 */
public interface AttractorEngine {
    
    /**
     * Initializes the attractor with starting coordinates.
     */
    void init(double x, double y, double z);

    /**
     * Advances the simulation by one time step dt.
     */
    void step();

    /**
     * Returns the current x coordinate.
     */
    double getX();

    /**
     * Returns the current y coordinate.
     */
    double getY();

    /**
     * Returns the current z coordinate.
     */
    double getZ();

    /**
     * Name of the attractor (e.g. "Lorenz", "Rossler").
     */
    String getName();
}
