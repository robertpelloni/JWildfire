package org.jwildfire.create.tina.swing;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;

public class LayersTabControllerFX {

    private TinaController tinaController;
    private boolean isUpdating = false;

    @FXML private ListView<String> layersListView;
    @FXML private Button layerAddBtn;
    @FXML private Button layerDeleteBtn;
    @FXML private Button layerDuplicateBtn;
    @FXML private Button layerExtractBtn;
    @FXML private Button layerAppendBtn;
    @FXML private TextField layerDensityField;
    @FXML private TextField layerWeightField;
    @FXML private CheckBox layerVisibleCheckBox;
    @FXML private Button layerPreviewBtn;
    @FXML private Button layerHideOthersBtn;
    @FXML private Button layerShowAllBtn;

    public void setTinaController(TinaController tinaController) {
        this.tinaController = tinaController;
        setupListeners();
    }

    private void setupListeners() {
        layersListView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (!isUpdating && newValue.intValue() >= 0) {
                if (tinaController != null) {
                    tinaController.getData().layersTable.getSelectionModel().setSelectionInterval(newValue.intValue(), newValue.intValue());
                    tinaController.layersTableClicked();
                }
            }
        });

        layerDensityField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isUpdating && tinaController != null && tinaController.getCurrLayer() != null) {
                try {
                    double val = Double.parseDouble(newValue.replace(',', '.'));
                    tinaController.getCurrLayer().setDensity(val);
                    tinaController.refreshUI(false);
                } catch (NumberFormatException e) {
                }
            }
        });

        layerWeightField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isUpdating && tinaController != null && tinaController.getCurrLayer() != null) {
                try {
                    double val = Double.parseDouble(newValue.replace(',', '.'));
                    tinaController.getCurrLayer().setWeight(val);
                    tinaController.refreshUI(false);
                } catch (NumberFormatException e) {
                }
            }
        });

        layerVisibleCheckBox.setOnAction(e -> {
            if (!isUpdating && tinaController != null && tinaController.getCurrLayer() != null) {
                tinaController.getData().layerVisibleBtn.setSelected(layerVisibleCheckBox.isSelected());
                tinaController.layerVisibilityButton_clicked();
            }
        });
    }

    public void refreshControls() {
        if (tinaController == null) return;

        isUpdating = true;
        try {
            Flame flame = tinaController.getCurrFlame();
            Layer layer = tinaController.getCurrLayer();

            boolean flameExists = flame != null;
            boolean layerExists = layer != null;

            layerAddBtn.setDisable(!flameExists);
            layerDeleteBtn.setDisable(!(flameExists && layerExists && flame.getLayers().size() > 1));
            layerDuplicateBtn.setDisable(!layerExists);
            layerExtractBtn.setDisable(!layerExists);
            layersListView.setDisable(!flameExists);
            layerAppendBtn.setDisable(!flameExists);
            layerVisibleCheckBox.setDisable(!layerExists);
            layerPreviewBtn.setDisable(!flameExists);
            layerHideOthersBtn.setDisable(!layerExists);
            layerShowAllBtn.setDisable(!flameExists);

            if (layerExists) {
                layerDensityField.setText(String.valueOf(layer.getDensity()));
                layerWeightField.setText(String.valueOf(layer.getWeight()));
            } else {
                layerDensityField.setText("");
                layerWeightField.setText("");
            }
            layerDensityField.setDisable(!layerExists);
            layerWeightField.setDisable(!layerExists);

            if (layerExists) {
                layerVisibleCheckBox.setSelected(layer.isVisible());
            }

            if (flameExists) {
                ObservableList<String> items = FXCollections.observableArrayList();
                for (int i = 0; i < flame.getLayers().size(); i++) {
                    Layer l = flame.getLayers().get(i);
                    items.add("Layer " + (i + 1) + " (W: " + String.format("%.2f", l.getWeight()) + ", " + (l.isVisible() ? "Visible" : "Hidden") + ")");
                }
                layersListView.setItems(items);
                layersListView.getSelectionModel().select(flame.getLayers().indexOf(layer));
            } else {
                layersListView.getItems().clear();
            }

        } finally {
            isUpdating = false;
        }
    }

    @FXML private void onLayerAdd(ActionEvent event) {
        if (tinaController != null) tinaController.addLayerBtn_clicked();
    }

    @FXML private void onLayerDelete(ActionEvent event) {
        if (tinaController != null) tinaController.deleteLayerBtn_clicked();
    }

    @FXML private void onLayerDuplicate(ActionEvent event) {
        if (tinaController != null) tinaController.duplicateLayerBtn_clicked();
    }

    @FXML private void onLayerExtract(ActionEvent event) {
        if (tinaController != null) tinaController.extractLayerBtn_clicked();
    }

    @FXML private void onLayerAppend(ActionEvent event) {
        if (tinaController != null) tinaController.layerAppendModeBtnClicked();
    }

    @FXML private void onLayerPreview(ActionEvent event) {
        if (tinaController != null) tinaController.layerPreviewBtnClicked();
    }

    @FXML private void onLayerHideOthers(ActionEvent event) {
        if (tinaController != null) tinaController.layerHideAllOthersButton_clicked();
    }

    @FXML private void onLayerShowAll(ActionEvent event) {
        if (tinaController != null) tinaController.layerShowAllButton_clicked();
    }
}
