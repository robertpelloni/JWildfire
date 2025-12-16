package org.jwildfire.swing;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.PropertySheet;
import org.jwildfire.base.Prefs;
import org.jwildfire.base.fx.PropertySheetFactory;

public class PreferencesController {

    @FXML
    private BorderPane rootPane;

    private PropertySheet propertySheet;
    private Prefs prefs;
    private Runnable onSave;
    private Runnable onCancel;

    public void initialize() {
        propertySheet = new PropertySheet();
        propertySheet.setMode(PropertySheet.Mode.CATEGORY);
        propertySheet.setSearchBoxVisible(true);
        rootPane.setCenter(propertySheet);
    }

    public void setPrefs(Prefs prefs) {
        this.prefs = prefs;
        if (prefs != null) {
            propertySheet.getItems().setAll(PropertySheetFactory.createItems(prefs));
        }
    }

    public void setOnSave(Runnable onSave) {
        this.onSave = onSave;
    }

    public void setOnCancel(Runnable onCancel) {
        this.onCancel = onCancel;
    }

    @FXML
    private void onSave() {
        if (prefs != null) {
            try {
                // The PropertySheet items modify the bean directly via setValue.
                // So the 'prefs' object is already updated.
                // We just need to save it.
                prefs.saveToFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (onSave != null) {
            onSave.run();
        }
    }

    @FXML
    private void onCancel() {
        if (onCancel != null) {
            onCancel.run();
        }
    }
}
