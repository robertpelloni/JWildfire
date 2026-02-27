package org.jwildfire.create.tina.swing;

import java.net.URL;
import java.util.ResourceBundle;

import org.jwildfire.create.tina.base.ColorType;
import org.jwildfire.create.tina.base.DrawMode;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.palette.RGBColor;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class TransformationsColorController implements Initializable {

    @FXML private ComboBox<ColorType> colorTypeCmb;
    @FXML private Slider colorSlider;
    @FXML private TextField colorField;
    @FXML private ColorPicker targetColorPicker;
    @FXML private Slider speedSlider;
    @FXML private TextField speedField;
    @FXML private ComboBox<DrawMode> drawModeCmb;
    @FXML private Slider opacitySlider;
    @FXML private TextField opacityField;

    private TinaController tinaController;
    private boolean refreshing;

    public void setTinaController(TinaController tinaController) {
        this.tinaController = tinaController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colorTypeCmb.getItems().addAll(ColorType.values());
        drawModeCmb.getItems().addAll(DrawMode.values());

        colorTypeCmb.valueProperty().addListener((obs, o, n) -> onColorTypeChanged());

        colorSlider.valueProperty().addListener((obs, o, n) -> {
            if (!refreshing) {
                colorField.setText(String.format("%.3f", n.doubleValue()));
                onColorChanged();
            }
        });

        colorField.textProperty().addListener((obs, o, n) -> {
            if (!refreshing) {
                try {
                    double val = Double.parseDouble(n);
                    refreshing = true;
                    colorSlider.setValue(val);
                    refreshing = false;
                    onColorChanged();
                } catch (Exception e) {}
            }
        });

        targetColorPicker.valueProperty().addListener((obs, o, n) -> onTargetColorChanged());

        speedSlider.valueProperty().addListener((obs, o, n) -> {
            if (!refreshing) {
                speedField.setText(String.format("%.3f", n.doubleValue()));
                onSpeedChanged();
            }
        });

        speedField.textProperty().addListener((obs, o, n) -> {
            if (!refreshing) {
                try {
                    double val = Double.parseDouble(n);
                    refreshing = true;
                    speedSlider.setValue(val);
                    refreshing = false;
                    onSpeedChanged();
                } catch (Exception e) {}
            }
        });

        drawModeCmb.valueProperty().addListener((obs, o, n) -> onDrawModeChanged());

        opacitySlider.valueProperty().addListener((obs, o, n) -> {
            if (!refreshing) {
                opacityField.setText(String.format("%.3f", n.doubleValue()));
                onOpacityChanged();
            }
        });

        opacityField.textProperty().addListener((obs, o, n) -> {
            if (!refreshing) {
                try {
                    double val = Double.parseDouble(n);
                    refreshing = true;
                    opacitySlider.setValue(val);
                    refreshing = false;
                    onOpacityChanged();
                } catch (Exception e) {}
            }
        });
    }

    public void refresh() {
        if (tinaController == null) return;
        XForm xForm = tinaController.getCurrXForm();
        if (xForm != null) {
            refreshing = true;
            try {
                colorTypeCmb.setValue(xForm.getColorType());

                colorSlider.setValue(xForm.getColor());
                colorField.setText(String.valueOf(xForm.getColor()));

                RGBColor tc = xForm.getTargetColor();
                targetColorPicker.setValue(Color.rgb(tc.getRed(), tc.getGreen(), tc.getBlue()));

                speedSlider.setValue(xForm.getColorSymmetry());
                speedField.setText(String.valueOf(xForm.getColorSymmetry()));

                drawModeCmb.setValue(xForm.getDrawMode());

                opacitySlider.setValue(xForm.getOpacity());
                opacityField.setText(String.valueOf(xForm.getOpacity()));

                updateVisibility();
            } finally {
                refreshing = false;
            }
        }
    }

    private void updateVisibility() {
        boolean isTarget = ColorType.TARGET.equals(colorTypeCmb.getValue());
        targetColorPicker.setDisable(!isTarget);
        colorSlider.setDisable(isTarget);
        colorField.setDisable(isTarget);
    }

    private void onColorTypeChanged() {
        if (refreshing || tinaController == null) return;
        XForm xForm = tinaController.getCurrXForm();
        if (xForm != null) {
            tinaController.saveUndoPoint();
            xForm.setColorType(colorTypeCmb.getValue());
            updateVisibility();
            tinaController.refreshFlameImage(true, false, 1, true, false);
        }
    }

    private void onColorChanged() {
        if (refreshing || tinaController == null) return;
        XForm xForm = tinaController.getCurrXForm();
        if (xForm != null) {
            try {
                xForm.setColor(Double.parseDouble(colorField.getText()));
                tinaController.refreshFlameImage(true, false, 1, true, false);
            } catch (Exception e) {}
        }
    }

    private void onTargetColorChanged() {
        if (refreshing || tinaController == null) return;
        XForm xForm = tinaController.getCurrXForm();
        if (xForm != null) {
            tinaController.saveUndoPoint();
            Color c = targetColorPicker.getValue();
            xForm.setTargetColor(new RGBColor((int)(c.getRed()*255), (int)(c.getGreen()*255), (int)(c.getBlue()*255)));
            tinaController.refreshFlameImage(true, false, 1, true, false);
        }
    }

    private void onSpeedChanged() {
        if (refreshing || tinaController == null) return;
        XForm xForm = tinaController.getCurrXForm();
        if (xForm != null) {
            try {
                xForm.setColorSymmetry(Double.parseDouble(speedField.getText()));
                tinaController.refreshFlameImage(true, false, 1, true, false);
            } catch (Exception e) {}
        }
    }

    private void onDrawModeChanged() {
        if (refreshing || tinaController == null) return;
        XForm xForm = tinaController.getCurrXForm();
        if (xForm != null) {
            tinaController.saveUndoPoint();
            xForm.setDrawMode(drawModeCmb.getValue());
            tinaController.refreshFlameImage(true, false, 1, true, false);
        }
    }

    private void onOpacityChanged() {
        if (refreshing || tinaController == null) return;
        XForm xForm = tinaController.getCurrXForm();
        if (xForm != null) {
            try {
                xForm.setOpacity(Double.parseDouble(opacityField.getText()));
                tinaController.refreshFlameImage(true, false, 1, true, false);
            } catch (Exception e) {}
        }
    }
}
