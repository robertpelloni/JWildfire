# Agents Instructions

Please refer to [LLM_INSTRUCTIONS.md](LLM_INSTRUCTIONS.md) for universal project instructions.

## Core Directives
*   **Deep Planning Mode**: Before writing code, fully analyze requirements and verify assumptions. Do not start coding until you have a crystal-clear understanding of the task.
*   **Completeness**: Do not leave `// TODO` stubs. Implement all logic fully. If a feature is planned, implement it.
*   **Documentation**: Update the Manual, UI Tooltips, and project markdown files (`ROADMAP.md`, `CHANGELOG.md`, `VISION.md`) for every new feature or major change.
*   **Versioning**: Increment the version in `VERSION.md` for every significant build/session. Ensure `CHANGELOG.md` reflects this version.
*   **Git Hygiene**: Commit and push regularly. Use descriptive commit messages. When merging, resolve conflicts intelligently to preserve all functionality.

## Workflow Protocol
1.  **Analysis**: Re-analyze the project status, conversation history, and goals.
2.  **Planning**: Create a detailed plan using `set_plan`.
3.  **Execution**: Implement features, fix bugs, and verify with tests/builds.
4.  **Documentation Update**: Update `ROADMAP.md`, `CHANGELOG.md`, and `VERSION.md`.
5.  **Dashboard Update**: Maintain `DASHBOARD.md` with submodule and structure info.
6.  **Handoff**: Create `HANDOFF.md` summarizing the session for the next agent.

## Specific Task Instructions
- **Modernization**: Continue the Swing-to-JavaFX migration. Ensure feature parity.
- **Submodules**: Keep submodules updated and documented.
- **Feedback Loop**: Ask for user clarification if any directive is ambiguous.
