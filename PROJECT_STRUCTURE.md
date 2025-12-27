# JWildfire Project Structure Dashboard

## Overview
This document provides an overview of the JWildfire project structure, including submodules (components) and their locations.

**Current Version**: 9.03
**Build Date**: 2025-12-25

## Directory Structure

### Root Directory
- `build.gradle`: Gradle build configuration.
- `pom.xml`: Maven build configuration.
- `VERSION.md`: Current project version.
- `CHANGELOG.md`: History of changes.
- `ROADMAP.md`: Future plans.
- `README.md`: General project information.
- `LLM_INSTRUCTIONS.md`: Universal instructions for AI agents.

### Source Code
- `src/`: Main source code directory (Java).
  - `org/jwildfire/`: Main package.
    - `base/`: Core utilities (e.g., `Tools.java`).
    - `create/`: Creation logic.
    - `sheep/`: **[NEW]** Electric Sheep integration (Downloader, Renderer, UI).
    - `swing/`: Swing UI components.
    - `visualizer/`: **[NEW]** Music Visualizer (Audio Capture, Visualizer Interface, UI).
- `test/`: Unit tests.

### Components / "Submodules"
Although not configured as Git submodules, the following directories represent distinct components or external dependencies:

| Component | Location | Description |
|-----------|----------|-------------|
| **Applet** | `applet/` | Web applet related files (legacy). |
| **CLI** | `cli/` | Command Line Interface scripts. |
| **Delphi** | `Delphi/` | Delphi related resources (legacy/interop). |
| **Denoiser** | `Denoiser/` | Denoising libraries (OIDN, OptiX). |
| **FARender** | `FARenderJWF/` | Flame Fractal rendering kernels (CUDA). |
| **GLSL** | `glsl/` | OpenGL Shading Language shaders. |
| **Libraries** | `lib/` | External JARs and native libraries. |
| **Manual** | `manual/` | User documentation and help files. |
| **Resources** | `resources/` | Assets, icons, default flames, etc. |

## Build Information
- **Build System**: Gradle (primary), Maven (supported).
- **Java Version**: 21 (as per `build.gradle`).
