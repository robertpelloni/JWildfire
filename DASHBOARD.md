# JWildfire Project Dashboard

## Project Structure

```
j-wildfire/
├── src/                    # Source code
│   └── org/jwildfire/
│       ├── base/           # Core utilities and math
│       ├── create/         # Generators (Fractals, etc.)
│       │   └── tina/       # Main Editor (Tina) logic
│       │       ├── base/   # Flame object model
│       │       ├── render/ # Rendering engines (CPU/GPU)
│       │       └── swing/  # UI Controllers (Legacy & Hybrid)
│       └── swing/          # General UI components
├── lib/                    # Local dependencies (legacy)
├── resources/              # Assets (Icons, FXML, etc.)
├── build.gradle            # Build configuration
└── ...
```

## Submodules & External Components

| Component | Version | Location | Description |
| :--- | :--- | :--- | :--- |
| **JWildfire Core** | 9.07 | `/` | Main application repository. |
| **LWJGL** | 3.3.3 | Maven | Lightweight Java Game Library (OpenGL/OpenCL). |
| **JTransforms** | 3.1 | Maven | Fast Fourier Transforms. |
| **Janino** | 3.1.10 | Maven | Runtime Java Compiler (for script variations). |
| **ControlsFX** | 11.2.1 | Maven | Advanced JavaFX controls (PropertySheet, etc.). |
| **SVG Salamander** | Local | `lib/` | SVG Rendering support. |

## Build Status
- **Latest Build**: Successful (with warnings).
- **Java Version**: 21
- **Gradle Version**: 8.5+

## Deployment
- **Main Class**: `org.jwildfire.swing.JWildfire`
- **Run Command**: `./gradlew run`
- **Distribution**: `./gradlew installDist` (creates `build/install/j-wildfire`)
