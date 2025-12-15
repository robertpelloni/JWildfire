package org.jwildfire.swing;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.jwildfire.create.tina.variation.RessourceManager;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class SystemInfoController {

    @FXML
    private TextArea infoTextArea;

    @FXML
    private Button refreshButton;

    @FXML
    private Button clearCacheButton;

    @FXML
    public void initialize() {
        refresh();
    }

    @FXML
    private void onRefresh() {
        refresh();
    }

    @FXML
    private void onClearCache() {
        RessourceManager.clearAll();
        System.gc();
        refresh();
    }

    public void refresh() {
        if (infoTextArea != null) {
            infoTextArea.setText(collectInfo());
        }
    }

    private String collectInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Operating system: ").append(System.getProperty("os.name")).append("\n");
        sb.append("Available processors: ").append(Runtime.getRuntime().availableProcessors()).append(" cores\n\n");
        sb.append("Java version: ").append(System.getProperty("java.version")).append("\n\n");
        
        long allocatedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        long presumableFreeMemory = Runtime.getRuntime().maxMemory() - allocatedMemory;
        
        sb.append("Maximum memory: ").append(Runtime.getRuntime().maxMemory() == Long.MAX_VALUE ? "no limit" : formatMemoryInGB(Runtime.getRuntime().maxMemory()) + " GB").append("\n");
        sb.append("Free memory (approximated): ").append(formatMemoryInGB(presumableFreeMemory)).append(" GB\n\n");
        sb.append("Press the [Clear cache]-button to free any resources (images, meshes, fonts, ...) which are currently hold in memory in order to speed up future calculations.\n\n");
        return sb.toString();
    }

    private String formatMemoryInGB(long memory) {
        NumberFormat fmt = DecimalFormat.getInstance(Locale.US);
        fmt.setGroupingUsed(false);
        fmt.setMaximumFractionDigits(1);
        fmt.setMinimumIntegerDigits(1);
        return fmt.format(memory / 1024.0 / 1024.0 / 1024.0);
    }
}
