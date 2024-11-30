package no.ntnu.greenhouse.actuator;

import java.util.HashMap;
import java.util.Map;

import no.ntnu.greenhouse.SensorActuatorNode;
import no.ntnu.greenhouse.sensor.SensorType;
import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.tools.Logger;

/**
 * An actuator that can change the environment in a way. The actuator will make
 * impact on the
 * sensors attached to this same node.
 */
public class Actuator {
  private static int nextId = 0;
  private final String type;
  private final int nodeId;
  private final int id;
  private Map<SensorType, Double> impacts = new HashMap<>();
  private final String TURN_OFF_TEXT;
  private final String TURN_ON_TEXT;

  private ActuatorListener listener;

  private boolean on;

  /**
   * Create an actuator. An ID will be auto-generated.
   *
   * @param type   The type of the actuator.
   * @param nodeId ID of the node to which this actuator is connected.
   */
  public Actuator(String type, int nodeId, String TURN_ON_TEXT, String TURN_OFF_TEXT) {
    if (type == null) {
      throw new IllegalArgumentException("Type cannot be null or empty");
    }
    this.type = type;
    this.nodeId = nodeId;
    this.on = false;
    this.id = generateUniqueId();
    this.TURN_ON_TEXT = TURN_ON_TEXT;
    this.TURN_OFF_TEXT = TURN_OFF_TEXT;
  }

  /**
   * Create an actuator.
   *
   * @param id     The desired ID of the node.
   * @param type   The type of the actuator.
   * @param nodeId ID of the node to which this actuator is connected.
   */
  public Actuator(int id, String type, int nodeId, String TURN_OFF_TEXT, String TURN_ON_TEXT) {
    if (type == null) {
      throw new IllegalArgumentException("Type cannot be null or empty");
    }
    this.type = type;
    this.nodeId = nodeId;
    this.on = false;
    this.id = id;
    this.TURN_OFF_TEXT = TURN_OFF_TEXT;
    this.TURN_ON_TEXT = TURN_ON_TEXT;
  }

  /**
   * Generate a unique ID for the actuator.
   * 
   * @return A unique ID for the actuator.
   */
  private static int generateUniqueId() {
    return nextId++;
  }

  /**
   * Set the listener which will be notified when actuator state changes.
   *
   * @param listener The listener of state change events
   */
  public void setListener(ActuatorListener listener) {
    this.listener = listener;
  }

  /**
   * Register the impact of this actuator when active.
   *
   * @param sensorType     Which type of sensor readings will be impacted.
   *                       Example: "temperature"
   * @param diffWhenActive What will be the introduced difference in the sensor
   *                       reading when
   *                       the actuator is active. For example, if this value is
   *                       2.0 and the
   *                       sensorType is "temperature", this means that
   *                       "activating this actuator
   *                       will increase the readings of temperature sensors
   *                       attached to the
   *                       same node by +2 degrees".
   */
  public void setImpact(SensorType sensorType, double diffWhenActive) {
    impacts.put(sensorType, diffWhenActive);
  }

  /**
   * Get the type of the actuator.
   *
   * @return The type of the actuator.
   */
  public String getType() {
    return type;
  }

  /**
   * Get the turn off text.
   * 
   * @return The text that should be displayed when the actuator is turned off.
   */
  public String getTurnOffText() {
    return this.TURN_OFF_TEXT;
  }

  /**
   * Get the turn on text.
   * 
   * @return The text that should be displayed when the actuator is turned on.
   */
  public String getTurnOnText() {
    return this.TURN_ON_TEXT;
  }

  /**
   * Create a clone of this actuator.
   *
   * @return A clone of this actuator, where all the fields are the same
   */
  public Actuator createClone() {
    Actuator a = new Actuator(type, nodeId, TURN_OFF_TEXT, TURN_ON_TEXT);
    // Note - we pass a reference to the same map! This should not be problem, as
    // long as we
    // don't modify the impacts AFTER creating the template
    a.impacts = impacts;
    return a;
  }

  /**
   * Toggle the actuator - if it was off, not it will be ON, and vice versa.
   */
  public void toggle() {
    this.on = !this.on;
    notifyChanges();
  }

  /**
   * Notify the listener about the change in the actuator state.
   */
  private void notifyChanges() {
    Logger.success("Actuator " + id + " on node " + nodeId + " is now " + (this.on ? "ON" : "OFF"));
    if (this.listener != null) {
      Logger.success("Notifying listener about actuator change");
      this.listener.actuatorUpdated(this.nodeId, this);
    }
  }

  /**
   * Check whether the actuator is active (ON), or inactive (OFF).
   *
   * @return True when the actuator is ON, false if it is OFF
   */
  public boolean isOn() {
    return on;
  }

  /**
   * Apply impact of this actuator to all sensors of one specific sensor node.
   *
   * @param node The sensor node to be affected by this actuator.
   */
  public void applyImpact(SensorActuatorNode node) {
    if (node == null) {
      throw new IllegalArgumentException("Actuator node cannot be null");
    }
    for (Map.Entry<SensorType, Double> impactEntry : impacts.entrySet()) {
      SensorType sensorType = impactEntry.getKey();
      double impact = impactEntry.getValue();
      if (!on) {
        impact = -impact;
      }
      node.applyActuatorImpact(sensorType, impact);
    }

  }

  // TODO change to fit protocol
  /**
   * Get a string representation of the actuator.
   * 
   * @return A string representation of the actuator
   */
  @Override
  public String toString() {
    return "Actuator{"
        + "type='" + type + '\''
        + ", on=" + on
        + '}';
  }

  /**
   * Turn on the actuator.
   */
  public void turnOn(boolean notifyChanges) {
    if (!on) {
      on = true;
      if (notifyChanges){
        this.notifyChanges();
      }
    }
  }

  /**
   * Turn off the actuator.
   */
  public void turnOff(boolean notifyChanges) {
    if (on) {
      on = false;
      if (notifyChanges){
        this.notifyChanges();
      }
    }
  }

  /**
   * Get the ID of the actuator.
   *
   * @return An ID which is guaranteed to be unique at a node level, not
   *         necessarily unique at
   *         the whole greenhouse-network level.
   */
  public int getId() {
    return id;
  }

  public int getNodeId() {
    return nodeId;
  }

  /**
   * Set the actuator to the desired state.
   *
   * @param on Turn on when true, turn off when false
   */
  public void set(boolean on, boolean notifyChanges) {
    if (on) {
      turnOn(notifyChanges);
    } else {
      turnOff(notifyChanges);
    }
  }
}
