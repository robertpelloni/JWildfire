package org.jwildfire.create.tina.quilt;

import java.net.URL;
import java.util.ResourceBundle;

import org.jwildfire.create.tina.swing.TinaController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class QuiltFlameRendererController implements Initializable {

    @FXML private TextField qualityField;
    @FXML private TextField renderWidthField;
    @FXML private TextField renderHeightField;
    @FXML private TextField xSegmentationField;
    @FXML private TextField ySegmentationField;
    @FXML private TextField segmentWidthField;
    @FXML private TextField segmentHeightField;
    @FXML private TextField outputFilenameField;
    @FXML private StackPane previewPane;
    @FXML private ImageView previewImageView;
    @FXML private ProgressBar segmentProgressBar;
    @FXML private ProgressBar totalProgressBar;
    @FXML private Button renderBtn;

    private TinaController tinaController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize defaults
        qualityField.setText("100");
        renderWidthField.setText("3840");
        renderHeightField.setText("2160");
        xSegmentationField.setText("2");
        ySegmentationField.setText("2");
        recalcSegmentSize();
    }

    public void setTinaController(TinaController tinaController) {
        this.tinaController = tinaController;
    }

    private void recalcSegmentSize() {
        try {
            int width = Integer.parseInt(renderWidthField.getText());
            int height = Integer.parseInt(renderHeightField.getText());
            int xSeg = Integer.parseInt(xSegmentationField.getText());
            int ySeg = Integer.parseInt(ySegmentationField.getText());

            if (xSeg > 0 && ySeg > 0) {
                segmentWidthField.setText(String.valueOf(width / xSeg));
                segmentHeightField.setText(String.valueOf(height / ySeg));
            }
        } catch (NumberFormatException e) {
            // Ignore invalid input during typing
        }
    }

    @FXML private void onOpenFlame(ActionEvent event) {
        // TODO: Implement using tinaController or QuiltRendererController logic
    }

    @FXML private void onFromEditor(ActionEvent event) {
        // TODO
    }

    @FXML private void onFromClipboard(ActionEvent event) {
        // TODO
    }

    @FXML private void onSetSize4K(ActionEvent event) {
        renderWidthField.setText("3840");
        renderHeightField.setText("2160");
        recalcSegmentSize();
    }

    @FXML private void onSetSize8K(ActionEvent event) {
        renderWidthField.setText("7680");
        renderHeightField.setText("4320");
        recalcSegmentSize();
    }

    @FXML private void onSetSize16K(ActionEvent event) {
        renderWidthField.setText("15360");
        renderHeightField.setText("8640");
        recalcSegmentSize();
    }

    @FXML private void onSetSize32K(ActionEvent event) {
        renderWidthField.setText("30720");
        renderHeightField.setText("17280");
        recalcSegmentSize();
    }

    @FXML private void onRender(ActionEvent event) {
        // TODO: Start render
    }
}
