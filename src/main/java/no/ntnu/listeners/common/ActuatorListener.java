package no.ntnu.listeners.common;

import no.ntnu.greenhouse.Actuator;

/**
 * Listener for actuator state changes.
 * This could be used both on the sensor/actuator (greenhouse) side, as wall as
 * on the control panel side.
 */
public interface ActuatorListener {
  /**
   * An event that is fired every time an actuator changes state.
   *
   * @param nodeId   ID of the node on which this actuator is placed
   * @param actuator The actuator that has changed its state
   */
  void actuatorUpdated(int nodeId, Actuator actuator);
}
