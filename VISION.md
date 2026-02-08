JWildfire is a modern Java-based fractal flame editor and renderer, heavily inspired by the pioneering work of Scott Draves (Electric Sheep) and the Chaos movement.

## Ultimate Vision

The goal of JWildfire is to be the premier, cross-platform, open-source tool for creating, exploring, and rendering "Flame" fractals. It aims to bridge the gap between artistic intuition and mathematical precision.

### Core Pillars
1.  **Hybrid Architecture**: Leveraging the stability of Swing for legacy compatibility while aggressively modernizing new interfaces with JavaFX for a rich, responsive user experience.
2.  **Performance**: Utilizing modern hardware acceleration (GPU/OpenCL via LWJGL 3) to render complex fractals in real-time.
3.  **Extensibility**: Providing a robust plugin system (Variations, Scripts) powered by runtime compilation (Janino) to allow users to define their own mathematical formulas.
4.  **Integration**: Seamlessly connecting with the wider fractal ecosystem (Electric Sheep distributed rendering, Music Visualization via ProjectM/MilkDrop).

### Design Philosophy
- **"Everything is a Parameter"**: Every aspect of the fractal generation process should be exposed to the user, from the core affine transforms to the post-processing filters.
- **Visual Feedback**: Immediate visual feedback is critical. The UI should prioritize real-time previews (JavaFX Canvas) over static inputs.
- **Community Driven**: Features like the "Tip of the Day", "Online Gallery", and "Electric Sheep" integration foster a sense of community and sharing.

### Future Roadmap (High Level)
- **Full JavaFX Migration**: Eventual replacement of all Swing components with JavaFX.
- **Cloud Rendering**: Integration with cloud services for massive-scale rendering jobs.
- **VR/AR Support**: Exploring fractal visualization in immersive environments.
