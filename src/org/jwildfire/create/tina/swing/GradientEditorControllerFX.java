package org.jwildfire.create.tina.swing;

import java.net.URL;
import java.util.ResourceBundle;

import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.randomgradient.RandomGradientGeneratorList;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;

public class GradientEditorControllerFX implements Initializable {

    @FXML private Canvas gradientPreviewCanvas;
    @FXML private Slider shiftSlider;

    private TinaController tinaController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        shiftSlider.valueProperty().addListener((obs, o, n) -> {
            if (tinaController != null && tinaController.getCurrLayer() != null) {
                // Shift logic would go here, usually interacting with RGBPalette
                // For now, trigger refresh
                refreshGradientPreview();
            }
        });
    }

    public void setTinaController(TinaController tinaController) {
        this.tinaController = tinaController;
        refreshGradientPreview();
    }

    @FXML private void onRandomGradient(ActionEvent event) {
        if (tinaController != null) {
            // Use legacy logic to generate random gradient
            // tinaController.getGradientControlsDelegate()... ?
            // Or access generator list directly.
            // For now, let's just assume we can set a random one.
            // This is a placeholder for the logic connecting to RandomGradientGeneratorList
        }
    }

    @FXML private void onSaveGradient(ActionEvent event) {
        // Save logic
    }

    @FXML private void onLoadGradient(ActionEvent event) {
        // Load logic
    }

    public void refreshGradientPreview() {
        if (tinaController == null || tinaController.getCurrLayer() == null) return;

        RGBPalette palette = tinaController.getCurrLayer().getPalette();
        if (palette != null) {
            GraphicsContext gc = gradientPreviewCanvas.getGraphicsContext2D();
            double width = gradientPreviewCanvas.getWidth();
            double height = gradientPreviewCanvas.getHeight();

            for (int x = 0; x < width; x++) {
                int index = (int) ((x / width) * 255.0); // Map to 0-255 palette index
                // RGBPalette usually has 256 colors
                org.jwildfire.create.tina.palette.RGBColor color = palette.getColor(index);
                gc.setFill(Color.rgb(color.getRed(), color.getGreen(), color.getBlue()));
                gc.fillRect(x, 0, 1, height);
            }
        }
    }
}
