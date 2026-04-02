package org.jwildfire.ca.fluid;

/**
 * Interface for fluid simulation engines in JWildfire (Visions of Chaos integration).
 */
public interface FluidEngine {
    
    /**
     * Initializes the simulation.
     * @param width  The width of the simulation domain
     * @param height The height of the simulation domain
     */
    void init(int width, int height);

    /**
     * Advances the simulation by one time step.
     */
    void step();

    /**
     * Returns the current density field as a flattened array.
     * @return Array of density values
     */
    float[] getDensity();

    /**
     * Returns the current velocity field as a flattened array (alternating x, y).
     * @return Array of velocity vectors
     */
    float[] getVelocity();

    /**
     * Adds an obstacle (solid cell) at the given location.
     * @param x
     * @param y
     */
    void addObstacle(int x, int y);

    /**
     * Retrieves the name of this fluid solver.
     * @return String name
     */
    String getName();
}
