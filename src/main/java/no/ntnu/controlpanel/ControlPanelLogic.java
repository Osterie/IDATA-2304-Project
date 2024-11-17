package no.ntnu.controlpanel;

import java.util.LinkedList;
import java.util.List;

import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.sensors.SensorReading;
import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.listeners.common.CommunicationChannelListener;
import no.ntnu.listeners.controlpanel.GreenhouseEventListener;
import no.ntnu.tools.Logger;

/**
 * The central logic of a control panel node. It uses a communication channel to send commands
 * and receive events. It supports listeners who will be notified on changes (for example, a new
 * node is added to the network, or a new sensor reading is received).
 * Note: this class may look like unnecessary forwarding of events to the GUI. In real projects
 * (read: "big projects") this logic class may do some "real processing" - such as storing events
 * in a database, doing some checks, sending emails, notifications, etc. Such things should never
 * be placed inside a GUI class (JavaFX classes). Therefore, we use proper structure here, even
 * though you may have no real control-panel logic in your projects.
 */
public class ControlPanelLogic implements GreenhouseEventListener, ActuatorListener,
    CommunicationChannelListener  {

  private final List<GreenhouseEventListener> listeners = new LinkedList<>();

  private CommunicationChannel communicationChannel;
  private CommunicationChannelListener communicationChannelListener;

  /**
   * Set the channel over which control commands will be sent to sensor/actuator nodes.
   *
   * @param communicationChannel The communication channel, the event sender
   */
  public void setCommunicationChannel(CommunicationChannel communicationChannel) {
    this.communicationChannel = communicationChannel;
  }

  
  /**
   * Set listener which will get notified when communication channel is closed.
   *
   * @param listener The listener
   */
  public void setCommunicationChannelListener(CommunicationChannelListener listener) {
    this.communicationChannelListener = listener;
  }

  /**
   * Add an event listener.
   *
   * @param listener The listener who will be notified on all events
   */
  public void addListener(GreenhouseEventListener listener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  @Override
  public void onNodeAdded(SensorActuatorNodeInfo nodeInfo) {
    try {
      listeners.forEach(listener -> listener.onNodeAdded(nodeInfo));
    } catch (Exception e) {
      Logger.error("Error notifying listener about node addition: " + e.getMessage());
    }
  }

  @Override
  public void onNodeRemoved(int nodeId) {
    try {
      listeners.forEach(listener -> listener.onNodeRemoved(nodeId));
    } catch (Exception e) {
      Logger.error("Error notifying listener about node removal: " + e.getMessage());
    }
  }

  @Override
  public void onSensorData(int nodeId, List<SensorReading> sensors) {
    try {
      listeners.forEach(listener -> listener.onSensorData(nodeId, sensors));
    } catch (Exception e) {
      Logger.error("Error notifying listener about sensor data: " + e.getMessage());
    }
  }

  @Override
  public void onActuatorStateChanged(int nodeId, int actuatorId, boolean isOn) {
    try {
      listeners.forEach(listener -> listener.onActuatorStateChanged(nodeId, actuatorId, isOn));
    } catch (Exception e) {
      Logger.error("Error notifying listener about actuator state change: " + e.getMessage());
    }
  }

  @Override
  public void actuatorUpdated(int nodeId, Actuator actuator) {
    if (communicationChannel != null) {
      try {
        communicationChannel.sendActuatorChange(nodeId, actuator.getId(), actuator.isOn());
      } catch (Exception e) {
        Logger.error("Error sending actuator change: " + e.getMessage());
      }
    }
    try {
      listeners.forEach(listener ->
              listener.onActuatorStateChanged(nodeId, actuator.getId(), actuator.isOn())
      );
    } catch (Exception e) {
      Logger.error("Error notifying listener about actuator state change: " + e.getMessage());
    }
  }

  @Override
  public void onCommunicationChannelClosed() {
    Logger.info("Communication closed, updating logic...");
    if (communicationChannelListener != null) {
      try {
        communicationChannelListener.onCommunicationChannelClosed();
      } catch (Exception e) {
        Logger.error("Error notifying listener about communication channel closure: " + e.getMessage());
      }
    }
  }
}
