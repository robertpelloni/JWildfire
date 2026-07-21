package org.jwildfire.create.tina.swing;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.jwildfire.create.tina.base.Flame;

public class CameraTabControllerFX {

    private TinaController tinaController;
    private boolean isUpdating = false;

    @FXML private TextField tinaCameraRollField;
    @FXML private TextField tinaCameraPitchField;
    @FXML private TextField tinaCameraYawField;
    @FXML private TextField tinaCameraBankField;
    @FXML private TextField tinaCameraPerspectiveField;
    @FXML private TextField tinaCameraCentreXField;
    @FXML private TextField tinaCameraCentreYField;
    @FXML private TextField tinaCameraZoomField;
    @FXML private TextField tinaCameraCamPosXField;
    @FXML private TextField tinaCameraCamPosYField;
    @FXML private TextField tinaCameraCamPosZField;
    @FXML private Button resetCameraSettingsBtn;

    public void setTinaController(TinaController tinaController) {
        this.tinaController = tinaController;
        setupListeners();
    }

    private void setupListeners() {
        setupDoubleField(tinaCameraRollField, "camRoll");
        setupDoubleField(tinaCameraPitchField, "camPitch");
        setupDoubleField(tinaCameraYawField, "camYaw");
        setupDoubleField(tinaCameraBankField, "camBank");
        setupDoubleField(tinaCameraPerspectiveField, "camPerspective");
        setupDoubleField(tinaCameraCentreXField, "centreX");
        setupDoubleField(tinaCameraCentreYField, "centreY");
        setupDoubleField(tinaCameraZoomField, "pixelsPerUnit");
        setupDoubleField(tinaCameraCamPosXField, "camPosX");
        setupDoubleField(tinaCameraCamPosYField, "camPosY");
        setupDoubleField(tinaCameraCamPosZField, "camPosZ");
    }

    private void setupDoubleField(TextField field, String propName) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isUpdating && tinaController != null && tinaController.getCurrFlame() != null) {
                try {
                    double val = Double.parseDouble(newValue.replace(',', '.'));
                    tinaController.getFrameControlsUtil().applyValueChange(tinaController.getCurrFlame(), propName, val);
                    tinaController.refreshUI(false);
                } catch (NumberFormatException e) {
                }
            }
        });
    }

    public void refreshControls() {
        if (tinaController == null) return;

        isUpdating = true;
        try {
            Flame flame = tinaController.getCurrFlame();
            boolean enabled = flame != null;

            resetCameraSettingsBtn.setDisable(!enabled);

            if (enabled) {
                tinaCameraRollField.setText(String.valueOf(flame.getCamRoll()));
                tinaCameraPitchField.setText(String.valueOf(flame.getCamPitch()));
                tinaCameraYawField.setText(String.valueOf(flame.getCamYaw()));
                tinaCameraBankField.setText(String.valueOf(flame.getCamBank()));
                tinaCameraPerspectiveField.setText(String.valueOf(flame.getCamPerspective()));
                tinaCameraCentreXField.setText(String.valueOf(flame.getCentreX()));
                tinaCameraCentreYField.setText(String.valueOf(flame.getCentreY()));
                tinaCameraZoomField.setText(String.valueOf(flame.getPixelsPerUnit()));
                tinaCameraCamPosXField.setText(String.valueOf(flame.getCamPosX()));
                tinaCameraCamPosYField.setText(String.valueOf(flame.getCamPosY()));
                tinaCameraCamPosZField.setText(String.valueOf(flame.getCamPosZ()));
            } else {
                tinaCameraRollField.setText("");
                tinaCameraPitchField.setText("");
                tinaCameraYawField.setText("");
                tinaCameraBankField.setText("");
                tinaCameraPerspectiveField.setText("");
                tinaCameraCentreXField.setText("");
                tinaCameraCentreYField.setText("");
                tinaCameraZoomField.setText("");
                tinaCameraCamPosXField.setText("");
                tinaCameraCamPosYField.setText("");
                tinaCameraCamPosZField.setText("");
            }

            tinaCameraRollField.setDisable(!enabled);
            tinaCameraPitchField.setDisable(!enabled);
            tinaCameraYawField.setDisable(!enabled);
            tinaCameraBankField.setDisable(!enabled);
            tinaCameraPerspectiveField.setDisable(!enabled);
            tinaCameraCentreXField.setDisable(!enabled);
            tinaCameraCentreYField.setDisable(!enabled);
            tinaCameraZoomField.setDisable(!enabled);
            tinaCameraCamPosXField.setDisable(!enabled);
            tinaCameraCamPosYField.setDisable(!enabled);
            tinaCameraCamPosZField.setDisable(!enabled);

        } finally {
            isUpdating = false;
        }
    }

    @FXML private void onResetCamera(ActionEvent event) {
        if (tinaController != null) {
            tinaController.resetCameraSettings();
        }
    }
}
