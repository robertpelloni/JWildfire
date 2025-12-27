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
    - [ ] **API**: Reverse engineer or find documentation for the real Electric Sheep API.
- [ ] **Music Visualization**:
    - [x] **Scaffolding**: Created `org.jwildfire.visualizer` package with `Visualizer` interface.
    - [x] **UI**: Integrated `MusicVisualizerInternalFrame` with audio capture.
    - [x] **Implementation**: Created `SwingVisualizer` with spectrum and waveform rendering.
    - [ ] **projectM**: Investigate Project Panama (Java 21) for native binding to `libprojectM`.
    - [x] **OpenGL**: Connect `SimpleGLVisualizer` to a real OpenGL context (via `GLFWVisualizerRunner`).

#### Algorithm Expansion
- [ ] **Visions of Chaos**:
    - [ ] Perform Gap Analysis against VoC feature list.
    - [ ] Implement missing Attractors and 3D Fractal types.
- [ ] **Fractal Suites**: Analyze and integrate features from other major fractal/flame programs.
- [ ] **New Generators**: Expand the library of available variations and generators.
