# JWildfire Roadmap

## Current Status
- **Version**: 9.01
- **Date**: 2025-12-25
- **Focus**: Documentation, Versioning, and Project Structure Analysis.

## Planned Features

### Short Term
- [x] Centralize versioning.
- [x] Create comprehensive documentation (Changelog, Roadmap, Project Structure).
- [x] Establish LLM instruction files.
- [ ] Implement automated version injection from `VERSION.md` into the build process.
- [ ] Review and update all submodules (if any are added in the future).

### Medium Term
- [ ] Refactor `Tools.java` to read version from a resource file instead of hardcoding.
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
    - [ ] Obtain sample Sheep files for compatibility testing.
    - [ ] Implement "Sheep Downloader" module (Real API integration).
    - [ ] Create "Sheep Rendering" mode (looping, specific constraints).
- [ ] **Music Visualization**:
    - [x] **Scaffolding**: Created `org.jwildfire.visualizer` package with `Visualizer` interface.
    - [x] **UI**: Integrated `MusicVisualizerInternalFrame` with audio capture.
    - [x] **Prototype**: Created `SimpleGLVisualizer` using LWJGL.
    - [ ] **projectM**: Investigate Project Panama (Java 21) for native binding to `libprojectM`.
    - [ ] **Real-time**: Connect `SimpleGLVisualizer` to a real OpenGL context within the Swing frame.

#### Algorithm Expansion
- [ ] **Visions of Chaos**:
    - [ ] Perform Gap Analysis against VoC feature list.
    - [ ] Implement missing Attractors and 3D Fractal types.
- [ ] **Fractal Suites**: Analyze and integrate features from other major fractal/flame programs.
- [ ] **New Generators**: Expand the library of available variations and generators.
