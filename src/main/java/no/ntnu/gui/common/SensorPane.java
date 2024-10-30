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
import no.ntnu.greenhouse.NumericSensor;
import no.ntnu.greenhouse.sensorreading.SensorReading;
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

  /**
   * Create a sensor pane with images.
   *
   * @param sensors The sensor data to be displayed on the pane.
   * @param imageBuffer The images associated with the sensors.
   */
  public SensorPane(Iterable<SensorReading> sensors, Iterable<BufferedImage> imageBuffer) {
    super();
    initializeWithImages(sensors, imageBuffer);
  }

  private void initialize(Iterable<SensorReading> sensors) {
    setText("Sensors");
    sensors.forEach(sensor ->
        contentBox.getChildren().add(createAndRememberSensorNode(sensor, null))
    );
    setContent(contentBox);
  }

  private void initializeWithImages(Iterable<SensorReading> sensors, Iterable<BufferedImage> imageBuffer) {
    setText("Sensors with Images");
    List<BufferedImage> images = new ArrayList<>();
    imageBuffer.forEach(images::add); // Collecting images in a list to match sensor indices
    int index = 0;
    for (SensorReading sensor : sensors) {
      BufferedImage img = index < images.size() ? images.get(index) : null;
      contentBox.getChildren().add(createAndRememberSensorNode(sensor, img));
      index++;
    }
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
  public SensorPane(List<NumericSensor> sensors) {
    initialize(sensors.stream().map(NumericSensor::getReading).toList());
  }

  /**
   * Update the GUI according to the changes in sensor data and images.
   *
   * @param sensors The sensor data that has been updated
   * @param images The updated sensor images
   */
  public void update(Iterable<SensorReading> sensors, Iterable<BufferedImage> images) {
    int index = 0;
    List<BufferedImage> imageList = new ArrayList<>();
    images.forEach(imageList::add);
    for (SensorReading sensor : sensors) {
      BufferedImage img = index < imageList.size() ? imageList.get(index) : null;
      updateSensorLabel(sensor, index++, img);
    }
  }

  /**
   * Update the GUI according to the changes in sensor data.
   * Wrapper for the other method with SensorReading-iterable parameter
   *
   * @param sensors The sensor data that has been updated
   */
  public void update(List<NumericSensor> sensors) {
    update(sensors.stream().map(NumericSensor::getReading).toList(), null);
  }

  private Node createAndRememberSensorNode(SensorReading sensor, BufferedImage image) {
    SimpleStringProperty props = new SimpleStringProperty(generateSensorText(sensor));
    sensorProps.add(props);
    Label label = new Label();
    label.textProperty().bind(props);

    if (image != null) {
      ImageView imageView = new ImageView(convertToFxImage(image));
      VBox container = new VBox(label, imageView);
      return container;
    }
    
    return label;
  }

  private String generateSensorText(SensorReading sensor) {
    return sensor.getType() + ": " + sensor.getFormatted();
  }

  private void updateSensorLabel(SensorReading sensor, int index, BufferedImage image) {
    if (sensorProps.size() > index) {
      SimpleStringProperty props = sensorProps.get(index);
      Platform.runLater(() -> {
        props.set(generateSensorText(sensor));
        if (image != null) {
          ImageView imageView = new ImageView(convertToFxImage(image));
          contentBox.getChildren().set(index, new VBox(new Label(props.get()), imageView));
        }
      });
    } else {
      Logger.info("Adding sensor[" + index + "]");
      Platform.runLater(() -> contentBox.getChildren().add(createAndRememberSensorNode(sensor, image)));
    }
  }

  /**
   * Convert a BufferedImage to a JavaFX Image.
   * 
   * @param bufferedImage The BufferedImage to convert.
   * @return The converted Image.
   */
  private Image convertToFxImage(BufferedImage bufferedImage) {
    return SwingFXUtils.toFXImage(bufferedImage, null);
  }
}
