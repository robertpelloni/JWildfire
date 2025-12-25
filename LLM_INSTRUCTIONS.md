# Universal LLM Instructions

This file contains universal instructions for all AI models working on the JWildfire project.

## Project Context
- **Project Name**: JWildfire
- **Language**: Java (Version 21)
- **Build Systems**: Gradle, Maven
- **Key Frameworks**: Swing, JavaFX, LWJGL

## Coding Standards
- **Indentation**: Use 2 spaces for indentation (as seen in `Tools.java`).
- **Encoding**: UTF-8.
- **Versioning**: 
  - Always check `VERSION.md` for the current version.
  - When making changes that affect the release, update `CHANGELOG.md`.
  - Increment `VERSION.md` for new builds/releases.
  - Ensure `pom.xml`, `build.gradle`, and `Tools.java` are synchronized with `VERSION.md`.

## Documentation
- Update `ROADMAP.md` when completing features.
- Update `PROJECT_STRUCTURE.md` if adding new modules or directories.

## Git Workflow
- Commit messages should be descriptive.
- Reference the version number in commit messages when bumping versions.

## Specific Tasks
- **Changelog**: Always add an entry under the "Unreleased" or current version section in `CHANGELOG.md` for any code change.
- **Version Bump**: If instructed to bump version, update `VERSION.md`, `pom.xml`, `build.gradle`, and `Tools.java`.
