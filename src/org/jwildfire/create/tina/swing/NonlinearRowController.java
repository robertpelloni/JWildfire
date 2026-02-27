package org.jwildfire.create.tina.swing;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class NonlinearRowController implements Initializable {

    @FXML private VBox rootBox;
    @FXML private Label varNameLbl;
    @FXML private ComboBox<String> varNameCmb;
    @FXML private TextField amountField;
    @FXML private ComboBox<String> paramNameCmb;
    @FXML private TextField paramValueField;
    @FXML private ToggleButton expandBtn;
    @FXML private ToggleButton preBtn;
    @FXML private ToggleButton postBtn;
    @FXML private Button randomizeBtn;
    @FXML private ToggleButton favouriteBtn;
    @FXML private VBox paramsContainer;

    private TinaController tinaController;
    private int index;
    private boolean refreshing;

    public void setTinaController(TinaController tinaController) {
        this.tinaController = tinaController;
    }

    public void setIndex(int index) {
        this.index = index;
        varNameLbl.setText("Var " + (index + 1));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        varNameCmb.getItems().addAll(VariationFuncList.getNameList());

        varNameCmb.valueProperty().addListener((obs, o, n) -> onVarNameChanged());
        amountField.textProperty().addListener((obs, o, n) -> onAmountChanged());
        paramNameCmb.valueProperty().addListener((obs, o, n) -> onParamNameChanged());
        paramValueField.textProperty().addListener((obs, o, n) -> onParamValueChanged());

        expandBtn.selectedProperty().addListener((obs, o, n) -> {
            paramsContainer.setVisible(n);
            paramsContainer.setManaged(n);
            if (n) rebuildParamsContainer();
        });

        preBtn.selectedProperty().addListener((obs, o, n) -> onPriorityChanged());
        postBtn.selectedProperty().addListener((obs, o, n) -> onPriorityChanged());

        // Hide params container initially
        paramsContainer.setVisible(false);
        paramsContainer.setManaged(false);
    }

    public void refresh(XForm xForm) {
        refreshing = true;
        try {
            if (xForm != null && index < xForm.getVariationCount()) {
                Variation var = xForm.getVariation(index);
                varNameCmb.setValue(var.getFunc().getName());
                amountField.setText(String.valueOf(var.getAmount()));

                // Priority
                preBtn.setSelected(var.getPriority() < 0 || var.getPriority() == 2);
                postBtn.setSelected(var.getPriority() > 0 || var.getPriority() == -2);

                // Params
                refreshParams(var);

                if (expandBtn.isSelected()) {
                    rebuildParamsContainer();
                }
            } else {
                varNameCmb.setValue(null);
                amountField.setText("");
                paramNameCmb.getItems().clear();
                paramValueField.setText("");
                preBtn.setSelected(false);
                postBtn.setSelected(false);
                paramsContainer.getChildren().clear();
            }
        } finally {
            refreshing = false;
        }
    }

    private void refreshParams(Variation var) {
        String selectedParam = paramNameCmb.getValue();
        paramNameCmb.getItems().clear();
        String[] paramNames = var.getFunc().getParameterNames();
        if (paramNames != null) {
            for (String p : paramNames) {
                paramNameCmb.getItems().add(p);
            }
        }

        if (selectedParam != null && paramNameCmb.getItems().contains(selectedParam)) {
            paramNameCmb.setValue(selectedParam);
        } else if (!paramNameCmb.getItems().isEmpty()) {
            paramNameCmb.getSelectionModel().selectFirst();
        }

        onParamNameChanged(); // Update value field
    }

    private void onVarNameChanged() {
        if (refreshing || tinaController == null) return;
        tinaController.saveUndoPoint();
        XForm xForm = tinaController.getCurrXForm();
        if (xForm != null) {
            String name = varNameCmb.getValue();
            if (name != null && !name.isEmpty()) {
                if (index < xForm.getVariationCount()) {
                    Variation var = xForm.getVariation(index);
                    if (!var.getFunc().getName().equals(name)) {
                        VariationFunc func = VariationFuncList.getVariationFuncInstance(name);
                        var.setFunc(func);
                        var.setPriority(func.getPriority());
                    }
                } else {
                    VariationFunc func = VariationFuncList.getVariationFuncInstance(name);
                    Variation var = new Variation();
                    var.setFunc(func);
                    var.setPriority(func.getPriority());
                    var.setAmount(getAmount());
                    xForm.addVariation(var);
                }
            } else {
                if (index < xForm.getVariationCount()) {
                    xForm.removeVariation(xForm.getVariation(index));
                }
            }
            refresh(xForm); // Re-read to ensure state
            tinaController.refreshFlameImage(true, false, 1, true, false);
        }
    }

    private void onAmountChanged() {
        if (refreshing || tinaController == null) return;
        XForm xForm = tinaController.getCurrXForm();
        if (xForm != null && index < xForm.getVariationCount()) {
            try {
                xForm.getVariation(index).setAmount(Double.parseDouble(amountField.getText()));
                tinaController.refreshFlameImage(true, false, 1, true, false);
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
    }

    private void onParamNameChanged() {
        if (tinaController == null) return;
        String param = paramNameCmb.getValue();
        XForm xForm = tinaController.getCurrXForm();
        if (xForm != null && index < xForm.getVariationCount() && param != null) {
            Variation var = xForm.getVariation(index);
            try {
                Object val = var.getFunc().getParameter(param);
                if (val != null) {
                    // Update field without triggering listener loop
                    boolean oldRefreshing = refreshing;
                    refreshing = true;
                    paramValueField.setText(val.toString());
                    refreshing = oldRefreshing;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void onParamValueChanged() {
        if (refreshing || tinaController == null) return;
        String param = paramNameCmb.getValue();
        XForm xForm = tinaController.getCurrXForm();
        if (xForm != null && index < xForm.getVariationCount() && param != null) {
            try {
                Variation var = xForm.getVariation(index);
                double val = Double.parseDouble(paramValueField.getText());
                // Handle int/double types? XForm API usually handles setParameter(String, double)
                var.getFunc().setParameter(param, val);
                tinaController.refreshFlameImage(true, false, 1, true, false);
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
    }

    private void onPriorityChanged() {
        if (refreshing || tinaController == null) return;
        XForm xForm = tinaController.getCurrXForm();
        if (xForm != null && index < xForm.getVariationCount()) {
            tinaController.saveUndoPoint();
            int priority = 0;
            if (preBtn.isSelected()) priority -= 1;
            if (postBtn.isSelected()) priority += 1;

            // Logic mimicking Swing:
            // if priority was 2/-2, and button pressed, it might need complex logic.
            // For now simple -1, 0, 1.
            xForm.getVariation(index).setPriority(priority);
            tinaController.refreshFlameImage(true, false, 1, true, false);
        }
    }

    @FXML private void onRandomize(ActionEvent event) {
        if (tinaController == null) return;
        XForm xForm = tinaController.getCurrXForm();
        if (xForm != null && index < xForm.getVariationCount()) {
            tinaController.saveUndoPoint();
            xForm.getVariation(index).getFunc().randomize();
            refresh(xForm);
            tinaController.refreshFlameImage(true, false, 1, true, false);
        }
    }

    private void rebuildParamsContainer() {
        paramsContainer.getChildren().clear();
        XForm xForm = tinaController.getCurrXForm();
        if (xForm != null && index < xForm.getVariationCount()) {
            Variation var = xForm.getVariation(index);
            String[] paramNames = var.getFunc().getParameterNames();
            if (paramNames != null) {
                for (String param : paramNames) {
                    HBox row = new HBox(5);
                    row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                    Label lbl = new Label(param);
                    lbl.setPrefWidth(80);

                    TextField field = new TextField();
                    field.setPrefWidth(60);
                    Object val = var.getFunc().getParameter(param);
                    field.setText(val != null ? val.toString() : "");

                    Slider slider = new Slider(-5, 5, 0); // Default range
                    slider.setPrefWidth(120);
                    try {
                        double dVal = Double.parseDouble(field.getText());
                        slider.setValue(dVal);
                    } catch (Exception e) {}

                    // Listeners
                    field.textProperty().addListener((obs, o, n) -> {
                        try {
                            double v = Double.parseDouble(n);
                            var.getFunc().setParameter(param, v);
                            slider.setValue(v);
                            tinaController.refreshFlameImage(true, false, 1, true, false);
                        } catch (Exception e) {}
                    });

                    slider.valueProperty().addListener((obs, o, n) -> {
                        if (slider.isPressed()) {
                            field.setText(String.format("%.3f", n.doubleValue()));
                        }
                    });

                    row.getChildren().addAll(lbl, field, slider);
                    paramsContainer.getChildren().add(row);
                }
            }
        }
    }

    private double getAmount() {
        try {
            return Double.parseDouble(amountField.getText());
        } catch (Exception e) {
            return 0.0;
        }
    }
}
