## 2025-05-20 - Welcome Screen Accessibility
**Learning:** Swing components like `JPanel` and `JLabel` (used for images) are not keyboard accessible by default and lack cursor feedback, making "invisible" links.
**Action:** Always add `setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))` and `setToolTipText()` to any Swing component acting as a button or link, not just `JButton`.
