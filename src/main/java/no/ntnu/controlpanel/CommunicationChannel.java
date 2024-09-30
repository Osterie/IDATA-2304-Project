package no.ntnu.controlpanel;

/**
 * A communication channel for disseminating control commands to the sensor nodes
 * (sending commands to the server) and receiving notifications about events.
 * Your socket class on the control panel side should implement this.
 */
public interface CommunicationChannel {
  /**
   * Request that state of an actuator is changed.
   *
   * @param nodeId     ID of the node to which the actuator is attached
   * @param actuatorId Node-wide unique ID of the actuator
   * @param isOn       When true, actuator must be turned on; off when false.
   */
  void sendActuatorChange(int nodeId, int actuatorId, boolean isOn);

  /**
   * Open the communication channel.
   *
   * @return True when the communication channel is successfully opened, false on error
   */
  boolean open();
}
