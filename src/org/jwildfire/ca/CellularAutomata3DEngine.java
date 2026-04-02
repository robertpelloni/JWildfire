package org.jwildfire.ca;

/**
 * Interface for 3D Cellular Automata engines (Visions of Chaos integration).
 */
public interface CellularAutomata3DEngine {
    
    /**
     * Initializes the 3D grid.
     * @param width
     * @param height
     * @param depth
     */
    void init(int width, int height, int depth);

    /**
     * Advances the simulation by one generation.
     */
    void tick();

    /**
     * Returns the current 3D grid state as a flattened array.
     * @return Flattened array of size width*height*depth.
     */
    int[] getGridState();

    /**
     * Returns the name of the engine.
     */
    String getName();

    /**
     * Resets the grid.
     */
    void randomize();
}
