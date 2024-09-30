package no.ntnu.listeners.greenhouse;

import no.ntnu.greenhouse.SensorActuatorNode;

/**
 * Listener which receives node lifecycle events.
 * This should be used on the sensor/actuator node part, where the real node object is available.
 * This event will (probably) not be useful on the control panel side.
 */
public interface NodeStateListener {
  /**
   * This event is fired when a sensor/actuator node has finished the starting procedure and
   * has entered the "ready" state.
   *
   * @param node the node which is ready now
   */
  void onNodeReady(SensorActuatorNode node);

  /**
   * This event is fired when a sensor/actuator node has stopped (shut down_.
   *
   * @param node The node which is stopped
   */
  void onNodeStopped(SensorActuatorNode node);
}
