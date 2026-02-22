# JWildfire TODO

## Immediate Tasks
- [ ] **Fix Missing Imports in `QuiltFlameRendererController`**: The `FlamePreparer` import is failing. Verify if the class was moved or renamed.
- [ ] **Verify `VariationProfilesFrame` Implementation**: Ensure that the getter methods (e.g., `getNewProfileBtn`) return valid objects or dummy objects that don't cause runtime null pointers if called by legacy code.
- [ ] **Implement `TinaInteractiveRendererControllerFX`**: Currently, it contains stub methods. The logic from the Swing controller needs to be ported or adapted to JavaFX.
- [ ] **Resolve `BatchRendererControllerFX` Progress Bar Issue**: The legacy `JobRenderThread` expects a `JProgressBar`. The current implementation returns a dummy or null. This needs a cleaner abstraction.

## Short Term - Documentation & Cleanup
- [ ] **Update User Manual**: Add a new chapter on the modernized JavaFX interface components.
- [ ] **Review `VISION.md` vs Reality**: Ensure the implementation aligns with the "Hybrid Modernization" philosophy.
- [ ] **Refactor `TinaController`**: The class is massive (`7000+` lines). Break it down into smaller, focused controllers or services.

## Medium Term - Feature Completion
- [ ] **Dancing Flames**: Complete the JavaFX modernization. Currently, it's in a hybrid state.
- [ ] **Quilt Renderer**: Complete the JavaFX modernization.
- [ ] **Mesh Generator**: Complete the JavaFX modernization.
- [ ] **Main Editor Tabs**: Migrate the remaining tabs (Transformations, Layers, Camera, Shading) to JavaFX.

## Long Term - Architecture
- [ ] **Dependency Injection**: Introduce a DI framework (like Guice or Spring) to manage the complex web of controllers and services.
- [ ] **Reactive Programming**: Adopt RxJava or JavaFX Properties more extensively for event handling.
- [ ] **Modularization**: Split the project into true Java modules (`module-info.java`).
