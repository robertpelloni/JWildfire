package org.jwildfire.create.tina.swing;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class ShadingTabControllerFX implements Initializable {

    // DOF Tab
    @FXML private TextField dofAreaField;
    @FXML private Slider dofAreaSlider;
    @FXML private TextField dofExponentField;
    @FXML private Slider dofExponentSlider;
    @FXML private TextField dofDofField;
    @FXML private Slider dofDofSlider;
    @FXML private Button resetDofBtn;

    // Bokeh Tab
    @FXML private ComboBox<String> bokehShapeCmb;

    // Post Bokeh Tab
    @FXML private ComboBox<String> postBokehFilterKernelCmb;
    @FXML private TextField postBokehIntensityField;
    @FXML private Slider postBokehIntensitySlider;
    @FXML private TextField postBokehSizeField;
    @FXML private Slider postBokehSizeSlider;
    @FXML private TextField postBokehBrightnessField;
    @FXML private Slider postBokehBrightnessSlider;
    @FXML private TextField postBokehActivationField;
    @FXML private Slider postBokehActivationSlider;
    @FXML private Button resetPostBokehBtn;

    // Post Blur Tab
    @FXML private TextField postBlurRadiusField;
    @FXML private Slider postBlurRadiusSlider;
    @FXML private TextField postBlurFadeField;
    @FXML private Slider postBlurFadeSlider;
    @FXML private TextField postBlurFalloffField;
    @FXML private Slider postBlurFalloffSlider;
    @FXML private Button resetPostBlurBtn;

    private TinaController tinaController;
    private boolean refreshing;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupFieldAndSlider(dofAreaField, dofAreaSlider, () -> { if(tinaController!=null) tinaController.getFlameControls().cameraDOFAreaREd_changed(); }, () -> { if(tinaController!=null) tinaController.getFlameControls().cameraDOFAreaSlider_stateChanged(null); });
        setupFieldAndSlider(dofExponentField, dofExponentSlider, () -> { if(tinaController!=null) tinaController.getFlameControls().cameraDOFExponentREd_changed(); }, () -> { if(tinaController!=null) tinaController.getFlameControls().cameraDOFExponentSlider_stateChanged(null); });
        setupFieldAndSlider(dofDofField, dofDofSlider, () -> { if(tinaController!=null) tinaController.getFlameControls().cameraDOFREd_changed(); }, () -> { if(tinaController!=null) tinaController.getFlameControls().cameraDOFSlider_stateChanged(null); });

        bokehShapeCmb.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (!refreshing && tinaController != null) {
                tinaController.saveUndoPoint();
                tinaController.getFlameControls().dofDOFShapeCmb_changed();
            }
        });

        postBokehFilterKernelCmb.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (!refreshing && tinaController != null) {
                tinaController.getFlameControls().solidRenderingPostBokehFilterKernelCmb_changed();
            }
        });

        setupFieldAndSlider(postBokehIntensityField, postBokehIntensitySlider, () -> { if(tinaController!=null) tinaController.getFlameControls().solidRenderingPostBokehIntensityREd_changed(); }, () -> { if(tinaController!=null) tinaController.getFlameControls().solidRenderingPostBokehIntensitySlider_stateChanged(null); });
        setupFieldAndSlider(postBokehSizeField, postBokehSizeSlider, () -> { if(tinaController!=null) tinaController.getFlameControls().solidRenderingPostBokehSizeREd_changed(); }, () -> { if(tinaController!=null) tinaController.getFlameControls().solidRenderingPostBokehSizeSlider_stateChanged(null); });
        setupFieldAndSlider(postBokehBrightnessField, postBokehBrightnessSlider, () -> { if(tinaController!=null) tinaController.getFlameControls().solidRenderingPostBokehBrightnessREd_changed(); }, () -> { if(tinaController!=null) tinaController.getFlameControls().solidRenderingPostBokehBrightnessSlider_stateChanged(null); });
        setupFieldAndSlider(postBokehActivationField, postBokehActivationSlider, () -> { if(tinaController!=null) tinaController.getFlameControls().solidRenderingPostBokehActivationREd_changed(); }, () -> { if(tinaController!=null) tinaController.getFlameControls().solidRenderingPostBokehActivationSlider_stateChanged(null); });

        setupTextFieldListener(postBlurRadiusField, () -> { if(tinaController!=null) tinaController.getFlameControls().postBlurRadiusREd_changed(); });
        postBlurRadiusSlider.valueProperty().addListener((obs, o, n) -> { if(tinaController!=null) tinaController.getFlameControls().postBlurRadiusREd_changed(); });
        setupTextFieldListener(postBlurFadeField, () -> { if(tinaController!=null) tinaController.getFlameControls().postBlurFadeREd_changed(); });
        postBlurFadeSlider.valueProperty().addListener((obs, o, n) -> { if(tinaController!=null) tinaController.getFlameControls().postBlurFadeREd_changed(); });
        setupTextFieldListener(postBlurFalloffField, () -> { if(tinaController!=null) tinaController.getFlameControls().postBlurFallOffREd_changed(); });
        postBlurFalloffSlider.valueProperty().addListener((obs, o, n) -> { if(tinaController!=null) tinaController.getFlameControls().postBlurFallOffREd_changed(); });
    }

    private void setupTextFieldListener(TextField field, Runnable action) {
        field.textProperty().addListener((obs, o, n) -> {
            if (!refreshing) {
                action.run();
            }
        });
    }

    private void setupFieldAndSlider(TextField field, Slider slider, Runnable fieldAction, Runnable sliderAction) {
        field.textProperty().addListener((obs, o, n) -> {
            if (!refreshing) fieldAction.run();
        });
        slider.valueProperty().addListener((obs, o, n) -> {
            if (!refreshing) sliderAction.run();
        });
        slider.setOnMousePressed(e -> {
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
            org.jwildfire.create.tina.base.Flame flame = tinaController.getCurrFlame();
            boolean enabled = flame != null;

            postBlurRadiusField.setDisable(!enabled);
            postBlurRadiusSlider.setDisable(!enabled);
            postBlurFadeField.setDisable(!enabled);
            postBlurFadeSlider.setDisable(!enabled);
            postBlurFalloffField.setDisable(!enabled);
            postBlurFalloffSlider.setDisable(!enabled);

            if (enabled) {
                dofAreaField.setText(String.valueOf(flame.getCamDOFArea()));
                dofExponentField.setText(String.valueOf(flame.getCamDOFExponent()));
                dofDofField.setText(String.valueOf(flame.getCamDOF()));
                postBokehIntensityField.setText(String.valueOf(tinaController.getFrameControlsUtil().getEvaluatedPropertyValue(flame, "solidRendering.postBokehIntensity")));
                postBokehSizeField.setText(String.valueOf(tinaController.getFrameControlsUtil().getEvaluatedPropertyValue(flame, "solidRendering.postBokehSize")));
                postBokehBrightnessField.setText(String.valueOf(tinaController.getFrameControlsUtil().getEvaluatedPropertyValue(flame, "solidRendering.postBokehBrightness")));
                postBokehActivationField.setText(String.valueOf(tinaController.getFrameControlsUtil().getEvaluatedPropertyValue(flame, "solidRendering.postBokehActivation")));
                postBlurRadiusField.setText(String.valueOf(flame.getPostBlurRadius()));
                postBlurFadeField.setText(String.valueOf(flame.getPostBlurFade()));
                postBlurFalloffField.setText(String.valueOf(flame.getPostBlurFallOff()));
            } else {
                dofAreaField.setText("");
                dofExponentField.setText("");
                dofDofField.setText("");
                postBokehIntensityField.setText("");
                postBokehSizeField.setText("");
                postBokehBrightnessField.setText("");
                postBokehActivationField.setText("");
                postBlurRadiusField.setText("");
                postBlurFadeField.setText("");
                postBlurFalloffField.setText("");
            }
        } finally {
            refreshing = false;
        }
    }

    @FXML private void resetDofArea(MouseEvent event) { if (event.getClickCount() == 2 && tinaController != null) tinaController.getFlameControls().cameraDOFAreaREd_reset(); }
    @FXML private void resetDofExponent(MouseEvent event) { if (event.getClickCount() == 2 && tinaController != null) tinaController.getFlameControls().cameraDOFExponentREd_reset(); }
    @FXML private void resetDofDof(MouseEvent event) { if (event.getClickCount() == 2 && tinaController != null) tinaController.getFlameControls().cameraDOFREd_reset(); }
    @FXML private void resetDofSettings(ActionEvent event) { if (tinaController != null) tinaController.resetDOFSettings(); }

    @FXML private void resetBokehShape(MouseEvent event) { if (event.getClickCount() == 2 && tinaController != null) tinaController.getFlameControls().dofDOFShapeCmb_reset(); }

    @FXML private void resetPostBokehIntensity(MouseEvent event) { if (event.getClickCount() == 2 && tinaController != null) tinaController.getFlameControls().solidRenderingPostBokehIntensityREd_reset(); }
    @FXML private void resetPostBokehSize(MouseEvent event) { if (event.getClickCount() == 2 && tinaController != null) tinaController.getFlameControls().solidRenderingPostBokehSizeREd_reset(); }
    @FXML private void resetPostBokehBrightness(MouseEvent event) { if (event.getClickCount() == 2 && tinaController != null) tinaController.getFlameControls().solidRenderingPostBokehBrightnessREd_reset(); }
    @FXML private void resetPostBokehActivation(MouseEvent event) { if (event.getClickCount() == 2 && tinaController != null) tinaController.getFlameControls().solidRenderingPostBokehActivationREd_reset(); }
    @FXML private void resetPostBokehSettings(ActionEvent event) { if (tinaController != null) tinaController.resetPostBokehSettings(); }

    @FXML private void resetPostBlurRadius(MouseEvent event) { if (event.getClickCount() == 2 && tinaController != null) tinaController.getFlameControls().postBlurRadiusREd_reset(); }
    @FXML private void resetPostBlurFade(MouseEvent event) { if (event.getClickCount() == 2 && tinaController != null) tinaController.getFlameControls().postBlurFadeREd_reset(); }
    @FXML private void resetPostBlurFalloff(MouseEvent event) { if (event.getClickCount() == 2 && tinaController != null) tinaController.getFlameControls().postBlurFallOffREd_reset(); }
    @FXML private void resetPostBlurSettings(ActionEvent event) { if (tinaController != null) tinaController.resetPostBlurSettings(); }
}
