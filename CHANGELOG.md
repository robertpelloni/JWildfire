# JWildfire Changelog

All notable changes to this project will be documented in this file.

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
