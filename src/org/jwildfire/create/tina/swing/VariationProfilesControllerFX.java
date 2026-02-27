package org.jwildfire.create.tina.swing;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.jwildfire.base.VariationProfile;
import org.jwildfire.base.VariationProfileRepository;
import org.jwildfire.base.VariationProfileType;
import org.jwildfire.create.tina.variation.VariationFuncList;
import org.jwildfire.create.tina.variation.VariationFuncType;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VariationProfilesControllerFX implements Initializable {

    @FXML private TableView<VariationProfile> profilesTable;
    @FXML private TableColumn<VariationProfile, String> profileNameCol;
    @FXML private TableColumn<VariationProfile, String> profileTypeCol;

    @FXML private Button newProfileBtn;
    @FXML private Button duplicateProfileBtn;
    @FXML private Button deleteProfileBtn;

    @FXML private TextField profileNameField;
    @FXML private ComboBox<VariationProfileType> profileTypeCmb;
    @FXML private CheckBox defaultCheckbox;
    @FXML private TextField profileStatusField;
    @FXML private VBox variationsContainer;

    private TinaController tinaController;
    private List<VariationProfile> currProfiles;
    private VariationProfile selectedProfile;
    private boolean ignoreUpdates;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Init table columns
        profileNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        profileTypeCol.setCellValueFactory(cellData -> {
            VariationProfileType type = cellData.getValue().getVariationProfileType();
            return new SimpleStringProperty(type != null ? type.toString() : "");
        });

        // Init combo
        profileTypeCmb.getItems().addAll(VariationProfileType.values());

        // Listeners
        profilesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> selectProfile(newVal));

        profileNameField.textProperty().addListener((obs, o, n) -> {
            if (!ignoreUpdates && selectedProfile != null) {
                selectedProfile.setName(n);
                profilesTable.refresh();
            }
        });

        profileTypeCmb.valueProperty().addListener((obs, o, n) -> {
            if (!ignoreUpdates && selectedProfile != null) {
                selectedProfile.setVariationProfileType(n);
                profilesTable.refresh();
                refreshVariationsPanel();
                refreshStatusText();
            }
        });

        defaultCheckbox.selectedProperty().addListener((obs, o, n) -> {
            if (!ignoreUpdates && selectedProfile != null) {
                selectedProfile.setDefaultProfile(n);
            }
        });

        // Load data
        loadProfiles();
    }

    public void setTinaController(TinaController tinaController) {
        this.tinaController = tinaController;
    }

    private void loadProfiles() {
        // Deep copy profiles to avoid modifying repository directly until save
        currProfiles = VariationProfileRepository.getProfiles().stream()
                .map(VariationProfile::makeCopy)
                .collect(Collectors.toList());
        profilesTable.getItems().setAll(currProfiles);

        if (!currProfiles.isEmpty()) {
            profilesTable.getSelectionModel().select(0);
        }
    }

    private void selectProfile(VariationProfile profile) {
        selectedProfile = profile;
        ignoreUpdates = true;

        if (profile != null) {
            profileNameField.setText(profile.getName());
            profileTypeCmb.setValue(profile.getVariationProfileType());
            defaultCheckbox.setSelected(profile.isDefaultProfile());
            profileNameField.setDisable(false);
            profileTypeCmb.setDisable(false);
            defaultCheckbox.setDisable(false);
            duplicateProfileBtn.setDisable(false);
            deleteProfileBtn.setDisable(false);
        } else {
            profileNameField.clear();
            profileTypeCmb.setValue(null);
            defaultCheckbox.setSelected(false);
            profileStatusField.clear();
            variationsContainer.getChildren().clear();

            profileNameField.setDisable(true);
            profileTypeCmb.setDisable(true);
            defaultCheckbox.setDisable(true);
            duplicateProfileBtn.setDisable(true);
            deleteProfileBtn.setDisable(true);
        }

        ignoreUpdates = false;
        if (profile != null) {
            refreshVariationsPanel();
            refreshStatusText();
        }
    }

    @FXML private void onNewProfile(ActionEvent event) {
        VariationProfile profile = new VariationProfile();
        profile.setName(getUniqueName("New Profile"));
        profile.setVariationProfileType(VariationProfileType.INCLUDE_VARIATIONS);
        currProfiles.add(profile);
        profilesTable.getItems().add(profile);
        profilesTable.getSelectionModel().select(profile);
    }

    @FXML private void onDuplicateProfile(ActionEvent event) {
        if (selectedProfile != null) {
            VariationProfile copy = selectedProfile.makeCopy();
            copy.setName(getUniqueName(selectedProfile.getName()));
            currProfiles.add(copy);
            profilesTable.getItems().add(copy);
            profilesTable.getSelectionModel().select(copy);
        }
    }

    @FXML private void onDeleteProfile(ActionEvent event) {
        if (selectedProfile != null) {
            // Confirmation dialog could be added here
            currProfiles.remove(selectedProfile);
            profilesTable.getItems().remove(selectedProfile);
        }
    }

    @FXML private void onSaveAndApply(ActionEvent event) {
        VariationProfileRepository.updateVariationProfiles(currProfiles);
        if (tinaController != null) {
            // Refresh main window logic
            ComboboxTools.initVariationProfileCmb(tinaController.getData().tinaVariationProfile1Cmb, true, false);
            ComboboxTools.initVariationProfileCmb(tinaController.getData().tinaVariationProfile2Cmb, false, true);
            tinaController.tinaVariationProfile1Cmb_changed();
        }
        closeWindow();
    }

    @FXML private void onCancel(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) newProfileBtn.getScene().getWindow();
        stage.hide();
    }

    private String getUniqueName(String baseName) {
        int counter = 0;
        while (true) {
            String name = counter > 0 ? baseName + "-" + counter : baseName;
            counter++;
            String finalName = name;
            if (currProfiles.stream().noneMatch(p -> finalName.equals(p.getName()))) {
                return name;
            }
        }
    }

    private void refreshVariationsPanel() {
        variationsContainer.getChildren().clear();
        if (selectedProfile == null || selectedProfile.getVariationProfileType() == null) return;

        List<String> items;
        boolean isTypes = selectedProfile.getVariationProfileType() == VariationProfileType.INCLUDE_TYPES ||
                          selectedProfile.getVariationProfileType() == VariationProfileType.EXCLUDE_TYPES;

        if (isTypes) {
            items = Arrays.stream(VariationFuncType.values())
                    .map(VariationFuncType::getCaption)
                    .sorted()
                    .collect(Collectors.toList());
        } else {
            items = new ArrayList<>(VariationFuncList.getNameList());
            Collections.sort(items);
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);

        int cols = 4;
        for (int i = 0; i < items.size(); i++) {
            String item = items.get(i);
            CheckBox cb = new CheckBox(item);

            // Set initial state
            if (isTypes) {
                VariationFuncType type = getVariationTypeByCaption(item);
                cb.setSelected(selectedProfile.getVariationTypes().contains(type));
            } else {
                cb.setSelected(selectedProfile.getVariations().contains(item));
            }

            // Add listener
            cb.selectedProperty().addListener((obs, o, n) -> {
                if (isTypes) {
                    VariationFuncType type = getVariationTypeByCaption(item);
                    if (n) selectedProfile.getVariationTypes().add(type);
                    else selectedProfile.getVariationTypes().remove(type);
                } else {
                    if (n) selectedProfile.getVariations().add(item);
                    else selectedProfile.getVariations().remove(item);
                }
                refreshStatusText();
            });

            grid.add(cb, i % cols, i / cols);
        }

        variationsContainer.getChildren().add(grid);
    }

    private Map<String, VariationFuncType> typeMap;
    private VariationFuncType getVariationTypeByCaption(String caption) {
        if (typeMap == null) {
            typeMap = new HashMap<>();
            for (VariationFuncType t : VariationFuncType.values()) {
                typeMap.put(t.getCaption(), t);
            }
        }
        return typeMap.get(caption);
    }

    private void refreshStatusText() {
        if (selectedProfile == null || selectedProfile.getVariationProfileType() == null) {
            profileStatusField.setText("");
            return;
        }

        int selected, total;
        boolean isTypes = selectedProfile.getVariationProfileType() == VariationProfileType.INCLUDE_TYPES ||
                          selectedProfile.getVariationProfileType() == VariationProfileType.EXCLUDE_TYPES;

        if (isTypes) {
            selected = selectedProfile.getVariationTypes().size();
            total = VariationFuncType.values().length;
        } else {
            selected = selectedProfile.getVariations().size();
            total = VariationFuncList.getNameList().size();
        }

        String typeStr = isTypes ? "types" : "variations";
        String modeStr = selectedProfile.getVariationProfileType().toString().startsWith("INCLUDE") ? "included" : "excluded";

        profileStatusField.setText(String.format("%d %s %s / %d total", selected, typeStr, modeStr, total));
    }
}
