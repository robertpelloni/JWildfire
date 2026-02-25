package org.jwildfire.create.tina.swing;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import org.jwildfire.create.tina.base.XForm;

public class TransformationsNonlinearController implements Initializable {

    @FXML private VBox rowsContainer;

    private TinaController tinaController;
    private final List<NonlinearRowController> rowControllers = new ArrayList<>();
    private static final int ROW_COUNT = 12;

    public void setTinaController(TinaController tinaController) {
        this.tinaController = tinaController;
        for (NonlinearRowController controller : rowControllers) {
            controller.setTinaController(tinaController);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (int i = 0; i < ROW_COUNT; i++) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/jwildfire/create/tina/swing/nonlinear_row.fxml"));
                Parent row = loader.load();
                NonlinearRowController controller = loader.getController();
                controller.setIndex(i);
                rowControllers.add(controller);
                rowsContainer.getChildren().add(row);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void refresh() {
        if (tinaController == null) return;
        XForm xForm = tinaController.getCurrXForm();
        for (NonlinearRowController controller : rowControllers) {
            controller.refresh(xForm);
        }
    }
}
