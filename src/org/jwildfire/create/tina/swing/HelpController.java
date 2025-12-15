package org.jwildfire.create.tina.swing;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;

public class HelpController {

    @FXML private WebView helpWebView;
    @FXML private WebView apophysisHintsWebView;
    @FXML private WebView meshGenHintsWebView;
    @FXML private WebView colorTypesWebView;

    public void setHelpContent(String content) {
        loadContent(helpWebView, content);
    }

    public void setApophysisHintsContent(String content) {
        loadContent(apophysisHintsWebView, content);
    }

    public void setMeshGenHintsContent(String content) {
        loadContent(meshGenHintsWebView, content);
    }

    public void setColorTypesContent(String content) {
        loadContent(colorTypesWebView, content);
    }

    private void loadContent(WebView view, String content) {
        if (view != null && content != null) {
            view.getEngine().loadContent(content);
        }
    }
}
