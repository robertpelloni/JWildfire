package org.jwildfire.swing;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jwildfire.base.Tools;

import java.awt.Desktop;
import java.net.URI;
import java.util.Random;

public class WelcomeController {

    @FXML
    private ImageView logoImageView;

    @FXML
    private ImageView mainImageView;

    @FXML
    private VBox rootPane;

    private final String[] imageFilenames = { "bronze_bubbles.jpg", "smoky_dreams.jpg", "watchers2.jpg", "woven.jpg" };

    @FXML
    public void initialize() {
        loadLogo();
        loadMainImage();
    }

    private void loadLogo() {
        try {
            String logoName = Tools.SPECIAL_VERSION ? "logo_special.png" : "logo.png";
            Image image = new Image(getClass().getResourceAsStream("welcomescreen/" + logoName));
            logoImageView.setImage(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMainImage() {
        try {
            String imageName = imageFilenames[new Random().nextInt(imageFilenames.length)];
            Image image = new Image(getClass().getResourceAsStream("welcomescreen/" + imageName));
            mainImageView.setImage(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onLogoClick() {
        browse("http://jwildfire.org/");
    }

    @FXML
    private void onMainImageClick() {
        closeWindow();
    }

    private void closeWindow() {
        if (swingFrame != null) {
            javax.swing.SwingUtilities.invokeLater(() -> swingFrame.setVisible(false));
        } else if (rootPane.getScene() != null && rootPane.getScene().getWindow() instanceof Stage) {
            ((Stage) rootPane.getScene().getWindow()).close();
        }
    }
    
    // Injecting the frame instance would be cleaner, but for now I'll use a static helper or look it up.
    // Actually, WelcomeFrame extends JFrame. 
    // I can modify WelcomeFrame to set itself into the controller? 
    // Or I can just leave the close logic to the Swing wrapper for now?
    // The original code: panel_2.addMouseListener(... setVisible(false) ...)
    
    // Let's add a setter for the Swing frame
    private javax.swing.JFrame swingFrame;
    public void setSwingFrame(javax.swing.JFrame frame) {
        this.swingFrame = frame;
    }
    
    @FXML
    private void onMainPage() { browse("https://jwildfire.overwhale.com/"); }
    
    @FXML
    private void onForum() { browse("https://jwildfire-forum.overwhale.com"); }
    
    @FXML
    private void onDownloads() { browse("https://blog.overwhale.com/?page_id=351"); }
    
    @FXML
    private void onVariations() { browse("https://1drv.ms/x/s!AhabogcLehGXjHG9QNEcSPkfkkrq"); }
    
    @FXML
    private void onDonate() { browse("https://blog.overwhale.com/?page_id=1401"); }
    
    @FXML
    private void onCommunity() { browse("http://www.facebook.com/groups/JWildfireOpenGroup/"); }
    
    @FXML
    private void onDocumentation() { browse("http://www.andreas-maschke.com/?page_id=875"); }

    private void browse(String url) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    // Helper to close if set
    public void closeIfSwing() {
        if (swingFrame != null) {
            javax.swing.SwingUtilities.invokeLater(() -> swingFrame.setVisible(false));
        }
    }
}
