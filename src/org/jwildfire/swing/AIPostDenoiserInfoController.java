package org.jwildfire.swing;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class AIPostDenoiserInfoController {

    @FXML private WebView optiXWebView;
    @FXML private WebView oidnWebView;

    @FXML
    public void initialize() {
        loadInfo("OptiX_Denoiser.txt", optiXWebView);
        loadInfo("OIDN_Denoiser.txt", oidnWebView);
    }

    private void loadInfo(String filename, WebView webView) {
        try {
            InputStream is = getClass().getResourceAsStream(filename);
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

                String htmlContent = "<html><body style='font-family: monospace;'><pre>" + content.toString() + "</pre></body></html>";
                webView.getEngine().loadContent(htmlContent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
