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
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import no.ntnu.greenhouse.sensors.ImageSensorReading;
import no.ntnu.greenhouse.sensors.NumericSensorReading;
import no.ntnu.greenhouse.sensors.Sensor;
import no.ntnu.greenhouse.sensors.SensorReading;
import no.ntnu.tools.Logger;

/**
 * A section of GUI displaying sensor data.
 */
public class SensorPane extends TitledPane {
  private final List<SimpleStringProperty> sensorProps = new ArrayList<>();
  private final VBox contentBox = new VBox();

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
        contentBox.getChildren().add(createAndRememberSensorLabel(sensor))
    );
    setContent(contentBox);
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

  private Node createImageSensorNode(SensorReading sensor){
    Logger.info("Creating image view");
      ImageSensorReading imageSensor = (ImageSensorReading) sensor;
      imageSensor.generateRandomImage("images/");
      BufferedImage bufferedImage = imageSensor.getImage();
      if (bufferedImage == null) {
        Logger.error("Buffered image is null");
        return new Label("No image found");
      }
      Image image = SwingFXUtils.toFXImage(bufferedImage, null);
      ImageView imageView = new ImageView(image);
    return imageView;
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
