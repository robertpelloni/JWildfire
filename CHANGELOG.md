# Changelog

All notable changes to this project will be documented in this file.

## [9.04] - 2025-12-27

### Added
- **Dashboard**: Created `DASHBOARD.md` to list all components, versions, and project structure details.

### Changed
- **Dependencies**: Merged upstream updates (Logback 1.2.13 in Maven build).
- **Documentation**: Updated `PROJECT_STRUCTURE.md` and `ROADMAP.md`.

## [9.03] - 2025-12-27

### Added
- **Electric Sheep Integration**:
    - Implemented full API client (`SheepServer`) to fetch flock lists and render jobs.
    - Added "Edit in JWildfire" button to load downloaded sheep directly into the editor.
    - Connected `SheepRenderer` to the main `GPURendererFactory`.
- **Music Visualizer**:
    - Added **projectM** support via Java 21 Foreign Function & Memory API (Native Binding).
    - Added **Raymarching Visualizer** with GLSL shader support.
    - Added UI controls to switch between Mandelbulb and Sphere shaders.
- **Visions of Chaos Gap Features**:
    - Added **ODE Solver Variation** (`OdeIntegrationVariation`) for Strange Attractors (Lorenz, Rossler, Aizawa).
    - Added **Janino Compiler** integration to support custom user-defined ODE equations at runtime.
- **Infrastructure**:
    - Added GitHub Actions CI/CD pipeline (`gradle.yml`).
    - Added automated version injection from `VERSION.md` into the build process.

### Changed
- **Modernization**:
    - Upgraded `JTransforms` from 2.4 to 3.1 (migrated to `org.jtransforms`).
    - Upgraded `SVG Salamander` to 1.1.3.
    - Explicitly defined dependencies for `Janino`, `JOML`, `JSON-IO`, and `SLF4J/Logback` in `build.gradle`.
    - Cleaned up `lib` folder dependencies to prefer Maven artifacts.
- **Documentation**:
    - Overhauled `LLM_INSTRUCTIONS.md` to serve as the universal source of truth for all AI agents.
    - Updated `PROJECT_STRUCTURE.md` to reflect the current state of the project.
    - Verified and standardized `CLAUDE.md`, `GEMINI.md`, `GPT.md`, and `copilot-instructions.md`.

## [9.02] - 2025-12-25

### Added
- **Electric Sheep Integration**:
    - Added `ElectricSheepInternalFrame` for UI interaction.
    - Added `SheepDownloader` (mock implementation) and `SheepRenderer` (stub).
- **Music Visualizer**:
    - Added `MusicVisualizerInternalFrame` with real-time audio capture.
    - Added `AudioCapture` using `javax.sound.sampled`.
    - Added `SimpleGLVisualizer` (LWJGL prototype).
- **Versioning**:
    - Added `resources/app-version.txt` as the source of truth for the runtime version.
    - Refactored `Tools.java` to read the version from the resource file.

## [9.01] - 2025-12-25

### Added
- Created `VERSION.md` to track the project version.
- Created `CHANGELOG.md` to track changes.
- Created `ROADMAP.md` to outline future plans.
- Created `PROJECT_STRUCTURE.md` to document the project layout and submodules.
- Created `LLM_INSTRUCTIONS.md` and related files for AI agent instructions.

### Changed
- Updated project version to 9.01 across `pom.xml`, `build.gradle`, and `Tools.java`.
- Synchronized version numbers.
- Updated `ROADMAP.md` with ambitious goals (Electric Sheep, projectM, Visions of Chaos).
- Modernized `build.gradle`:
    - Added JUnit 5 support (Jupiter + Vintage engine).
    - Updated `commons-io` to 2.14.0.

## [9.00] - 2025-10-01
- Previous release (inferred from code).
