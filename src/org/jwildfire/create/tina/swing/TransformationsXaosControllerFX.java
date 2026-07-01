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
        // Not yet fully implemented, acts as a stub to avoid errors when TinaController tries to update it
    }
}
