# JWildfire Architecture

## Hybrid Swing / JavaFX UI

The modernization of JWildfire uses a hybrid approach, embedding JavaFX `Scene` graphs inside legacy `JFrame` and `JPanel` components via `JFXPanel`.

### Transformations Tab
The Transformations tab has been entirely modernized using this pattern. The `TransformationsAffineController`, `TransformationsNonlinearController`, `TransformationsColorController`, `TransformationsXaosControllerFX`, `TransformationsGammaControllerFX`, and `TransformationsWFieldControllerFX` handle the user interface, reading and writing to the `XForm` model and synchronizing through the `TinaController`.
