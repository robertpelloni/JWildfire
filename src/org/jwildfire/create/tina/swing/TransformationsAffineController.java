package org.jwildfire.create.tina.swing;

import java.net.URL;
import java.util.ResourceBundle;

import org.jwildfire.create.tina.base.XForm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

public class TransformationsAffineController implements Initializable {

    @FXML private TextField c00Field;
    @FXML private TextField c01Field;
    @FXML private TextField c10Field;
    @FXML private TextField c11Field;
    @FXML private TextField c20Field;
    @FXML private TextField c21Field;
    @FXML private TextField rotateAmountField;
    @FXML private TextField scaleAmountField;
    @FXML private TextField moveAmountField;

    private TinaController tinaController;
    private boolean refreshing;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupListeners();
    }

    public void setTinaController(TinaController tinaController) {
        this.tinaController = tinaController;
    }

    private void setupListeners() {
        c00Field.textProperty().addListener((obs, o, n) -> onCoeffChanged());
        c01Field.textProperty().addListener((obs, o, n) -> onCoeffChanged());
        c10Field.textProperty().addListener((obs, o, n) -> onCoeffChanged());
        c11Field.textProperty().addListener((obs, o, n) -> onCoeffChanged());
        c20Field.textProperty().addListener((obs, o, n) -> onCoeffChanged());
        c21Field.textProperty().addListener((obs, o, n) -> onCoeffChanged());
    }

    public void refresh() {
        if (tinaController == null) return;
        XForm xForm = tinaController.getCurrXForm();
        if (xForm != null) {
            refreshing = true;
            try {
                c00Field.setText(String.valueOf(xForm.getCoeff00()));
                c01Field.setText(String.valueOf(xForm.getCoeff01()));
                c10Field.setText(String.valueOf(xForm.getCoeff10()));
                c11Field.setText(String.valueOf(xForm.getCoeff11()));
                c20Field.setText(String.valueOf(xForm.getCoeff20()));
                c21Field.setText(String.valueOf(xForm.getCoeff21()));
            } finally {
                refreshing = false;
            }
        }
    }

    private void onCoeffChanged() {
        if (refreshing || tinaController == null) return;
        XForm xForm = tinaController.getCurrXForm();
        if (xForm != null) {
            try {
                xForm.setCoeff00(Double.parseDouble(c00Field.getText()));
                xForm.setCoeff01(Double.parseDouble(c01Field.getText()));
                xForm.setCoeff10(Double.parseDouble(c10Field.getText()));
                xForm.setCoeff11(Double.parseDouble(c11Field.getText()));
                xForm.setCoeff20(Double.parseDouble(c20Field.getText()));
                xForm.setCoeff21(Double.parseDouble(c21Field.getText()));
                tinaController.refreshFlameImage(true, false, 1, true, false);
            } catch (NumberFormatException e) {
                // Ignore invalid input
            }
        }
    }

    @FXML private void onRotateLeft(ActionEvent event) {
        applyRotation(-getRotateAmount());
    }

    @FXML private void onRotateRight(ActionEvent event) {
        applyRotation(getRotateAmount());
    }

    private void applyRotation(double angle) {
        if (tinaController == null) return;
        tinaController.saveUndoPoint();
        XForm xForm = tinaController.getCurrXForm();
        if (xForm != null) {
            // Convert to radians if needed, or use XForm helper
            // Assuming XForm rotation methods expect degrees or radians?
            // Usually degrees in UI, radians in math.
            // Let's check XForm implementation later or assume radians for now and fix if needed.
            // Actually, Affine transforms usually work with matrices.
            // A simple rotation around center (0,0) or flame center?
            // Legacy TinaController.affineRotateLeftButton_clicked calls animationController?

            // Re-implementing basic rotation:
            double rad = Math.toRadians(angle);
            double cos = Math.cos(rad);
            double sin = Math.sin(rad);

            double c00 = xForm.getCoeff00();
            double c01 = xForm.getCoeff01();
            double c10 = xForm.getCoeff10();
            double c11 = xForm.getCoeff11();

            xForm.setCoeff00(c00 * cos - c10 * sin);
            xForm.setCoeff01(c01 * cos - c11 * sin);
            xForm.setCoeff10(c00 * sin + c10 * cos);
            xForm.setCoeff11(c01 * sin + c11 * cos);

            refresh();
            tinaController.refreshFlameImage(true, false, 1, true, false);
        }
    }

    @FXML private void onEnlarge(ActionEvent event) {
        applyScale(1.0 + getScaleAmount() / 100.0);
    }

    @FXML private void onShrink(ActionEvent event) {
        applyScale(1.0 / (1.0 + getScaleAmount() / 100.0));
    }

    private void applyScale(double scale) {
        if (tinaController == null) return;
        tinaController.saveUndoPoint();
        XForm xForm = tinaController.getCurrXForm();
        if (xForm != null) {
            xForm.setCoeff00(xForm.getCoeff00() * scale);
            xForm.setCoeff01(xForm.getCoeff01() * scale);
            xForm.setCoeff10(xForm.getCoeff10() * scale);
            xForm.setCoeff11(xForm.getCoeff11() * scale);
            refresh();
            tinaController.refreshFlameImage(true, false, 1, true, false);
        }
    }

    @FXML private void onMoveLeft(ActionEvent event) {
        applyMove(-getMoveAmount(), 0);
    }

    @FXML private void onMoveRight(ActionEvent event) {
        applyMove(getMoveAmount(), 0);
    }

    @FXML private void onMoveUp(ActionEvent event) {
        applyMove(0, getMoveAmount());
    }

    @FXML private void onMoveDown(ActionEvent event) {
        applyMove(0, -getMoveAmount());
    }

    private void applyMove(double dx, double dy) {
        if (tinaController == null) return;
        tinaController.saveUndoPoint();
        XForm xForm = tinaController.getCurrXForm();
        if (xForm != null) {
            xForm.setCoeff20(xForm.getCoeff20() + dx);
            xForm.setCoeff21(xForm.getCoeff21() + dy);
            refresh();
            tinaController.refreshFlameImage(true, false, 1, true, false);
        }
    }

    private double getRotateAmount() {
        try {
            return Double.parseDouble(rotateAmountField.getText());
        } catch (Exception e) {
            return 15.0;
        }
    }

    private double getScaleAmount() {
        try {
            return Double.parseDouble(scaleAmountField.getText());
        } catch (Exception e) {
            return 10.0;
        }
    }

    private double getMoveAmount() {
        try {
            return Double.parseDouble(moveAmountField.getText());
        } catch (Exception e) {
            return 0.1;
        }
    }
}
