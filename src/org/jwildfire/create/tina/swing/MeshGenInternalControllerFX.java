package org.jwildfire.create.tina.swing;

import java.net.URL;
import java.util.ResourceBundle;

import org.jwildfire.create.tina.meshgen.filter.PreFilterType;
import org.jwildfire.create.tina.meshgen.render.MeshGenRenderOutputType;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class MeshGenInternalControllerFX implements Initializable {

    @FXML private TextField resolutionXField;
    @FXML private TextField resolutionYField;
    @FXML private TextField resolutionZField;
    @FXML private TextField qualityField;
    @FXML private ComboBox<MeshGenRenderOutputType> outputTypeCmb;
    @FXML private ComboBox<PreFilterType> preFilter1Cmb;
    @FXML private ComboBox<PreFilterType> preFilter2Cmb;
    @FXML private TextField filenameField;

    private TinaController tinaController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        outputTypeCmb.getItems().addAll(MeshGenRenderOutputType.values());
        outputTypeCmb.getSelectionModel().select(MeshGenRenderOutputType.VOXELSTACK);

        preFilter1Cmb.getItems().addAll(PreFilterType.values());
        preFilter1Cmb.getSelectionModel().select(PreFilterType.NONE);

        preFilter2Cmb.getItems().addAll(PreFilterType.values());
        preFilter2Cmb.getSelectionModel().select(PreFilterType.NONE);
    }

    public void setTinaController(TinaController tinaController) {
        this.tinaController = tinaController;
    }

    @FXML private void onBrowseFilename(ActionEvent event) {
        // TODO: File chooser logic
    }

    @FXML private void onGenerate(ActionEvent event) {
        // TODO: Start generation logic using tinaController.getMeshGenController()
    }

    @FXML private void onCancel(ActionEvent event) {
        // TODO: Cancel logic
    }

    // Getters for integration with legacy logic if needed, or better to expose properties
    public ComboBox<MeshGenRenderOutputType> getOutputTypeCmb() {
        return outputTypeCmb;
    }

    public ComboBox<PreFilterType> getPreFilter1Cmb() {
        return preFilter1Cmb;
    }

    public ComboBox<PreFilterType> getPreFilter2Cmb() {
        return preFilter2Cmb;
    }
}
