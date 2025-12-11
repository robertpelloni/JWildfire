package org.jwildfire.base;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class JavaFXIntegration extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        URL resource = getClass().getResource("sample.fxml");
        if (resource == null) throw new RuntimeException("sample.fxml not found");
        Parent root = FXMLLoader.load(resource);
        primaryStage.setTitle("JWildfire JavaFX Integration");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }
    public static void verify() { System.out.println("Launching JavaFX Application..."); }
    public static void main(String[] args) { launch(args); }
}
