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
        setupSliderField(fadeToWhiteSlider, fadeToWhiteField, 0, 255); // Actually usually 0-1 or 0-255? Legacy slider suggests 0-255?
        setupSliderField(lowDensityBrightnessSlider, lowDensityBrightnessField, 0, 50);
        setupSliderField(redBalanceSlider, redBalanceField, 0, 1);
        setupSliderField(greenBalanceSlider, greenBalanceField, 0, 1);
        setupSliderField(blueBalanceSlider, blueBalanceField, 0, 1);

        // Listeners for updates
        addUpdateListener(brightnessField, (v) -> updateFlame(f -> f.setBrightness(v)));
        addUpdateListener(contrastField, (v) -> updateFlame(f -> f.setContrast(v)));
        addUpdateListener(gammaField, (v) -> updateFlame(f -> f.setGamma(v)));
        addUpdateListener(vibrancyField, (v) -> updateFlame(f -> f.setVibrancy(v)));
        addUpdateListener(gammaThresholdField, (v) -> updateFlame(f -> f.setGammaThreshold(v)));
        addUpdateListener(opacityField, (v) -> updateFlame(f -> f.setFgOpacity(v)));
        addUpdateListener(fadeToWhiteField, (v) -> updateFlame(f -> f.setDimishWhite(v)));
        addUpdateListener(lowDensityBrightnessField, (v) -> updateFlame(f -> f.setBackgroundBrightness(v)));

        addUpdateListener(redBalanceField, (v) -> updateFlame(f -> f.setRedBalance(v)));
        addUpdateListener(greenBalanceField, (v) -> updateFlame(f -> f.setGreenBalance(v)));
        addUpdateListener(blueBalanceField, (v) -> updateFlame(f -> f.setBlueBalance(v)));

        bgTransparencyCbx.selectedProperty().addListener((obs, o, n) -> updateFlame(f -> f.setBGTransparency(n)));
        bgColorTypeCmb.valueProperty().addListener((obs, o, n) -> updateFlame(f -> f.setBgColorType(n)));
    }

    private void setupSliderField(Slider slider, TextField field, double min, double max) {
        slider.setMin(min);
        slider.setMax(max);

        // Slider -> Field
        slider.valueProperty().addListener((obs, o, n) -> {
            if (!noRefresh) {
                field.setText(String.format("%.3f", n));
            }
        });

        // Field -> Slider
        field.textProperty().addListener((obs, o, n) -> {
            if (!noRefresh) {
                try {
                    double val = Double.parseDouble(n);
                    slider.setValue(val);
                } catch (NumberFormatException e) { }
            }
        });
    }

    private void addUpdateListener(TextField field, java.util.function.Consumer<Double> updater) {
        field.textProperty().addListener((obs, o, n) -> {
            if (!noRefresh) {
                try {
                    double val = Double.parseDouble(n);
                    updater.accept(val);
                    if (tinaController != null) tinaController.refreshFlameImage(true, false);
                } catch (Exception e) {}
            }
        });
    }

    private void updateFlame(java.util.function.Consumer<Flame> updater) {
        if (tinaController != null && tinaController.getCurrFlame() != null) {
            updater.accept(tinaController.getCurrFlame());
            tinaController.refreshFlameImage(true, false);
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

            opacitySlider.setValue(f.getFgOpacity());
            opacityField.setText(String.valueOf(f.getFgOpacity()));

            // Assuming fadeToWhite maps to DimishWhite or similar
            fadeToWhiteSlider.setValue(f.getDimishWhite());
            fadeToWhiteField.setText(String.valueOf(f.getDimishWhite()));

            lowDensityBrightnessSlider.setValue(f.getBackgroundBrightness());
            lowDensityBrightnessField.setText(String.valueOf(f.getBackgroundBrightness()));

            redBalanceSlider.setValue(f.getRedBalance());
            redBalanceField.setText(String.valueOf(f.getRedBalance()));

            greenBalanceSlider.setValue(f.getGreenBalance());
            greenBalanceField.setText(String.valueOf(f.getGreenBalance()));

            blueBalanceSlider.setValue(f.getBlueBalance());
            blueBalanceField.setText(String.valueOf(f.getBlueBalance()));

            bgTransparencyCbx.setSelected(f.isBGTransparency());
            bgColorTypeCmb.setValue(f.getBgColorType());

        } finally {
            noRefresh = false;
        }
    }

    @FXML private void onReset(ActionEvent event) {
        if (tinaController != null) {
            tinaController.resetColoringSettings();
            refreshControls();
        }
    }
}
