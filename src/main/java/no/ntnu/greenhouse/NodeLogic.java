package no.ntnu.greenhouse;

import java.util.ArrayList;
import java.util.List;

import no.ntnu.greenhouse.actuator.Actuator;
import no.ntnu.greenhouse.sensor.Sensor;
import no.ntnu.greenhouse.sensor.SensorReading;
import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.messages.Delimiters;

/**
 * The logic of a node in the greenhouse.
 */
public class NodeLogic implements ActuatorListener {
  private final SensorActuatorNode node;
  private final List<ActuatorListener> actuatorListeners;

  /**
   * Create new node logic and adds listener to the node.
   *
   * @param node The node to create logic for.
   */
  public NodeLogic(SensorActuatorNode node) {
    this.node = node;
    this.node.addActuatorListener(this);
    this.actuatorListeners = new ArrayList<>();
  }

  /**
   * Returns the node.
   *
   * @return The node.
   */
  public SensorActuatorNode getNode(){
    return this.node;
  }

  /**
   * Returns the ID of the node.
   *
   * @return The ID of the node.
   */
  public int getId() {
    return this.node.getId();
  }

  /**
   * Returns the sensor data of the node.
   *
   * @return The sensor data of the node.
   */
  public String getSensorData(){

    List<Sensor> sensors = this.node.getSensors();
    String sensorData = this.node.getId() + Delimiters.BODY_FIELD.getValue();

    for (Sensor sensor : sensors) {
      try {
        SensorReading reading = sensor.getReading();
        String sensorType = sensor.getDataFormat();
        String formattedSensorReading = reading.getFormatted();
        sensorData += sensorType + ":" + formattedSensorReading + Delimiters.BODY_SENSOR_SEPARATOR.getValue();
      } catch (IllegalStateException e) {
        sensorData += sensor.getDataFormat() + ": =  NoData";
      }
    }
    // Remove the last comma
    sensorData = sensorData.substring(0, sensorData.length() - 1);

    return sensorData;
  }

  /**
   * Add an actuator listener.
   *
   * @param listener The listener to add.
   */
  public void addActuatorListener(ActuatorListener listener) {
    this.actuatorListeners.add(listener);
  }

  /**
   * Handle an actuator update.
   *
   * @param nodeId The ID of the node to which the actuator is attached.
   * @param actuator The updated actuator.
   */
  @Override
  public void actuatorUpdated(int nodeId, Actuator actuator) {
    for (ActuatorListener listener : actuatorListeners) {
      listener.actuatorUpdated(nodeId, actuator);
    }
  }
}
