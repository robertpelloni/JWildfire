package org.jwildfire.create.tina.dance;

import javafx.scene.control.TreeItem;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.dance.model.AbstractProperty;
import org.jwildfire.create.tina.dance.model.AnimationModelService;
import org.jwildfire.create.tina.dance.model.FlamePropertyPath;
import org.jwildfire.create.tina.dance.model.PlainProperty;
import org.jwildfire.create.tina.dance.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;

public class FlamePropertiesTreeServiceFX {

    public String getFlameCaption(Flame pFlame) {
        return (pFlame.getName() == null || pFlame.getName().equals("")) ? String.valueOf(pFlame.hashCode()) : pFlame.getName();
    }

    public void refreshFlamePropertiesTree(TreeItem<FlamePropertyItem> root, DancingFlameProject pProject) {
        root.getChildren().clear();
        for (Flame flame : pProject.getFlames()) {
            FlamePropertyItem item = new FlamePropertyItem(getFlameCaption(flame), flame, false);
            TreeItem<FlamePropertyItem> flameNode = new TreeItem<>(item);
            PropertyModel model = AnimationModelService.createModel(flame);
            addNodesToTree(model, flameNode);
            root.getChildren().add(flameNode);
        }
    }

    private void addNodesToTree(PropertyModel pModel, TreeItem<FlamePropertyItem> pParentNode) {
        for (PropertyModel subNode : pModel.getChields()) {
            FlamePropertyItem item = new FlamePropertyItem(subNode.getName(), subNode, false);
            TreeItem<FlamePropertyItem> child = new TreeItem<>(item);
            pParentNode.getChildren().add(child);
            addNodesToTree(subNode, child);
        }
        for (PlainProperty property : pModel.getProperties()) {
            FlamePropertyItem item = new FlamePropertyItem(property.getName(), property, true);
            TreeItem<FlamePropertyItem> child = new TreeItem<>(item);
            pParentNode.getChildren().add(child);
        }
    }

    public boolean isPlainPropertySelected(TreeItem<FlamePropertyItem> selectedItem) {
        if (selectedItem != null && selectedItem.getValue() != null) {
            return selectedItem.getValue().getData() instanceof PlainProperty;
        }
        return false;
    }

    public FlamePropertyPath getSelectedPropertyPath(TreeItem<FlamePropertyItem> selectedItem) {
        if (selectedItem != null) {
            // Traverse up to find the flame
            List<String> path = new ArrayList<>();
            TreeItem<FlamePropertyItem> current = selectedItem;
            Flame flame = null;

            while (current != null) {
                Object data = current.getValue().getData();
                if (data instanceof Flame) {
                    flame = (Flame) data;
                    break; // Don't add flame name to path list as per legacy implementation (it skips index 1)
                } else if (data instanceof AbstractProperty) {
                    path.add(0, ((AbstractProperty) data).getName());
                }
                current = current.getParent();
            }

            if (flame != null && !path.isEmpty()) {
                return new FlamePropertyPath(flame, path);
            }
        }
        return null;
    }
}
