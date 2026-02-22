# JWildfire Roadmap

## Current Status
- **Version**: 9.07
- **Date**: 2026-02-09
- **Focus**: Deep Planning & Comprehensive Modernization.

## Planned Features

### Deep Planning Modernization (Current Focus)
**Goal:** Achieve 100% feature parity and documentation for the following modules, ensuring no "TODO" stubs remain.

- [x] **Easy Movie Maker**:
    - [x] Modernize UI to JavaFX.
    - [x] Implement Motion Curve editing UI.
    - [x] Complete Movie Render logic.
    - [ ] Full documentation in Manual.
- [ ] **Dancing Flames**:
    - [ ] Complete Project Load/Save logic.
    - [ ] Implement Random Flame Generation.
    - [ ] Full documentation in Manual.
- [ ] **Quilt Flame Renderer**:
    - [ ] Wire up Render/Cancel/Save logic.
    - [ ] Connect UI controls to Renderer.
    - [ ] Full documentation in Manual.
- [ ] **Mesh Generator**:
    - [ ] Implement OBJ/Voxel generation logic.
    - [ ] Wire up File Chooser and Progress UI.
    - [ ] Full documentation in Manual.
- [x] **Variation Profiles**:
    - [x] Modernize to JavaFX.
    - [x] Implement Add/Remove/Duplicate logic.
    - [x] Integrate with TinaController.
- [x] **GPU Renderer**:
    - [x] Modernize to JavaFX.
    - [x] Implement Rendering Logic.
    - [x] Documentation.
- [x] **Batch Renderer**:
    - [x] Modernize to JavaFX.
    - [x] Implement Job Queue Logic.
    - [x] Documentation.
- [x] **Script Editor**:
    - [x] Modernize to JavaFX.
    - [x] Syntax Highlighting.
    - [x] Documentation.
- [ ] **Main Editor Sub-Panels**:
    - [x] Modernize Gradient Editor to JavaFX.
    - [x] Modernize Coloring Tab to JavaFX.
    - [ ] Modernize Transformations Tab.
    - [ ] Modernize Layers Tab.
    - [ ] Modernize Camera Tab.
    - [ ] Modernize Shading Tab.

### Short Term
- [x] Centralize versioning.
- [x] Create comprehensive documentation (Changelog, Roadmap, Project Structure, Dashboard).
- [x] Establish LLM instruction files.
- [x] Refactor `Tools.java` to read version from a resource file instead of hardcoding.
- [x] Implement automated version injection from `VERSION.md` into the build process.

### Medium Term
- [x] Add CI/CD pipeline configuration (GitHub Actions) to automate builds and tests.
- [x] Improve test coverage and modernize test framework (Started JUnit 5 migration).
- [x] **Modernization**: Update core libraries and toolsets (Commons, LWJGL, JTransforms, Janino, etc.).

### Long Term
- [ ] **UI Overhaul**: Modernize the Swing UI, potentially exploring JavaFX hybrids or complete rewrites.
- [ ] **Performance**: Optimizations for rendering kernels.

### Future / Ambitious Goals
#### Integrations & Visualizers
- [x] **Electric Sheep**:
    - [x] **Scaffolding**: Created `org.jwildfire.sheep` package with `SheepDownloader` and `SheepRenderer`.
    - [x] **UI**: Modernized `ElectricSheepInternalFrame` with JavaFX, including settings, browser, and preview tabs.
    - [x] **Logic**: Implemented `SheepRenderer` using `GPURendererFactory`.
    - [x] **Downloader**: Implemented robust mock downloader with sample file support.
    - [x] **API**: Researched protocol and implemented `SheepServer` for list retrieval.
- [x] **Music Visualization**:
    - [x] **Scaffolding**: Created `org.jwildfire.visualizer` package with `Visualizer` interface.
    - [x] **UI**: Modernized `MusicVisualizerInternalFrame` with JavaFX controls, real-time preview canvas, and external launcher buttons.
    - [x] **Implementation**: Created `SwingVisualizer` with spectrum and waveform rendering.
    - [x] **projectM**: Created `ProjectMBinding` using Java 21 Foreign Function & Memory API (Preview).
    - [x] **OpenGL**: Connect `SimpleGLVisualizer` to a real OpenGL context (via `GLFWVisualizerRunner`).

#### Algorithm Expansion
- [x] **Visions of Chaos Gap Analysis**:
    - **Findings**: VoC uses dedicated engines (Raymarching, ODE Solver) vs JWildfire's IFS/Point Cloud approach.
    - **Goal**: Implement a **Generic ODE Solver** variation to support arbitrary attractor equations (parsing user input).
        - *Status*: Implemented `OdeIntegrationVariation` (Lorenz/Rossler/Aizawa + Custom Janino support).
    - [x] **Goal**: Explore a **Raymarching Renderer** (possibly via the new OpenGL module) for solid 3D fractals.
        - *Status*: Implemented `RaymarchingVisualizer` with GLSL shader support (Mandelbulb).
- [ ] **Fractal Suites**: Analyze and integrate features from other major fractal/flame programs.
- [ ] **New Generators**: Expand the library of available variations and generators.
