package no.ntnu.listeners.controlpanel;

import java.util.List;
import no.ntnu.controlpanel.SensorActuatorNodeInfo;
import no.ntnu.greenhouse.SensorReading;

/**
 * Listener of events happening "inside a greenhouse", such as a node appearing, disappearing,
 * new sensor readings, etc.
 * While the name can be misleading, this interface will actually be usable on the
 * control-panel side, not the greenhouse side.
 * The idea is that a control panel can get events when some new information is received
 * about some changes in a greenhouse.
 */
public interface GreenhouseEventListener {
  /**
   * This event is fired when a new node is added to the greenhouse.
   *
   * @param nodeInfo Information about the added node
   */
  void onNodeAdded(SensorActuatorNodeInfo nodeInfo);

  /**
   * This event is fired when a node is removed from the greenhouse.
   *
   * @param nodeId ID of the node which has disappeared (removed)
   */
  void onNodeRemoved(int nodeId);

  /**
   * This event is fired when new sensor data is received from a node.
   *
   * @param nodeId  ID of the node
   * @param sensors List of all current sensor values
   */
  void onSensorData(int nodeId, List<SensorReading> sensors);

  /**
   * This event is fired when an actuator changes state.
   *
   * @param nodeId ID of the node to which the actuator is attached
   * @param actuatorId ID of the actuator
   * @param isOn  When true, actuator is on; off when false.
   */
  void onActuatorStateChanged(int nodeId, int actuatorId, boolean isOn);
}
