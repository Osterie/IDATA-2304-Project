package no.ntnu.controlpanel;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import no.ntnu.greenhouse.actuator.Actuator;
import no.ntnu.greenhouse.sensor.SensorReading;
import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.listeners.common.CommunicationChannelListener;
import no.ntnu.listeners.controlpanel.GreenhouseEventListener;
import no.ntnu.tools.Logger;

/**
 * The central logic of a control panel node. It uses a communication channel to send commands
 * and receive events. It supports listeners who will be notified on changes (for example, a new
 * node is added to the network, or a new sensor reading is received).
 *
 * Note: This class demonstrates proper separation of logic from the GUI. In larger projects,
 * this class may handle complex logic such as storing events in a database, performing checks,
 * or sending notifications, which should never be placed inside GUI classes.
 */
public class ControlPanelLogic implements GreenhouseEventListener, ActuatorListener,
        CommunicationChannelListener  {

  private final List<GreenhouseEventListener> listeners = new LinkedList<>();

  private CommunicationChannel communicationChannel;
  private CommunicationChannelListener communicationChannelListener;

  /**
   * Set the channel over which control commands will be sent to sensor/actuator nodes.
   *
   * @param communicationChannel The communication channel, the event sender.
   */
  public void setCommunicationChannel(CommunicationChannel communicationChannel) {
    this.communicationChannel = communicationChannel;
  }

  /**
   * Set listener which will get notified when the communication channel is closed.
   *
   * @param listener The listener.
   */
  public void setCommunicationChannelListener(CommunicationChannelListener listener) {
    this.communicationChannelListener = listener;
  }

  /**
   * Add a greenhouse event listener.
   *
   * @param listener The listener who will be notified on all events.
   */
  public void addListener(GreenhouseEventListener listener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  /**
   * Reset the internal state of the control panel logic.
   * This clears all listeners and prepares the logic for a fresh start.
   */
  public void resetState() {
    Logger.info("Resetting ControlPanelLogic state...");
    listeners.clear();

    // Clear the communication channel reference
    communicationChannel = null;
    // Clear the communication channel listener
    communicationChannelListener = null;
  }

  /**
   * Advertise new sensor readings.
   * Notifies the control panel logic of new sensor readings after a specified delay.
   * 
   * @param specification Specification of the readings in the following format:
   *                      [nodeID]
   *                      semicolon
   *                      [sensor_type_1] equals [sensor_value_1] space [unit_1]
   *                      comma
   *                      ...
   *                      comma
   *                      [sensor_type_N] equals [sensor_value_N] space [unit_N]
   * @param delay         Delay in seconds
   */
  public void advertiseSensorData(List<SensorReading> sensors, int nodeId, int delay) {
    ControlPanelLogic self = this;

    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        self.onSensorData(nodeId, sensors);
      }
    }, delay * 1000L);
  }

  /**
   * Advertise that a node is removed.
   * Notifies the control panel logic that a node has been removed after a specified delay.
   *
   * @param nodeId ID of the removed node
   * @param delay  Delay in seconds
   */
  public void advertiseRemovedNode(int nodeId, int delay) {

    ControlPanelLogic self = this;

    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        self.onNodeRemoved(nodeId);
      }
    }, delay * 1000L);
  }

    /**
   * Advertise that an actuator has changed its state.
   * Notifies the control panel logic of an actuator state change after a specified delay.
   *
   * @param nodeId     ID of the node to which the actuator is attached
   * @param actuatorId ID of the actuator
   * @param on         When true, actuator is on; off when false
   * @param delay      The delay in seconds after which the advertisement will be generated
   */
  public void advertiseActuatorState(int nodeId, int actuatorId, boolean on, int delay) {

    ControlPanelLogic self = this;

    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        self.onActuatorStateChanged(nodeId, actuatorId, on);
      }
    }, delay * 1000L);
  }

  /**
   * Add a node based on the response.
   * Creates a new node and schedules its addition to the control panel logic.
   *
   * @param response The response containing node information
   */
  public void addNode(SensorActuatorNodeInfo nodeInfo) {

    ControlPanelLogic self = this;

    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        self.onNodeAdded(nodeInfo);
      }
    }, 5 * 1000L);
  }

  /**
   * Notifies all listeners that a new node has been added.
   * 
   * @param nodeInfo The information about the new node to advertise.
   */
  @Override
  public void onNodeAdded(SensorActuatorNodeInfo nodeInfo) {
    listeners.forEach(listener -> listener.onNodeAdded(nodeInfo));
  }

  /**
   * Notifies all listeners that a node has been removed.
   * 
   * @param nodeId The ID of the removed node.
   */
  @Override
  public void onNodeRemoved(int nodeId) {
    listeners.forEach(listener -> listener.onNodeRemoved(nodeId));
  }

  /**
   * Notifies all listeners that sensor data has been received.
   * 
   * @param nodeId  The ID of the node that sent the data.
   * @param sensors The list of sensor readings.
   */
  @Override
  public void onSensorData(int nodeId, List<SensorReading> sensors) {
    listeners.forEach(listener -> listener.onSensorData(nodeId, sensors));
  }

  /**
   * Notifies all listeners that an actuator state has changed.
   * 
   * @param nodeId    The ID of the node to which the actuator is attached.
   * @param actuatorId The ID of the actuator.
   * @param isOn      True if the actuator is on; false if it is off.
   */
  @Override
  public void onActuatorStateChanged(int nodeId, int actuatorId, boolean isOn) {
    listeners.forEach(listener -> listener.onActuatorStateChanged(nodeId, actuatorId, isOn));
  }

  /**
   * Notifies all listeners that an actuator has been updated.
   * 
   * @param nodeId    The ID of the node to which the actuator is attached.
   * @param actuator  The updated actuator.
   */
  @Override
  public void actuatorUpdated(int nodeId, Actuator actuator) {
    if (communicationChannel != null) {
      communicationChannel.sendActuatorChange(nodeId, actuator.getId(), actuator.isOn());
    }
    listeners.forEach(listener ->
            listener.onActuatorStateChanged(nodeId, actuator.getId(), actuator.isOn())
    );
  }

  /**
   * Notifies all listeners that the communication channel has been closed.
   */
  @Override
  public void onCommunicationChannelClosed() {
    Logger.info("Communication closed, updating logic...");
    if (communicationChannelListener != null) {
      communicationChannelListener.onCommunicationChannelClosed();
    }
  }
}