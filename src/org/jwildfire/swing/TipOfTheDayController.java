package org.jwildfire.swing;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.web.WebView;
import org.jwildfire.base.Prefs;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class TipOfTheDayController {

    @FXML
    private WebView tipWebView;

    @FXML
    private Button prevButton;

    @FXML
    private Button nextButton;

    @FXML
    private CheckBox showOnStartupCheckBox;

    private List<String> tips;
    private int currentTipIndex = -1;

    @FXML
    public void initialize() {
        initializeTips();
        showOnStartupCheckBox.setSelected(Prefs.getPrefs().isShowTipsAtStartup());
        showOnStartupCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Prefs.getPrefs().setShowTipsAtStartup(newValue);
            try {
                Prefs.getPrefs().saveToFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        loadNextTip(); // Show the first tip (or next in sequence)
    }

    @FXML
    private void onPrev() {
        loadPrevTip();
    }

    @FXML
    private void onNext() {
        loadNextTip();
    }

    private void showTip(String content) {
        if (tipWebView != null) {
            tipWebView.getEngine().loadContent(content);
        }
    }

    private void initializeTips() {
        if (tips == null) {
            tips = new ArrayList<>();
            try {
                // Load from the same location as the original class
                // Since this controller is in the same package (org.jwildfire.swing), this should work
                InputStream is = getClass().getResourceAsStream("/org/jwildfire/swing/TipsOfTheDay.html");
                if (is == null) {
                    // Fallback try without absolute path if the resource structure is flat in the jar
                    is = getClass().getResourceAsStream("TipsOfTheDay.html");
                }
                
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

                    String fullContent = content.toString();
                    int p0 = -1;
                    while (true) {
                        final String startToken = "<html";
                        final String endToken = "</html>";
                        int p1 = fullContent.indexOf(startToken, p0 + 1);
                        if (p1 < 0) break;
                        int p2 = fullContent.indexOf(endToken, p1 + 1);
                        if (p2 < 0) break;

                        String fragment = fullContent.substring(p1, p2 + endToken.length());
                        tips.add(fragment);

                        p0 = p2 + endToken.length();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // Sync with prefs logic from original class
            currentTipIndex = Prefs.getPrefs().getLastTip();
            if (currentTipIndex < 0 || currentTipIndex >= tips.size()) {
                currentTipIndex = 0;
            }
            // The original logic increments lastTip on init, let's preserve that behavior effectively
            // by saving the state when we actually show it.
        }
    }

    private void loadNextTip() {
        if (tips == null || tips.isEmpty()) return;

        currentTipIndex++;
        if (currentTipIndex >= tips.size()) {
            currentTipIndex = 0; // Wrap around
        }
        showTip(tips.get(currentTipIndex));
        updatePrefs();
    }

    private void loadPrevTip() {
        if (tips == null || tips.isEmpty()) return;

        currentTipIndex--;
        if (currentTipIndex < 0) {
            currentTipIndex = tips.size() - 1; // Wrap around
        }
        showTip(tips.get(currentTipIndex));
        updatePrefs();
    }
    
    private void updatePrefs() {
        // Save the index for next time (store as next index to show)
        Prefs.getPrefs().setLastTip(currentTipIndex); 
        try {
            Prefs.getPrefs().saveToFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
