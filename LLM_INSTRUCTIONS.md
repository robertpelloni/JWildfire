# Universal LLM Instructions

This file contains universal instructions for all AI models working on the JWildfire project.

## Project Context
- **Project Name**: JWildfire
- **Language**: Java (Version 21)
- **Build Systems**: Gradle, Maven (Dual support)
- **Key Frameworks**: 
  - **UI**: Hybrid Swing (JInternalFrames) / JavaFX (JFXPanel, FXML)
  - **Graphics**: LWJGL 3 (OpenGL/OpenCL), Java 2D
  - **Audio**: `javax.sound.sampled`
- **New Modules**:
  - `org.jwildfire.sheep`: Electric Sheep integration (JavaFX UI, Downloader, Renderer).
  - `org.jwildfire.visualizer`: Music Visualization (JavaFX UI, AudioCapture, Swing/GL Visualizers).

## Coding Standards
- **Indentation**: Use 2 spaces for indentation.
- **Encoding**: UTF-8.
- **Style**: Follow existing patterns in `src/org/jwildfire`. Prefer clear variable names and Javadoc for public methods.
- **UI Modernization**: Prefer JavaFX (`JFXPanel` inside `JInternalFrame`) for new or refactored UI components. Use `fxml` for layout and `Controller` classes for logic.

## Versioning Protocol
- **Source of Truth**: `VERSION.md`.
- **Access**: `org.jwildfire.base.Tools.APP_VERSION` reads from `resources/app-version.txt` (generated during build).
- **Update Process**:
  1. Modify `VERSION.md`.
  2. Update `CHANGELOG.md` with the new version and date.
  3. Commit with message: "Bump version to X.XX".

## Documentation
- **Roadmap**: Update `ROADMAP.md` immediately upon completing or starting a feature.
- **Structure**: Update `PROJECT_STRUCTURE.md` if adding new packages or directories.
- **Changelog**: Log every significant change in `CHANGELOG.md`.

## Git Workflow
- **Commits**: Descriptive messages. Reference issue IDs if applicable.
- **Submodules**: Always check for submodule updates (`git submodule update --init --recursive`) before starting work.
- **Sync**: Pull frequently to avoid conflicts.

## Feature Specifics
- **Electric Sheep**:
  - Located in `org.jwildfire.sheep`.
  - **UI**: `ElectricSheepInternalFrame` (Swing wrapper) -> `electric_sheep.fxml` / `ElectricSheepController` (JavaFX).
  - **Logic**: `SheepDownloader` (supports Render Job fetching) and `SheepRenderer` (wraps `GPURenderer`).
  - **Server**: `SheepServer` handles communication with `sheepserver.net`.
- **Music Visualizer**:
  - Located in `org.jwildfire.visualizer`.
  - **UI**: `MusicVisualizerInternalFrame` (Swing wrapper) -> `music_visualizer.fxml` / `MusicVisualizerController` (JavaFX).
  - **Logic**: `AudioCapture` handles microphone input/FFT. `Visualizer` interface allows swapping backends (Swing vs OpenGL).

## Agent/Model Specifics
- **Claude**: Focus on architectural consistency and detailed documentation.
- **Gemini**: Leverage large context window for deep code analysis.
- **GPT**: Focus on code generation and refactoring.
- **Copilot**: Focus on inline code completion and immediate task execution.
