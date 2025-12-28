# JWildfire Project Dashboard

**Current Version**: 9.04
**Build Date**: 2025-12-27
**Build System**: Gradle (Primary), Maven (Legacy/Lib)

## Project Structure & Components

This dashboard lists all major components ("submodules") of the JWildfire project, their locations, and current versions.

### Core Application
| Component | Location | Version | Description |
|-----------|----------|---------|-------------|
| **JWildfire** | `src/org/jwildfire` | 9.04 | Main application code (Swing UI, Renderers). |
| **Launcher** | `src/org/jwildfire/swing/JWildfire.java` | - | Main entry point. |

### Modules & Integrations
| Component | Location | Status | Description |
|-----------|----------|--------|-------------|
| **Electric Sheep** | `src/org/jwildfire/sheep` | Active | Integration with Electric Sheep (Downloader, Renderer). |
| **Music Visualizer** | `src/org/jwildfire/visualizer` | Active | Audio visualization module (projectM, GLSL). |
| **Scripting** | `src/org/jwildfire/base/Tools.java` | Active | Internal scripting and utility tools. |

### External Components (Non-Git Submodules)
These directories contain external tools or libraries integrated into the project structure.

| Component | Location | Version | Notes |
|-----------|----------|---------|-------|
| **Denoiser** | `Denoiser/` | - | Contains OIDN and OptiX denoiser binaries/licenses. |
| **FARender** | `FARenderJWF/` | - | CUDA kernels for Flame rendering. |
| **GLSL Shaders** | `glsl/` | - | Collection of fragment/vertex shaders. |
| **CLI Tools** | `cli/` | - | Shell scripts for headless rendering. |
| **Applet** | `applet/` | Legacy | Web applet resources (deprecated). |
| **Delphi** | `Delphi/` | Legacy | Old Delphi interoperability code. |

### Key Dependencies (Gradle)
Versions defined in `build.gradle`.

| Library | Version | Purpose |
|---------|---------|---------|
| **Java** | 21 | Core Language Level. |
| **LWJGL** | 3.3.3 | OpenGL/OpenCL bindings. |
| **JavaFX** | 21.0.2 | Embedded web view and modern UI components. |
| **JTransforms** | 3.1 | FFT and math utilities. |
| **Janino** | 3.1.10 | Runtime Java compiler (for custom variations). |
| **JOML** | 1.10.5 | Java OpenGL Math Library. |
| **JSON-IO** | 4.14.0 | JSON serialization. |
| **Logback** | 1.4.11 | Logging framework. |
| **SVG Salamander** | 1.1.3 | SVG rendering. |

### Key Dependencies (Maven)
Versions defined in `pom.xml` (Legacy Build).

| Library | Version | Purpose |
|---------|---------|---------|
| **Logback** | 1.2.13 | Logging (Legacy). |
| **Janino** | 2.5.15 | Compiler (Legacy). |
| **JTransforms** | 2.4 | Math (Legacy). |

## Directory Layout Explanation

- **`src/`**: The heart of the application. Contains all Java source code.
- **`resources/`**: Non-code assets.
    - `flames/`: Example fractals.
    - `app-version.txt`: Source of truth for version number.
- **`lib/`**: Local JAR files not available in Maven Central (or legacy overrides).
- **`manual/`**: Documentation source files.
- **`test/`**: Unit and integration tests.

## Recent Updates
- **2025-12-27**: Merged upstream dependency updates (Logback 1.2.13).
- **2025-12-27**: Updated project version to 9.04.
- **2025-12-27**: Created this Dashboard.

