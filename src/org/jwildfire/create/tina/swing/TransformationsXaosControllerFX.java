package org.jwildfire.create.tina.swing;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class TransformationsXaosControllerFX implements Initializable {

    @FXML private RadioButton viewAsToBtn;
    @FXML private RadioButton viewAsFromBtn;
    @FXML private TableView<XaosRow> relWeightsTable;
    @FXML private TableColumn<XaosRow, String> fromCol;
    @FXML private TableColumn<XaosRow, String> toCol;
    @FXML private TableColumn<XaosRow, String> weightCol;
    @FXML private TextField relWeightField;
    @FXML private Button zeroBtn;
    @FXML private Button oneBtn;
    @FXML private Button resetBtn;

    private TinaController tinaController;
    private boolean refreshing;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fromCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().from));
        toCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().to));
        weightCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().weight));

        viewAsToBtn.selectedProperty().addListener((obs, o, n) -> {
            if (!refreshing && tinaController != null) {
                tinaController.xaosViewAsChanged();
            }
        });
        viewAsFromBtn.selectedProperty().addListener((obs, o, n) -> {
            if (!refreshing && tinaController != null) {
                tinaController.xaosViewAsChanged();
            }
        });

        relWeightField.textProperty().addListener((obs, o, n) -> {
            if (!refreshing && tinaController != null) {
                tinaController.relWeightsREd_changed();
            }
        });

        relWeightsTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (!refreshing && tinaController != null) {
                tinaController.relWeightsTableClicked();
            }
        });
    }

    public void setTinaController(TinaController tinaController) {
        this.tinaController = tinaController;
    }

    @FXML
    private void zeroBtnClicked(ActionEvent event) {
        if (tinaController != null) {
            tinaController.saveUndoPoint();
            tinaController.relWeightsZeroButton_clicked();
        }
    }

    @FXML
    private void oneBtnClicked(ActionEvent event) {
        if (tinaController != null) {
            tinaController.saveUndoPoint();
            tinaController.relWeightsOneButton_clicked();
        }
    }

    @FXML
    private void resetBtnClicked(ActionEvent event) {
        if (tinaController != null) {
            tinaController.saveUndoPoint();
            tinaController.relWeightsResetButton_clicked();
        }
    }

    // Stub class to represent table rows for now
    public static class XaosRow {
        String from;
        String to;
        String weight;
        public XaosRow(String from, String to, String weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    public void refreshControls() {
        if (tinaController == null) return;
        org.jwildfire.create.tina.base.XForm xForm = tinaController.getCurrXForm();
        org.jwildfire.create.tina.base.Layer layer = tinaController.getCurrLayer();

        refreshing = true;
        try {
            ObservableList<XaosRow> items = FXCollections.observableArrayList();

            boolean viewAsTo = viewAsToBtn.isSelected();

            // Replicate table model logic
            if (layer != null && xForm != null && layer.getFinalXForms().indexOf(xForm) < 0 && layer.getBGXForms().indexOf(xForm) < 0) {
                int transformIndex = tinaController.getData().transformationsTable.getSelectedRow();
                if (transformIndex >= 0 && transformIndex < layer.getXForms().size()) {
                    for (int rowIndex = 0; rowIndex < layer.getXForms().size(); rowIndex++) {
                        String fromStr, toStr, weightStr;
                        if (viewAsTo) {
                            fromStr = (String) tinaController.getXFormCaption(layer.getXForms().get(transformIndex));
                            toStr = (String) tinaController.getXFormCaption(layer.getXForms().get(rowIndex));
                            weightStr = org.jwildfire.base.Tools.doubleToString(layer.getXForms().get(transformIndex).getModifiedWeights()[rowIndex]);
                        } else {
                            fromStr = (String) tinaController.getXFormCaption(layer.getXForms().get(rowIndex));
                            toStr = (String) tinaController.getXFormCaption(layer.getXForms().get(transformIndex));
                            weightStr = org.jwildfire.base.Tools.doubleToString(layer.getXForms().get(rowIndex).getModifiedWeights()[transformIndex]);
                        }
                        items.add(new XaosRow(fromStr, toStr, weightStr));
                    }
                }
            }
            relWeightsTable.setItems(items);

            int xaosRow = tinaController.getData().relWeightsTable.getSelectedRow();
            if (layer != null && xaosRow >= 0 && xaosRow < items.size()) {
                relWeightsTable.getSelectionModel().select(xaosRow);

                int transformRow = tinaController.getData().transformationsTable.getSelectedRow();
                if (transformRow >= 0 && transformRow < layer.getXForms().size()) {
                    if (viewAsTo) {
                        relWeightField.setText(org.jwildfire.base.Tools.doubleToString(layer.getXForms().get(transformRow).getModifiedWeights()[xaosRow]));
                    } else {
                        relWeightField.setText(org.jwildfire.base.Tools.doubleToString(layer.getXForms().get(xaosRow).getModifiedWeights()[transformRow]));
                    }
                }
            } else {
                relWeightField.setText("");
            }

            viewAsToBtn.setSelected(tinaController.getData().xaosViewAsToBtn.isSelected());
            viewAsFromBtn.setSelected(tinaController.getData().xaosViewAsFromBtn.isSelected());
        } finally {
            refreshing = false;
        }
    }
}
