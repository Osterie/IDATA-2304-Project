package no.ntnu.gui.greenhouse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * The main GUI window for greenhouse simulator.
 */
public class MainGreenhouseGuiWindow extends Scene {
  public static final int WIDTH = 300;
  public static final int HEIGHT = 300;

  public MainGreenhouseGuiWindow() {
    super(createMainContent(), WIDTH, HEIGHT);
    // Load the CSS file and apply it to the scene
    getStylesheets().add(getClass().getResource("/css/greenhouse.css").toExternalForm());
  }

  private static Parent createMainContent() {
    VBox container = new VBox(createInfoLabel(), createMasterImage(), createCopyrightNotice());
    container.getStyleClass().add("root"); // Adding a root style class
    return container;
  }

  private static Label createInfoLabel() {
    Label label = new Label("Close this window to stop the whole simulation");
    label.getStyleClass().add("info-label"); // Apply CSS class
    return label;
  }

  private static Node createCopyrightNotice() {
    Label label = new Label("Image generated with Picsart");
    label.getStyleClass().add("copyright-label"); // Apply CSS class
    return label;
  }

  private static Node createMasterImage() {
    Node node;
    try {
      // TODO change stuff. What if the image is not found?
      InputStream fileContent = new FileInputStream("images/picsart_chuck.jpg");
      ImageView imageView = new ImageView();
      imageView.setImage(new Image(fileContent));
      imageView.getStyleClass().add("image-view");
      imageView.setFitWidth(300);
      imageView.setPreserveRatio(true);

      node = imageView;
    } catch (FileNotFoundException e) {
      node = new Label("Could not find image file: " + e.getMessage());
    }
    return node;
  }

}