package org.jwildfire.create.tina.swing;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class TransformationsWFieldControllerFX implements Initializable {

    @FXML private ComboBox<String> typeCmb;
    @FXML private ComboBox<String> inputCmb;

    @FXML private TextField colorIntensityField;
    @FXML private TextField variationIntensityField;
    @FXML private TextField jitterIntensityField;

    @FXML private TextField param01Field;
    @FXML private TextField param02Field;
    @FXML private TextField param03Field;
    @FXML private ComboBox<String> param04Cmb;
    @FXML private TextField param05Field;
    @FXML private TextField param06Field;
    @FXML private TextField param07Field;
    @FXML private ComboBox<String> param08Cmb;

    @FXML private ComboBox<String> varParam1Cmb;
    @FXML private TextField varParam1Field;
    @FXML private ComboBox<String> varParam2Cmb;
    @FXML private TextField varParam2Field;
    @FXML private ComboBox<String> varParam3Cmb;
    @FXML private TextField varParam3Field;

    @FXML private Button selectImageBtn;
    @FXML private Button randomizeAllBtn;
    @FXML private Button resetAllBtn;

    private TinaController tinaController;
    private boolean refreshing;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeCmb.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (!refreshing && tinaController != null) {
                tinaController.saveUndoPoint();
                tinaController.weightMapTypeCmb_changed();
            }
        });

        inputCmb.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (!refreshing && tinaController != null) {
                tinaController.saveUndoPoint();
                tinaController.weightMapInputCmb_changed();
            }
        });

        setupTextFieldListener(colorIntensityField, () -> { if (tinaController != null) tinaController.weightMapColorIntensityREd_changed(); });
        setupTextFieldListener(variationIntensityField, () -> { if (tinaController != null) tinaController.weightMapVariationIntensityREd_changed(); });
        setupTextFieldListener(jitterIntensityField, () -> { if (tinaController != null) tinaController.weightingFieldJitterIntensityREd_changed(); });

        setupTextFieldListener(param01Field, () -> { if (tinaController != null) tinaController.weightMapParam01REd_changed(); });
        setupTextFieldListener(param02Field, () -> { if (tinaController != null) tinaController.weightMapParam02REd_changed(); });
        setupTextFieldListener(param03Field, () -> { if (tinaController != null) tinaController.weightMapParam03REd_changed(); });

        param04Cmb.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (!refreshing && tinaController != null) {
                tinaController.saveUndoPoint();
                tinaController.weightMapParam04Cmb_changed();
            }
        });

        setupTextFieldListener(param05Field, () -> { if (tinaController != null) tinaController.weightMapParam05REd_changed(); });
        setupTextFieldListener(param06Field, () -> { if (tinaController != null) tinaController.weightMapParam06REd_changed(); });
        setupTextFieldListener(param07Field, () -> { if (tinaController != null) tinaController.weightMapParam07REd_changed(); });

        param08Cmb.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (!refreshing && tinaController != null) {
                tinaController.saveUndoPoint();
                tinaController.weightMapParam08Cmb_changed();
            }
        });

        varParam1Cmb.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (!refreshing && tinaController != null) {
                tinaController.saveUndoPoint();
                tinaController.weightingFieldVarParam1NameCmb_changed();
            }
        });
        setupTextFieldListener(varParam1Field, () -> { if (tinaController != null) tinaController.weightingFieldVarParam1AmountREd_changed(); });

        varParam2Cmb.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (!refreshing && tinaController != null) {
                tinaController.saveUndoPoint();
                tinaController.weightingFieldVarParam2NameCmb_changed();
            }
        });
        setupTextFieldListener(varParam2Field, () -> { if (tinaController != null) tinaController.weightingFieldVarParam2AmountREd_changed(); });

        varParam3Cmb.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (!refreshing && tinaController != null) {
                tinaController.saveUndoPoint();
                tinaController.weightingFieldVarParam3NameCmb_changed();
            }
        });
        setupTextFieldListener(varParam3Field, () -> { if (tinaController != null) tinaController.weightingFieldVarParam3AmountREd_changed(); });
    }

    private void setupTextFieldListener(TextField field, Runnable action) {
        field.textProperty().addListener((obs, o, n) -> {
            if (!refreshing) {
                action.run();
            }
        });
    }

    public void setTinaController(TinaController tinaController) {
        this.tinaController = tinaController;
    }

    @FXML private void resetWFieldType(MouseEvent event) { if (event.getClickCount() == 2 && tinaController != null) { tinaController.saveUndoPoint(); tinaController.weightingFieldTypeCmb_reset(); } }
    @FXML private void resetInput(MouseEvent event) { if (event.getClickCount() == 2 && tinaController != null) { tinaController.saveUndoPoint(); tinaController.weightMapInputCmb_reset(); } }
    @FXML private void resetImageFile(MouseEvent event) { if (event.getClickCount() == 2 && tinaController != null) { tinaController.saveUndoPoint(); tinaController.weightMapColorMapFilename_reset(); } }
    @FXML private void resetParam01(MouseEvent event) { if (event.getClickCount() == 2 && tinaController != null) { tinaController.saveUndoPoint(); tinaController.weightMapParam01REd_reset(); } }
    @FXML private void resetParam02(MouseEvent event) { if (event.getClickCount() == 2 && tinaController != null) { tinaController.saveUndoPoint(); tinaController.weightMapParam02REd_reset(); } }
    @FXML private void resetParam03(MouseEvent event) { if (event.getClickCount() == 2 && tinaController != null) { tinaController.saveUndoPoint(); tinaController.weightMapParam03REd_reset(); } }
    @FXML private void resetParam05(MouseEvent event) { if (event.getClickCount() == 2 && tinaController != null) { tinaController.saveUndoPoint(); tinaController.weightMapParam05REd_reset(); } }
    @FXML private void resetParam06(MouseEvent event) { if (event.getClickCount() == 2 && tinaController != null) { tinaController.saveUndoPoint(); tinaController.weightMapParam06REd_reset(); } }
    @FXML private void resetParam07(MouseEvent event) { if (event.getClickCount() == 2 && tinaController != null) { tinaController.saveUndoPoint(); tinaController.weightMapParam07REd_reset(); } }

    @FXML private void selectImageBtnClicked(ActionEvent event) { if (tinaController != null) tinaController.weightMapColorMapFilenameBtn_clicked(); }

    @FXML private void randomizeAllBtnClicked(ActionEvent event) {
        if (tinaController != null) {
            // Hardcode false for "Whole fractal" since checkbox isn't fully bound yet in this snippet
            tinaController.weightMapRandomizeAllBtn_clicked(false);
        }
    }

    @FXML private void resetAllBtnClicked(ActionEvent event) {
        if (tinaController != null) {
            tinaController.weightMapResetAllBtn_clicked(false);
        }
    }
}
