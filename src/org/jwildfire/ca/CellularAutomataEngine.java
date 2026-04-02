package org.jwildfire.ca;

/**
 * The base interface for all Visions of Chaos (VoC) inspired Cellular Automata engines.
 */
public interface CellularAutomataEngine {
    
    /**
     * Initializes the grid/state.
     * @param width  The width of the grid
     * @param height The height of the grid
     */
    void init(int width, int height);

    /**
     * Advances the simulation by one generation/tick.
     */
    void tick();

    /**
     * Returns the current state of the grid as a flattened array for fast rendering.
     * Often represents pixel colors or cell states.
     * @return Flattened integer array of states
     */
    int[] getGridState();

    /**
     * Retrieves the name of this specific automaton (e.g., "Conway's Game of Life", "Hodgepodge").
     * @return String name
     */
    String getName();

    /**
     * Resets the simulation to an initial random state.
     */
    void randomize();
}
