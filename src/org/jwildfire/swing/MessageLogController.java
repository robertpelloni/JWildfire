package org.jwildfire.swing;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.text.SimpleDateFormat;

public class MessageLogController implements MessageLogEventObserver {

    @FXML
    private TextArea logTextArea;

    @FXML
    public void initialize() {
        MessageLogMapHolder.create().registerObserver(this);
    }

    @Override
    public void update(ILoggingEvent event) {
        StringBuilder builder = new StringBuilder();
        builder.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(event.getTimeStamp()))
               .append(" ").append(event.getLevel()).append("\n");
        builder.append("    ").append(event.getLoggerName()).append(": ").append(event.getFormattedMessage()).append("\n");
        
        if (event.getCallerData() != null) {
            for (StackTraceElement element : event.getCallerData()) {
                builder.append("        ").append(element.toString()).append("\n");
            }
        }
        builder.append("\n\n");

        Platform.runLater(() -> {
            if (logTextArea != null) {
                // Prepend to top
                logTextArea.insertText(0, builder.toString());
                logTextArea.positionCaret(0);
            }
        });
    }
}
