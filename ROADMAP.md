# JWildfire Roadmap

## Current Status
- **Version**: 9.01
- **Date**: 2025-12-25
- **Focus**: Documentation, Versioning, and Project Structure Analysis.

## Planned Features

### Short Term
- [x] Centralize versioning.
- [x] Create comprehensive documentation (Changelog, Roadmap, Project Structure).
- [x] Establish LLM instruction files.
- [ ] Implement automated version injection from `VERSION.md` into the build process.
- [ ] Review and update all submodules (if any are added in the future).

### Medium Term
- [ ] Refactor `Tools.java` to read version from a resource file instead of hardcoding.
- [ ] Add CI/CD pipeline configuration (e.g., GitHub Actions) to automate builds and releases.
- [ ] Improve test coverage and modernize test framework (JUnit 5).
- [ ] **Modernization**: Update core libraries and toolsets (Commons, LWJGL, etc.).

### Long Term
- [ ] **UI Overhaul**: Modernize the Swing UI, potentially exploring JavaFX hybrids or complete rewrites.
- [ ] **Performance**: Optimizations for rendering kernels.

### Future / Ambitious Goals
#### Integrations & Visualizers
- [ ] **Electric Sheep**: Integrate clients and algorithms. Explore "Sheep" rendering modes.
- [ ] **Music Visualization**: Integrate projectM, libprojectM, MilkDrop 2/3, and Geiss algorithms.
- [ ] **Real-time**: Leverage LWJGL for real-time visualization capabilities.

#### Algorithm Expansion
- [ ] **Visions of Chaos**: Analyze and implement algorithms and features found in VoC.
- [ ] **Fractal Suites**: Analyze and integrate features from other major fractal/flame programs.
- [ ] **New Generators**: Expand the library of available variations and generators.
