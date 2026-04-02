# JWildfire Changelog

All notable changes to this project will be documented in this file.

## [9.09] - 2026-04-01

### Added
- **Omni-Workspace Architecture**:
    - Created `INTEGRATION_PLAN.md` mapping out the comprehensive integration of apophysis-j, BeatDrop, electricsheep, geiss, MilkDrop3, projectm, and Visions of Chaos into JWildfire.
    - **Unified Audio Pipeline**:
        - Added `AudioListener.java` interface for universal audio data distribution.
        - Refactored `AudioCapture.java` into a Pub/Sub event emitter, broadcasting real-time `pcmData` and `spectrumData` to all subscribed modules.
    - **FFM Native Bindings**:
        - Extended `ProjectMBinding.java` with Java 21 FFM API to marshal Java `float[]` arrays to native C `projectm_pcm_add_float`.
        - Wired `ProjectMVisualizer.java` to push real-time audio data into the native renderer context.

## [9.08] - 2026-02-09

### Added
- **Modernized Quilt Flame Renderer**:
    - Complete JavaFX implementation of UI (`QuiltFlameRendererController`, `quilt_flame_renderer.fxml`).
    - Implemented rendering logic with progress tracking and cancellation.
- **Modernized Mesh Generator**:
    - Complete JavaFX implementation of UI (`MeshGenInternalControllerFX`, `mesh_gen_internal.fxml`).
    - Implemented "Generate Slices" and "Generate Mesh" workflows.
    - Added "Slices Per Pass" configuration to UI.

### Changed
- **Documentation**: Updated `ROADMAP.md` and `TODO.md` to reflect completion of Quilt Renderer and Mesh Generator.

## [9.07] - 2026-02-09

### Added
- **Modernized Main Editor Sub-Panels**:
    - **Coloring Tab**: Replaced Swing UI with a fully functional JavaFX implementation (`ColoringControllerFX.java`).
    - **Gradient Editor**: Implemented a new JavaFX gradient editor with preview.
- **Modernized Rendering Frames**:
    - **GPU Renderer**: Replaced Swing UI with a hybrid Swing/JavaFX implementation (`FlamesGPURenderFrame` / `FlamesGPURenderControllerFX`).
    - **Batch Renderer**: Replaced Swing UI with a hybrid Swing/JavaFX implementation (`BatchFlameRendererFrame` / `BatchRendererControllerFX`).
    - **Easy Movie Maker**: Replaced Swing UI with a hybrid Swing/JavaFX implementation (`EasyMovieMakerFrame` / `EasyMovieMakerController`).
- **Variation Profiles**:
    - Converted `VariationProfilesFrame` to use JavaFX.
- **Backend Refactoring**:
    - Introduced `ITinaInteractiveRendererController` to abstract renderer operations, allowing seamless switching between Swing and JavaFX implementations.
    - Updated `TinaController` to use the new interface.
    - Added compatibility stubs to legacy frames to ensure compilation and backward compatibility during the transition phase.

### Changed
- **Build System**: Resolved multiple compilation errors related to missing fields and methods during the Swing-to-JavaFX transition.
- **Documentation**: Updated `VISION.md`, `ROADMAP.md`, `AGENTS.md` and `TODO.md` with detailed deep analysis and future plans.

## [9.06] - 2026-02-09

### Planned (In Progress)
- **Deep Planning Modernization**:
    - **Easy Movie Maker**: Complete JavaFX implementation of motion curves and movie rendering.
    - **Dancing Flames**: Complete JavaFX implementation of project management and random generation.
    - **Quilt Renderer**: Complete JavaFX implementation of tiling rendering logic.
    - **Mesh Generator**: Complete JavaFX implementation of voxel/OBJ export.
    - **Variation Profiles**: Modernized UI for managing variation sets (JavaFX).
    - **GPU Renderer**: Modernized UI for controlling FAEngine (JavaFX).
    - **Batch Renderer**: Modernized UI for background rendering queues (JavaFX).
    - **Script Editor**: Modernized UI with syntax highlighting (JavaFX).
    - **Gradient Editor**: Modernized UI embedded in Main Editor (JavaFX).
    - **Coloring Tab**: Modernized UI embedded in Main Editor (JavaFX).
- **Documentation**:
    - Added `VISION.md`.
    - Updating Manual with new chapters for modernized modules.

## [9.05] - 2026-02-08

### Added
- **Modernized Electric Sheep UI**:
    - Replaced Swing UI with JavaFX (`electric_sheep.fxml`).
    - Added "Browser", "Settings", and "Help" tabs.
    - Implemented real server communication for fetching "RENDER_JOB"s.
    - Added configuration for nickname, server URL, and cache directory.
    - Added `help.html` documentation.
- **Modernized Music Visualizer UI**:
    - Replaced Swing UI with JavaFX (`music_visualizer.fxml`).
    - Added "Visualizer" tab with real-time `Canvas` preview and "Help" tab.
    - Added Audio Device selection combo box.
    - Added Sensitivity and Gain sliders.
    - Added "Launcher" buttons for OpenGL, ProjectM, and Raymarching visualizers.
    - Added `help.html` documentation.
- **Backend Improvements**:
    - Updated `SheepServer` to handle SSL connections to community servers (permissive trust).
    - Updated `AudioCapture` to support device enumeration (`Mixer.Info`) and signal scaling.
    - Updated `SheepDownloader` to handle "RENDER_JOB" fetching.

### Changed
- **Dependencies**:
    - Fixed CI build failure by reverting `svg-salamander` to local `lib/` JAR due to Maven Central unavailability.
    - Updated `build.gradle` to exclude `svgSalamander.jar` from fileTree exclusion list.
    - Updated `ROADMAP.md` and `PROJECT_STRUCTURE.md` to reflect modernized modules.

## [9.04] - 2025-12-27

### Added
- **Project Dashboard**: Created `DASHBOARD.md` to track components and versions.
- **LLM Instructions**: Added `LLM_INSTRUCTIONS.md` and agent-specific files (`CLAUDE.md`, `AGENTS.md`).
- **Versioning**: Centralized version source of truth to `VERSION.md`.
- **CI/CD**: Added GitHub Actions workflow (`gradle.yml`).

### Changed
- **Build System**: Refactored `Tools.java` to read version from resource file.
- **Documentation**: Updated `README.md` and created structure docs.

## [9.03] - Legacy

### Added
- Initial support for Electric Sheep and Music Visualizer (Swing prototypes).
- Integration of `JTransforms`, `Janino`, `JOML`.

### Changed
- Massive refactoring for Java 21 support.
- Removal of Applet support.
