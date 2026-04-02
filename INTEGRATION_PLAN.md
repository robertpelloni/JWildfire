# Omni-Workspace Integration Plan: JWildfire as the Universal Visualizer Hub

## Objective
To achieve 100% feature and function parity by integrating all standalone visualizers (`apophysis-j`, `BeatDrop`, `electricsheep`, `geiss`, `MilkDrop3`, `projectm`) into the `JWildfire` platform, transforming it into the ultimate "Bobsaver" suite.

## Strategic Approach: Hybrid Native Orchestration
Rewriting decades of optimized C++ graphics code (e.g., MilkDrop, Geiss) into pure Java is mathematically and computationally unfeasible without severe performance loss. Instead, JWildfire will serve as a **Universal Orchestrator**, utilizing Java 21's **Foreign Function & Memory (FFM) API (Project Panama)** to securely and performantly bind to the native engines.

### Phase 1: Native Engine Encapsulation (C/C++ Projects)
We will compile the C/C++ visualizers as dynamic shared libraries (`.dll`, `.so`, `.dylib`) rather than standalone executables.
*   **projectm & MilkDrop3**: Extend the existing `ProjectMBinding.java` proof-of-concept. Expose a unified C-API that allows JWildfire to initialize contexts, push audio (FFT/PCM) data, trigger preset changes, and retrieve rendered frames (via OpenGL texture sharing).
*   **geiss & BeatDrop**: Refactor the DirectX/OpenGL presentation layers to operate in "headless" or "texture-render" modes. Expose parameters via FFM.
*   **electricsheep**: Expand `SheepRenderer.java` and `SheepDownloader.java`. Bind the `electricsheep` C-core as a background native renderer that receives frame requests and returns fractal image data, while JWildfire handles the network communication and caching layer.

### Phase 2: Algorithm & Logic Merging (Java Projects)
*   **apophysis-j**: Since Apophysis-J is also Java-based, we will perform a direct source-level integration. We will map Apophysis's unique fractal formulas, variations, and mutation logic directly into JWildfire's `org.jwildfire.transform` and `org.jwildfire.base` packages, ensuring no mathematical capability is lost.

### Phase 3: The Unified "Omni-Viz" Architecture
1.  **Unified Audio Pipeline**: JWildfire will implement a centralized audio loopback capture service. This service will compute FFT and PCM streams once and distribute the data to all active visualizer plugins (Java or Native) simultaneously.
2.  **Shared Render Context**: Use LWJGL (Lightweight Java Game Library) to create a master OpenGL/Vulkan context within JWildfire's JavaFX UI. The native libraries will be handed this context (or shared textures) to render directly onto JWildfire's canvases.
3.  **100% UI Parity**: JWildfire's JavaFX dashboard will dynamically generate control panels for each visualizer based on a standardized parameter schema. Every shader parameter, configuration tweak, and preset list from the original projects will be exposed.

## Execution Steps for Current Session
Given the massive scope, the immediate next steps involve scaffolding the core architecture:
1.  **Finalize the FFM Pipeline**: Solidify `ProjectMBinding` to ensure we can successfully initialize a native visualizer and send audio data.
2.  **Audio Capture Engine**: Build the unified audio capture service in JWildfire.
3.  **Apophysis-J Analysis**: Map the specific formulas in `apophysis-j/src` that are missing from JWildfire.

**Status**: Ready for approval to proceed with Phase 1 and Phase 2 foundations.