package no.ntnu.gui.greenhouse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Main GUI window for the greenhouse simulator.
 * Displays a simple interface with instructions, an image, and a copyright
 * notice.
 * This window provides users with a message on how to exit, an image,
 * and a copyright notice for the image. If the image is not found, a
 * placeholder is shown instead.
 */
public class MainGreenhouseGuiWindow extends Scene {
  public static final int WIDTH = 300;
  public static final int HEIGHT = 300;

  /**
   * Constructor for the main GUI window.
   * Initializes the scene with the specified width and height and applies the CSS
   * styles.
   */
  public MainGreenhouseGuiWindow() {
    super(createMainContent(), WIDTH, HEIGHT);
    // Load the CSS file and apply it to the scene
    getStylesheets().add(getClass().getResource("/css/greenhouse.css").toExternalForm());
  }

  /**
   * Creates the main content layout for the scene.
   * Includes an information label, the master image, and a copyright notice in a
   * vertical layout.
   *
   * @return The main container for the GUI content.
   */
  private static Parent createMainContent() {
    // VBox container holds all components vertically
    VBox container = new VBox(createInfoLabel(), createMasterImage(), createCopyrightNotice());
    container.getStyleClass().add("root"); // Adding a root style class for CSS styling
    return container;
  }

  /**
   * Creates an information label instructing the user on how to close the window.
   *
   * @return A label with instructions to close the window.
   */
  private static Label createInfoLabel() {
    Label label = new Label("Close this window to stop the whole simulation");
    label.getStyleClass().add("info-label"); // Apply CSS class for styling
    return label;
  }

  /**
   * Creates a copyright notice label for the image.
   *
   * @return A label indicating the source of the image.
   */
  private static Node createCopyrightNotice() {
    Label label = new Label("Image generated with Picsart");
    label.getStyleClass().add("copyright-label"); // Apply CSS class for styling
    return label;
  }

  /**
   * Creates an image node that displays the master image.
   * Attempts to load the image from a file. If unsuccessful, displays a
   * placeholder label.
   *
   * @return An ImageView if the image is successfully loaded; otherwise, a
   *         placeholder Label.
   */
  private static Node createMasterImage() {
    Node node;
    try {
      InputStream fileContent = new FileInputStream("resources/images/picsart_chuck.jpg");
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