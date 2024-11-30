package no.ntnu.gui.common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import no.ntnu.greenhouse.sensor.Sensor;
import no.ntnu.greenhouse.sensor.SensorReading;
import no.ntnu.gui.common.ComponentBuilder;
import no.ntnu.tools.Logger;

// TODO refactor, create classes for alot of the logic in this class. This class does too much.
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
    // TODO what happens here, why check .equals(The sensor is off.")?
    try {
      initialize(sensors.stream().map(Sensor::getReading).toList());
    } catch (IllegalStateException e) {
      if (e.getMessage().equals("The sensor is off.")) {
        Logger.error("Cannot add sensor to sensor pane becuase sensor is off");
      } else {
        throw e;
      }
    }
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
    // TODO what happens here, why check .equals(The sensor is off.")?
    try {
      update(sensors.stream().map(Sensor::getReading).toList());
    } catch (IllegalStateException e) {
      if (e.getMessage().equals("The sensor is off.")) {
        Logger.warn("Cannot update sensor becuase sensor is off");
      } else {
        throw e;
      }
    }
  }

  // TODO refactor this logic. Create a method above this method and such. This method should create a label and then another for images.
  // Alternatively, would not be necessary when we have implemented the component builder class.

  /**
   * Creates sensor label and remember it for future updates.
   *
   * @param sensor The sensor reading for creating the label.
   * @return A Node representing the sensor label.
   */
  private Node createAndRememberSensorLabel(SensorReading sensor) {
    ComponentBuilder builder = new ComponentBuilder();
    return builder.createComponent(sensor);
  }

//  /**
//   * Clear previous thumbnail and add a thumbnail to the UI.
//   *
//   * @param thumbnail The thumbnail to add.
//   */
//  private void addThumbnailToUI(Node thumbnail) {
//    if (thumbnail == null) {
//      throw new IllegalArgumentException("Thumbnail is null");
//    }
//    Platform.runLater(() -> {
//      contentBox.getChildren().clear();
//      contentBox.getChildren().addAll(thumbnail);
//    });
//  }
//
//  private Node createNumericSensorLabel(SensorReading sensor) {
//    SimpleStringProperty props = new SimpleStringProperty(generateSensorText(sensor));
//    sensorProps.add(props);
//    Label label = new Label();
//    label.textProperty().bind(props);
//    return label;
//  }
//
//  private String generateSensorText(SensorReading sensor) {
//    return sensor.getFormatted();
//  }

  /**
   * Updates sensor label in the GUI at the specified index.
   * If index already exists, it updates the existing component.
   * If index does not exist, it adds a new component for the sensor.
   *
   * @param sensor The sensor reading to update or add.
   * @param index  The index at which to update or add the sensor component.
   */
  private void updateSensorLabel(SensorReading sensor, int index) {
    // Checks if the index already exists in the contentBox
    if (index < contentBox.getChildren().size()) {
      // Updates the existing component
      Node existingNode = contentBox.getChildren().get(index);
      ComponentBuilder builder = new ComponentBuilder();
      Node updatedNode = builder.createComponent(sensor);

      // Replaces the existing component only if it has changed
      if (!existingNode.equals(updatedNode)) {
        Platform.runLater(() -> contentBox.getChildren().set(index, updatedNode));
      }
    } else {
      // Adds a new component for a new sensor
      Logger.info("Adding new sensor component at index: " + index);
      Platform.runLater(() -> contentBox.getChildren().add(createAndRememberSensorLabel(sensor)));
    }
  }
}