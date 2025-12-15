package org.jwildfire.swing;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class ListOfChangesController {

    @FXML
    private WebView changesWebView;

    @FXML
    public void initialize() {
        loadChanges();
    }

    private void loadChanges() {
        try {
            InputStream is = getClass().getResourceAsStream("CHANGES.txt");
            if (is != null) {
                StringBuilder content = new StringBuilder();
                String lineFeed = System.getProperty("line.separator");
                String line;
                Reader r = new InputStreamReader(is, "utf-8");
                BufferedReader in = new BufferedReader(r);
                while ((line = in.readLine()) != null) {
                    content.append(line).append(lineFeed);
                }
                in.close();

                // Use <pre> tag to preserve formatting, similar to original implementation
                String htmlContent = "<html><body style='font-family: monospace;'><pre>" + content.toString() + "</pre></body></html>";
                changesWebView.getEngine().loadContent(htmlContent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
