package org.jwildfire.create.tina.swing;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class TransformationsGammaControllerFX implements Initializable {

    @FXML private TextField gammaField;
    @FXML private Slider gammaSlider;
    @FXML private TextField gammaSpeedField;
    @FXML private Slider gammaSpeedSlider;

    private TinaController tinaController;
    private boolean refreshing;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gammaField.textProperty().addListener((obs, o, n) -> {
            if (!refreshing && tinaController != null) {
                if (!gammaSlider.isValueChanging()) {
                    tinaController.saveUndoPoint();
                }
                tinaController.xFormModGammaREd_changed();
            }
        });

        gammaSlider.valueProperty().addListener((obs, o, n) -> {
            if (!refreshing && tinaController != null) {
                tinaController.xFormModGammaSlider_changed();
            }
        });

        gammaSlider.setOnMousePressed(e -> {
            if (tinaController != null) tinaController.saveUndoPoint();
        });

        gammaSpeedField.textProperty().addListener((obs, o, n) -> {
            if (!refreshing && tinaController != null) {
                if (!gammaSpeedSlider.isValueChanging()) {
                    tinaController.saveUndoPoint();
                }
                tinaController.xFormModGammaSpeedREd_changed();
            }
        });

        gammaSpeedSlider.valueProperty().addListener((obs, o, n) -> {
            if (!refreshing && tinaController != null) {
                tinaController.xFormModGammaSpeedSlider_changed();
            }
        });

        gammaSpeedSlider.setOnMousePressed(e -> {
            if (tinaController != null) tinaController.saveUndoPoint();
        });
    }

    public void setTinaController(TinaController tinaController) {
        this.tinaController = tinaController;
    }

    public void refresh() {
        if (tinaController == null) return;
        refreshing = true;
        try {
            org.jwildfire.create.tina.base.XForm xForm = tinaController.getCurrXForm();
            boolean enabled = xForm != null;

            gammaField.setDisable(!enabled);
            gammaSlider.setDisable(!enabled);
            gammaSpeedField.setDisable(!enabled);
            gammaSpeedSlider.setDisable(!enabled);

            if (enabled) {
                gammaField.setText(String.valueOf(xForm.getModGamma()));
                gammaSpeedField.setText(String.valueOf(xForm.getModGammaSpeed()));
            } else {
                gammaField.setText("");
                gammaSpeedField.setText("");
            }
        } finally {
            refreshing = false;
        }
    }

    @FXML
    private void resetGamma(MouseEvent event) {
        if (event.getClickCount() == 2 && tinaController != null) {
            tinaController.saveUndoPoint();
            tinaController.xFormModGammaREd_reset();
        }
    }

    @FXML
    private void resetGammaSpeed(MouseEvent event) {
        if (event.getClickCount() == 2 && tinaController != null) {
            tinaController.saveUndoPoint();
            tinaController.xFormModGammaSpeedREd_reset();
        }
    }
}
