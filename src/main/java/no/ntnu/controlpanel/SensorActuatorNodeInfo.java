package no.ntnu.controlpanel;

import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.ActuatorCollection;

/**
 * Contains information about one sensor/actuator node. This is NOT the node itself, rather
 * an information that can be used on the control-panel side to represent the node.
 */
public class SensorActuatorNodeInfo {

  private final int nodeId;
  private final ActuatorCollection actuators = new ActuatorCollection();

  public SensorActuatorNodeInfo(int nodeId) {
    this.nodeId = nodeId;
  }

  public void addActuator(Actuator actuator) {
    actuators.add(actuator);
  }

  /**
   * Get ID of the node.
   *
   * @return The unique ID of the node
   */
  public int getId() {
    return nodeId;
  }

  /**
   * Get all the actuators of the sensor/actuator node.
   *
   * @return The actuator collection
   */
  public ActuatorCollection getActuators() {
    return actuators;
  }

  /**
   * Get an actuator of given type, with given index.
   *
   * @param actuatorId ID of the actuator
   * @return The actuator or null if none found
   */
  public Actuator getActuator(int actuatorId) {
    return actuators.get(actuatorId);
  }
}
