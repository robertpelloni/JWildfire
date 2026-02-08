package org.jwildfire.create.tina.swing;

import java.net.URL;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.jwildfire.create.tina.script.swing.JWFScriptUserNode;
import org.jwildfire.swing.ErrorHandler;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;

public class ScriptEditorController implements Initializable {

    @FXML
    private StackPane editorContainer;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private Label statusLabel;

    private CodeArea codeArea;
    private ScriptEditDialog parentDialog;
    private TinaController tinaController;
    private ErrorHandler errorHandler;
    private JWFScriptUserNode scriptNode;

    // Syntax Highlighting Patterns
    private static final String[] KEYWORDS = new String[] {
            "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else",
            "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import",
            "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        codeArea = new CodeArea();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

        // Subscription to text changes for highlighting
        codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                .subscribe(change -> {
                    codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText()));
                });

        editorContainer.getChildren().add(codeArea);

        // Load CSS
        URL cssUrl = getClass().getResource("java-keywords.css");
        if (cssUrl != null) {
            codeArea.getStylesheets().add(cssUrl.toExternalForm());
        }
    }

    public void setContext(ScriptEditDialog parentDialog, TinaController tinaController, ErrorHandler errorHandler) {
        this.parentDialog = parentDialog;
        this.tinaController = tinaController;
        this.errorHandler = errorHandler;
    }

    public void setScriptNode(JWFScriptUserNode pScriptNode) {
        this.scriptNode = pScriptNode;
        if (scriptNode != null) {
            codeArea.replaceText(0, 0, scriptNode.getScript());
            descriptionArea.setText(scriptNode.getDescription());
            statusLabel.setText("Editing: " + scriptNode.getUserObject().toString());
        }
    }

    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("PAREN") != null ? "paren" :
                    matcher.group("BRACE") != null ? "brace" :
                    matcher.group("BRACKET") != null ? "bracket" :
                    matcher.group("SEMICOLON") != null ? "semicolon" :
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("COMMENT") != null ? "comment" :
                    null; /* never happens */ assert styleClass != null;

            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    @FXML
    private void onCompile() {
        setStatus("Compiling...");
        String script = codeArea.getText();

        // Run on Swing Thread
        SwingUtilities.invokeLater(() -> {
            try {
                tinaController.compileScript(script);
                Platform.runLater(() -> setStatus("Compilation successful."));
            } catch (Throwable ex) {
                handleError(ex);
            }
        });
    }

    @FXML
    private void onRun() {
        setStatus("Running...");
        String script = codeArea.getText();
        String filename = scriptNode != null ? scriptNode.getFilename() : "script.java";

        SwingUtilities.invokeLater(() -> {
            try {
                tinaController.runScript(filename, script);
                Platform.runLater(() -> setStatus("Script executed."));
            } catch (Throwable ex) {
                handleError(ex);
            }
        });
    }

    @FXML
    private void onSave() {
        if (scriptNode != null) {
            try {
                scriptNode.saveScript(codeArea.getText(), descriptionArea.getText());
                parentDialog.closeDialog();
            } catch (Exception ex) {
                handleError(ex);
            }
        }
    }

    @FXML
    private void onCancel() {
        parentDialog.closeDialog();
    }

    private void setStatus(String msg) {
        statusLabel.setText(msg);
    }

    private void handleError(Throwable ex) {
        ex.printStackTrace();
        Platform.runLater(() -> {
            statusLabel.setText("Error: " + ex.getMessage());
            // Optionally show alert?
        });
        if (errorHandler != null) {
            errorHandler.handleError(ex);
        }
    }
}
