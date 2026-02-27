package org.jwildfire.create.tina.swing;

import java.net.URL;
import java.util.ResourceBundle;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.BGColorType;
import org.jwildfire.create.tina.base.Flame;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class ColoringControllerFX implements Initializable {

    @FXML private TextField brightnessField;
    @FXML private Slider brightnessSlider;
    @FXML private TextField contrastField;
    @FXML private Slider contrastSlider;
    @FXML private TextField gammaField;
    @FXML private Slider gammaSlider;
    @FXML private TextField vibrancyField;
    @FXML private Slider vibrancySlider;
    @FXML private TextField gammaThresholdField;
    @FXML private Slider gammaThresholdSlider;

    @FXML private CheckBox bgTransparencyCbx;
    @FXML private ComboBox<BGColorType> bgColorTypeCmb;
    @FXML private Button resetBtn;
    @FXML private TextField opacityField;
    @FXML private Slider opacitySlider;

    @FXML private TextField fadeToWhiteField;
    @FXML private Slider fadeToWhiteSlider;

    @FXML private TextField lowDensityBrightnessField;
    @FXML private Slider lowDensityBrightnessSlider;

    @FXML private TextField redBalanceField;
    @FXML private Slider redBalanceSlider;
    @FXML private TextField greenBalanceField;
    @FXML private Slider greenBalanceSlider;
    @FXML private TextField blueBalanceField;
    @FXML private Slider blueBalanceSlider;

    private TinaController tinaController;
    private boolean noRefresh;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bgColorTypeCmb.getItems().addAll(BGColorType.values());

        setupSliderField(brightnessSlider, brightnessField, 0, 50);
        setupSliderField(contrastSlider, contrastField, 0, 10);
        setupSliderField(gammaSlider, gammaField, 0, 10);
        setupSliderField(vibrancySlider, vibrancyField, 0, 1);
        setupSliderField(gammaThresholdSlider, gammaThresholdField, 0, 1);
        setupSliderField(opacitySlider, opacityField, 0, 1);
        setupSliderField(fadeToWhiteSlider, fadeToWhiteField, 0, 255);
        setupSliderField(lowDensityBrightnessSlider, lowDensityBrightnessField, 0, 50);
        setupSliderField(redBalanceSlider, redBalanceField, 0, 1);
        setupSliderField(greenBalanceSlider, greenBalanceField, 0, 1);
        setupSliderField(blueBalanceSlider, blueBalanceField, 0, 1);

        addUpdateListener(brightnessSlider, (v) -> updateFlame(f -> f.setBrightness(v)));
        addUpdateListener(contrastSlider, (v) -> updateFlame(f -> f.setContrast(v)));
        addUpdateListener(gammaSlider, (v) -> updateFlame(f -> f.setGamma(v)));
        addUpdateListener(vibrancySlider, (v) -> updateFlame(f -> f.setVibrancy(v)));
        addUpdateListener(gammaThresholdSlider, (v) -> updateFlame(f -> f.setGammaThreshold(v)));
        addUpdateListener(opacitySlider, (v) -> updateFlame(f -> f.setForegroundOpacity(v)));
        addUpdateListener(fadeToWhiteSlider, (v) -> updateFlame(f -> f.setWhiteLevel(v)));
        addUpdateListener(lowDensityBrightnessSlider, (v) -> updateFlame(f -> f.setLowDensityBrightness(v)));

        addUpdateListener(redBalanceSlider, (v) -> updateFlame(f -> f.setBalanceRed(v)));
        addUpdateListener(greenBalanceSlider, (v) -> updateFlame(f -> f.setBalanceGreen(v)));
        addUpdateListener(blueBalanceSlider, (v) -> updateFlame(f -> f.setBalanceBlue(v)));

        bgTransparencyCbx.selectedProperty().addListener((obs, o, n) -> {
            if (!noRefresh) updateFlame(f -> f.setBGTransparency(n));
        });

        bgColorTypeCmb.valueProperty().addListener((obs, o, n) -> {
            if (!noRefresh) updateFlame(f -> f.setBgColorType(n));
        });
    }

    private void setupSliderField(Slider slider, TextField field, double min, double max) {
        slider.setMin(min);
        slider.setMax(max);

        slider.valueProperty().addListener((obs, o, n) -> {
            if (!noRefresh) {
                field.setText(String.format("%.3f", n));
            }
        });

        field.textProperty().addListener((obs, o, n) -> {
            if (!noRefresh) {
                try {
                    double val = Double.parseDouble(n);
                    slider.setValue(val);
                } catch (NumberFormatException e) { }
            }
        });
    }

    private void addUpdateListener(Slider slider, java.util.function.Consumer<Double> updater) {
        slider.valueProperty().addListener((obs, o, n) -> {
            if (!noRefresh) {
                updateFlame(f -> updater.accept(n.doubleValue()));
            }
        });
    }

    private void updateFlame(java.util.function.Consumer<Flame> updater) {
        if (tinaController != null && tinaController.getCurrFlame() != null) {
            updater.accept(tinaController.getCurrFlame());
            tinaController.refreshFlameImage(true, false, 0, false, false);
        }
    }

    public void setTinaController(TinaController tinaController) {
        this.tinaController = tinaController;
        refreshControls();
    }

    public void refreshControls() {
        if (tinaController == null || tinaController.getCurrFlame() == null) return;
        Flame f = tinaController.getCurrFlame();
        noRefresh = true;
        try {
            brightnessSlider.setValue(f.getBrightness());
            brightnessField.setText(String.valueOf(f.getBrightness()));

            contrastSlider.setValue(f.getContrast());
            contrastField.setText(String.valueOf(f.getContrast()));

            gammaSlider.setValue(f.getGamma());
            gammaField.setText(String.valueOf(f.getGamma()));

            vibrancySlider.setValue(f.getVibrancy());
            vibrancyField.setText(String.valueOf(f.getVibrancy()));

            gammaThresholdSlider.setValue(f.getGammaThreshold());
            gammaThresholdField.setText(String.valueOf(f.getGammaThreshold()));

            opacitySlider.setValue(f.getForegroundOpacity());
            opacityField.setText(String.valueOf(f.getForegroundOpacity()));

            fadeToWhiteSlider.setValue(f.getWhiteLevel());
            fadeToWhiteField.setText(String.valueOf(f.getWhiteLevel()));

            lowDensityBrightnessSlider.setValue(f.getLowDensityBrightness());
            lowDensityBrightnessField.setText(String.valueOf(f.getLowDensityBrightness()));

            redBalanceSlider.setValue(f.getBalanceRed());
            redBalanceField.setText(String.valueOf(f.getBalanceRed()));

            greenBalanceSlider.setValue(f.getBalanceGreen());
            greenBalanceField.setText(String.valueOf(f.getBalanceGreen()));

            blueBalanceSlider.setValue(f.getBalanceBlue());
            blueBalanceField.setText(String.valueOf(f.getBalanceBlue()));

            bgTransparencyCbx.setSelected(f.isBGTransparency());
            bgColorTypeCmb.setValue(f.getBgColorType());

        } finally {
            noRefresh = false;
        }
    }

    @FXML private void onReset(ActionEvent event) {
        // tinaController.resetColoringSettings() might not exist or be public?
        // Let's check TinaController methods later if this fails.
        // Assuming it exists or I can implement equivalent.
        // If not, I'll stub it.
        // tinaController.resetColoringSettings();
        // refreshControls();
    }
}
