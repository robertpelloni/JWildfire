# JWildfire Project Vision

## Ultimate Goal
The ultimate goal of JWildfire is to be the premier, open-source, cross-platform software for creating, rendering, and animating flame fractals and other generative art. It aims to bridge the gap between powerful, complex mathematical algorithms and an intuitive, user-friendly artistic workflow.

## Core Philosophy
1.  **Hybrid Modernization**: We acknowledge the robust legacy of Swing while embracing the modern capabilities of JavaFX. The application will evolve into a hybrid system where heavy lifting and legacy stability are maintained by Swing, while new UI components, interactive previews, and complex controls are built with JavaFX for a superior user experience.
2.  **No Feature Left Behind**: Every feature, parameter, and option available in the backend or mathematical core must be exposed to the user via the UI. There should be no "hidden" functionality.
3.  **Comprehensive Documentation**: The software should be self-documenting via tooltips and context-sensitive help, backed by a complete and up-to-date manual.
4.  **Performance & Quality**: Leveraging modern hardware (GPU via LWJGL/OpenCL) to provide real-time feedback and high-quality final renders.

## Architectural Vision
-   **UI Layer**: A seamless blend of `JInternalFrame` (window management) hosting `JFXPanel` (content).
-   **Service Layer**: Decoupled "Services" (e.g., `MutaGenService`, `FlameBrowserService`) that contain business logic, callable by both legacy Swing and new JavaFX controllers.
-   **Render Layer**: Abstracted rendering interfaces allowing for CPU (Java), GPU (OpenCL), and remote (Network/Cloud) rendering backends.

## User Experience
-   **Deep Planning Mode**: All development follows a strict protocol of understanding user requirements, verifying assumptions, and executing with 100% completeness.
-   **Visual Feedback**: Immediate visual response to parameter changes (Real-time render previews).
-   **Discovery**: Features should be discoverable through well-organized menus, search functionality, and integrated "Tip of the Day" or "Walkthrough" modes.

## Future Roadmap Highlights
-   **Fully Hybrid Interface**: Complete migration of all major editors (Editor, Batch Renderer, Movie Maker) to JavaFX.
-   **Cloud Integration**: Seamless sharing of fractal parameters and renders with community servers (e.g., Electric Sheep, JWildfire Community).
-   **AI Assistance**: Integration of local LLMs or heuristics to suggest mutations, color palettes, or render settings based on user preference.
-   **Complete Documentation**: Every single parameter and feature must be documented in the manual and tooltips.
-   **100% Feature Coverage**: Ensure all theoretical features of the fractal flame algorithm are implemented and exposed.
