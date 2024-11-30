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
import no.ntnu.constants.Resources;
import no.ntnu.greenhouse.sensor.ImageSensorReading;
import no.ntnu.greenhouse.sensor.SensorReading;
import no.ntnu.tools.Logger;


public class ImageSensorPane extends Pane {

  private ImageSensorReading sensorReading; 
  private ImageView thumbnail;
    
    public ImageSensorPane(ImageSensorReading sensorReading) {
        this.sensorReading = sensorReading;
    }

    public SensorReading getSensorReading() {
        return sensorReading;
    }

    public ImageView getThumbnail() {
        return thumbnail;
    }

    /**
   * Creates a JavaFX Node that displays a thumbnail image for the given sensor reading.
   * The thumbnail is clickable and opens a new window displaying the full image.
   *
   * @param sensor the sensor reading containing the image data
   * @return a VBox containing the image label and thumbnail
   */
  public Node createContent() {
    Logger.info("Creating image view for thumbnail");
    ImageSensorReading imageSensor = this.sensorReading;

    //TODO REFACTOR TO GET IMAGE FROM SENSOR
    // Generate image
    imageSensor.generateRandomImage(Resources.IMAGES.getPath());
    BufferedImage bufferedImage = imageSensor.getImage();
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
   * Displays the given image in a new window with a specified size.
   *
   * @param image the Image object to be displayed in full size
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

  

    // Add your methods and properties here
}