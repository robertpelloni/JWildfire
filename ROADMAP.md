# JWildfire Roadmap

## Current Status
- **Version**: 9.02
- **Date**: 2025-12-25
- **Focus**: Feature Implementation (Electric Sheep, Music Visualizer).

## Planned Features

### Short Term
- [x] Centralize versioning.
- [x] Create comprehensive documentation (Changelog, Roadmap, Project Structure).
- [x] Establish LLM instruction files.
- [x] Refactor `Tools.java` to read version from a resource file instead of hardcoding.
- [ ] Implement automated version injection from `VERSION.md` into the build process.

### Medium Term
- [ ] Add CI/CD pipeline configuration (e.g., GitHub Actions) to automate builds and releases.
- [ ] Improve test coverage and modernize test framework (JUnit 5).
- [ ] **Modernization**: Update core libraries and toolsets (Commons, LWJGL, etc.).

### Long Term
- [ ] **UI Overhaul**: Modernize the Swing UI, potentially exploring JavaFX hybrids or complete rewrites.
- [ ] **Performance**: Optimizations for rendering kernels.

### Future / Ambitious Goals
#### Integrations & Visualizers
- [ ] **Electric Sheep**:
    - [x] **Scaffolding**: Created `org.jwildfire.sheep` package with `SheepDownloader` and `SheepRenderer`.
    - [x] **UI**: Integrated `ElectricSheepInternalFrame` into the main application.
    - [x] **Logic**: Implemented `SheepRenderer` using `GPURendererFactory`.
    - [x] **Downloader**: Implemented robust mock downloader with sample file support.
    - [x] **API**: Researched protocol and implemented `SheepServer` for list retrieval.
- [ ] **Music Visualization**:
    - [x] **Scaffolding**: Created `org.jwildfire.visualizer` package with `Visualizer` interface.
    - [x] **UI**: Integrated `MusicVisualizerInternalFrame` with audio capture.
    - [x] **Implementation**: Created `SwingVisualizer` with spectrum and waveform rendering.
    - [x] **projectM**: Created `ProjectMBinding` using Java 21 Foreign Function & Memory API (Preview).
    - [x] **OpenGL**: Connect `SimpleGLVisualizer` to a real OpenGL context (via `GLFWVisualizerRunner`).

#### Algorithm Expansion
- [ ] **Visions of Chaos Gap Analysis**:
    - **Findings**: VoC uses dedicated engines (Raymarching, ODE Solver) vs JWildfire's IFS/Point Cloud approach.
    - **Goal**: Implement a **Generic ODE Solver** variation to support arbitrary attractor equations (parsing user input).
        - *Status*: Implemented `OdeIntegrationVariation` (Lorenz/Rossler support).
    - [x] **Goal**: Explore a **Raymarching Renderer** (possibly via the new OpenGL module) for solid 3D fractals.
        - *Status*: Implemented `RaymarchingVisualizer` with GLSL shader support.
- [ ] **Fractal Suites**: Analyze and integrate features from other major fractal/flame programs.
- [ ] **New Generators**: Expand the library of available variations and generators.
