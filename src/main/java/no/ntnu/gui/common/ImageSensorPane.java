package no.ntnu.gui.common;

import java.awt.image.BufferedImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import no.ntnu.greenhouse.sensor.ImageSensorReading;
import no.ntnu.greenhouse.sensor.SensorReading;
import no.ntnu.tools.Logger;

/**
 * The ImageSensorPane class extends the Pane class and is responsible for displaying
 * an image from an ImageSensorReading. It provides methods to retrieve the current
 * sensor reading and the thumbnail image view, as well as creating content nodes
 * for displaying the image and handling user interactions to view the full image.
 *
 * <p>Features include:
 * <ul>
 *   <li>Constructing an ImageSensorPane with a specified ImageSensorReading.</li>
 *   <li>Retrieving the current sensor reading.</li>
 *   <li>Retrieving the thumbnail image view.</li>
 *   <li>Creating a content node displaying an image thumbnail with a click listener
 *       to open the full image in a new window.</li>
 *   <li>Displaying the provided image in a new window with a specified size.</li>
 * </ul>
 */
public class ImageSensorPane extends Pane {

  private ImageSensorReading sensorReading; 
  private ImageView thumbnail;
    
    /**
     * Constructs an ImageSensorPane with the specified ImageSensorReading.
     *
     * @param sensorReading the ImageSensorReading to be used by this pane
     */
    public ImageSensorPane(ImageSensorReading sensorReading) {
        this.sensorReading = sensorReading;
    }

    /**
     * Retrieves the current sensor reading.
     *
     * @return the current SensorReading object.
     */
    public SensorReading getSensorReading() {
        return sensorReading;
    }

    /**
     * Retrieves the thumbnail image view.
     *
     * @return the ImageView representing the thumbnail.
     */
    public ImageView getThumbnail() {
        return thumbnail;
    }

  /**
   * Creates a content node displaying an image from the given ImageSensorReading.
   * If the image is null, a label indicating "No image found" is returned.
   * Otherwise, a thumbnail of the image is created and displayed with a click listener
   * to open the full image in a new window.
   *
   * @param sensorreading the ImageSensorReading containing the image to display
   * @return a Node containing the image thumbnail and label, or a label indicating no image found
   */
  public Node createContent(ImageSensorReading sensorreading) {
    
    BufferedImage bufferedImage = sensorreading.getImage();
    if (bufferedImage == null) {
        Logger.error("Buffered image is null");

        return new Label("No image found");
    }

    // Convert to JavaFX Image
    Image image = SwingFXUtils.toFXImage(bufferedImage, null);

    // Create a small thumbnail
    this.thumbnail = new ImageView(image);
    thumbnail.setFitWidth(100); // Set desired thumbnail width
    thumbnail.setPreserveRatio(true); // Maintain aspect ratio
    thumbnail.cursorProperty().setValue(javafx.scene.Cursor.HAND);

    // Add click listener to open a new window
    thumbnail.setOnMouseClicked(event -> showFullImage(image));

    Label imageLabel = new Label("Image: ");
    VBox imageNode = new VBox(5); // Add spacing between items
    imageNode.getChildren().addAll(imageLabel, thumbnail);

    return imageNode;
  }

  /**
   * Displays the provided image in a new window (Stage) with a specified size.
   *
   * @param image The Image object to be displayed in full size.
   */
  private void showFullImage(Image image) {
    // Create a new Stage (window)
    Stage fullImageStage = new Stage();
    fullImageStage.setTitle("Full Image");

    // Create an ImageView for the full image
    ImageView fullImageView = new ImageView(image);
    fullImageView.setPreserveRatio(true);
    fullImageView.setFitWidth(800); // Optional: Set window size
    fullImageView.setFitHeight(600);

    // Add the full image to the scene
    Scene scene = new Scene(new StackPane(fullImageView), 800, 600); // Optional: Set initial size
    fullImageStage.setScene(scene);

    // Show the stage
    fullImageStage.show();
  }
}