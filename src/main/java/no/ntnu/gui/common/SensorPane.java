package no.ntnu.gui.common;

import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import no.ntnu.greenhouse.sensors.ImageSensorReading;
import no.ntnu.greenhouse.sensors.NumericSensorReading;
import no.ntnu.greenhouse.sensors.Sensor;
import no.ntnu.greenhouse.sensors.SensorReading;
import no.ntnu.tools.Logger;

// TODO refactor, create classes for alot of the logic in this class. This class does too much.
/**
 * A section of GUI displaying sensor data.
 */
public class SensorPane extends TitledPane {
  private final List<SimpleStringProperty> sensorProps = new ArrayList<>();
  private final VBox contentBox = new VBox();
  private final List<Node> thumbnailList = new LinkedList<>(); // TODO why?

  /**
   * Create a sensor pane.
   *
   * @param sensors The sensor data to be displayed on the pane.
   */
  public SensorPane(Iterable<SensorReading> sensors) {
    super();
    initialize(sensors);
  }

  private void initialize(Iterable<SensorReading> sensors) {
    setText("Sensors");
    sensors.forEach(sensor ->
        this.contentBox.getChildren().add(createAndRememberSensorLabel(sensor))
    );
    setContent(this.contentBox);
  }

  /**
   * Create an empty sensor pane, without any data.
   */
  public SensorPane() {
    initialize(new LinkedList<>());
  }

  /**
   * Create a sensor pane.
   * Wrapper for the other constructor with SensorReading-iterable parameter
   *
   * @param sensors The sensor data to be displayed on the pane.
   */
  public SensorPane(List<Sensor> sensors) {
    initialize(sensors.stream().map(Sensor::getReading).toList());
  }

  /**
   * Update the GUI according to the changes in sensor data.
   *
   * @param sensors The sensor data that has been updated
   */
  public void update(Iterable<SensorReading> sensors) {
    int index = 0;
    for (SensorReading sensor : sensors) {
      updateSensorLabel(sensor, index++);
    }
  }

  /**
   * Update the GUI according to the changes in sensor data.
   * Wrapper for the other method with SensorReading-iterable parameter
   *
   * @param sensors The sensor data that has been updated
   */
  public void update(List<Sensor> sensors) {
    update(sensors.stream().map(Sensor::getReading).toList());
  }

  // TODO refactor this logic. Create a method above this method and such. This method should create a label and then another for images.
  // Alternatively, would not be necessary when we have implemented the component builder class.
  private Node createAndRememberSensorLabel(SensorReading sensor) {
    Node nodeToReturn;
    
    if (sensor instanceof NumericSensorReading) {
      nodeToReturn = createNumericSensorLabel(sensor);
    } else if (sensor instanceof ImageSensorReading) {
      nodeToReturn = createImageSensorNode(sensor);
    } else {
      return new Label("Unknown sensor type: " + sensor.getClass());
      // throw new IllegalArgumentException("Unknown sensor type: " + sensor.getClass());
    }
    return nodeToReturn;
  }

  private Node createNumericSensorLabel(SensorReading sensor){
    SimpleStringProperty props = new SimpleStringProperty(generateSensorText(sensor));
      sensorProps.add(props);
      Label label = new Label();
      label.textProperty().bind(props);
    return label;
  }

  // TODO wtf
  private Node createImageSensorNode(SensorReading sensor){
    Logger.info("Creating image view for thumbnail");
    ImageSensorReading imageSensor = (ImageSensorReading) sensor;

    // Generate image
    imageSensor.generateRandomImage("images/");
    BufferedImage bufferedImage = imageSensor.getImage();
    if (bufferedImage == null) {
        Logger.error("Buffered image is null");
        return new Label("No image found");
    }

    // Convert to JavaFX Image
    Image image = SwingFXUtils.toFXImage(bufferedImage, null);

    // Create a small thumbnail
    ImageView thumbnail = new ImageView(image);
    thumbnail.setFitWidth(100); // Set desired thumbnail width
    thumbnail.setPreserveRatio(true); // Maintain aspect ratio
    thumbnail.cursorProperty().setValue(javafx.scene.Cursor.HAND);

    // Add click listener to open a new window
    thumbnail.setOnMouseClicked(event -> showFullImage(image));

    // Add thumbnail to the list and update the UI
    addThumbnailToUI(thumbnail);

    return thumbnail;
  }

  // TODO wtf?
  private void addThumbnailToUI(Node thumbnail) {
    // Add the new thumbnail to the list
    thumbnailList.add(thumbnail);

    // If there are more than 3 thumbnails, remove the oldest
    if (thumbnailList.size() > 3) {
        thumbnailList.remove(0);
    }

    // Update the VBox to show only the thumbnails in the list
    Platform.runLater(() -> {
        contentBox.getChildren().clear();
        contentBox.getChildren().addAll(thumbnailList);
    });
  }

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

  private String generateSensorText(SensorReading sensor) {
    return sensor.getFormatted();
  }

  private void updateSensorLabel(SensorReading sensor, int index) {
    if (sensorProps.size() > index) {
      SimpleStringProperty props = sensorProps.get(index);
      Platform.runLater(() -> props.set(generateSensorText(sensor)));
    } else {
      Logger.info("Adding sensor[" + index + "]");
      Platform.runLater(() -> contentBox.getChildren().add(createAndRememberSensorLabel(sensor)));
    }
  }
}