package no.ntnu.greenhouse;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import no.ntnu.greenhouse.actuator.Actuator;
import no.ntnu.greenhouse.actuator.ActuatorCollection;
import no.ntnu.greenhouse.sensor.ImageSensorReading;
import no.ntnu.greenhouse.sensor.NumericSensorReading;
import no.ntnu.greenhouse.sensor.Sensor;
import no.ntnu.greenhouse.sensor.SensorReading;
import no.ntnu.greenhouse.sensor.SensorType;
import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.listeners.common.CommunicationChannelListener;
import no.ntnu.listeners.greenhouse.NodeStateListener;
import no.ntnu.listeners.greenhouse.SensorListener;
import no.ntnu.tools.Logger;

/**
 * Represents one node with sensors and actuators.
 */
public class SensorActuatorNode implements ActuatorListener, CommunicationChannelListener {
  // How often to generate new sensor values, in seconds.
  private static final long SENSING_DELAY = 5000;
  private final int id;

  private final List<Sensor> sensors = new LinkedList<>();
  private final ActuatorCollection actuators = new ActuatorCollection();

  private final List<SensorListener> sensorListeners = new LinkedList<>();
  private final List<ActuatorListener> actuatorListeners = new LinkedList<>();
  private final List<NodeStateListener> stateListeners = new LinkedList<>();

  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  private boolean running;
  private final Random random = new Random();

  /**
   * Create a sensor/actuator node. Note: the node itself does not check whether
   * the ID is unique.
   * This is done at the greenhouse-level.
   *
   * @param id A unique ID of the node
   */
  public SensorActuatorNode(int id) {
    this.id = id;
    this.running = false;
  }

  /**
   * Get the unique ID of the node.
   *
   * @return the ID
   */
  public int getId() {
    return id;
  }

  /**
   * Add sensors to the node.
   *
   * @param template The template to use for the sensors. The template will be
   *                 cloned.
   *                 This template defines the type of sensors, the value range,
   *                 value
   *                 generation algorithms, etc.
   * @param n        The number of sensors to add to the node.
   */
  public void addSensors(Sensor template, int n) {
    if (template == null) {
      throw new IllegalArgumentException("Sensor template is missing");
    }
    SensorType type = template.getType();
    if (type == null) {
      throw new IllegalArgumentException("Sensor type missing");
    }
    if (n <= 0) {
      throw new IllegalArgumentException("Can't add a negative number of sensors");
    }

    for (int i = 0; i < n; ++i) {
      this.sensors.add(template.createClone());
    }
  }

  /**
   * Add an actuator to the node.
   *
   * @param actuator The actuator to add
   */
  public void addActuator(Actuator actuator) {
    if (actuator == null) {
      throw new IllegalArgumentException("Actuator cannot be null");
    }
    actuator.setListener(this);
    this.actuators.add(actuator);
    Logger.info("Created " + actuator.getType() + "[" + actuator.getId() + "] on node " + id);
  }

  /**
   * Register a new listener for sensor updates.
   *
   * @param listener The listener which will get notified every time sensor values
   *                 change.
   */
  public void addSensorListener(SensorListener listener) {
    if (listener == null) {
      throw new IllegalArgumentException("SensorListener cannot be null");
    }
    if (!this.sensorListeners.contains(listener)) {
      this.sensorListeners.add(listener);
    }
  }

  /**
   * Register a new listener for actuator updates.
   *
   * @param listener The listener which will get notified every time actuator
   *                 state changes.
   */
  public void addActuatorListener(ActuatorListener listener) {
    if (listener == null) {
      throw new IllegalArgumentException("ActuatorListener cannot be null");
    }
    if (!this.actuatorListeners.contains(listener)) {
      this.actuatorListeners.add(listener);
    }
  }

  /**
   * Register a new listener for node state updates.
   *
   * @param listener The listener which will get notified when the state of this
   *                 node changes
   */
  public void addStateListener(NodeStateListener listener) {
    if (listener == null) {
      throw new IllegalArgumentException("NodeStateListener cannot be null");
    }
    if (!this.stateListeners.contains(listener)) {
      this.stateListeners.add(listener);
    }
  }

  /**
   * Start simulating the sensor node's operation.
   */
  public void start() {
    if (!this.running) {
      this.startPeriodicSensorReading();
      this.running = true;
      this.notifyStateChanges(true);
    }
  }

  /**
   * Stop simulating the sensor node's operation.
   */
  public void stop() {
    if (this.running) {
      Logger.info("-- Stopping simulation of node " + id);
      this.stopPeriodicSensorReading();
      this.running = false;
      this.notifyStateChanges(false);
    }
  }

  /**
   * Check whether the node is currently running.
   *
   * @return True if it is in a running-state, false otherwise
   */
  public boolean isRunning() {
    return this.running;
  }

  /**
   * Start generating new sensor values periodically.
   */
  private void startPeriodicSensorReading() {
    Runnable newSensorValueTask = this::generateNewSensorValues;
    long randomStartDelay = random.nextLong(SENSING_DELAY);
    this.scheduler.scheduleAtFixedRate(newSensorValueTask, randomStartDelay, SENSING_DELAY, TimeUnit.MILLISECONDS);
  }

  /**
   * Stop generating new sensor values periodically.
   */
  private void stopPeriodicSensorReading() {
    this.scheduler.shutdownNow();
  }

  /**
   * Generate new sensor values and send a notification to all listeners.
   */
  public void generateNewSensorValues() {
    this.addRandomNoiseToSensors();
    this.notifySensorChanges();
  }

  /**
   * Add random noise to all sensors.
   */
  private void addRandomNoiseToSensors() {
    sensors.parallelStream().forEach(Sensor::addRandomNoise);
  }

  /**
   * Print a debug message with all the sensor values and actuator states.
   */
  private void debugPrint() {
    for (Sensor sensor : sensors) {
      SensorReading reading = sensor.getReading();
      if (reading instanceof NumericSensorReading) {
        NumericSensorReading numericSensorReading = (NumericSensorReading) reading;
        Logger.infoNoNewline(" " + numericSensorReading.getFormatted());
      } else if (reading instanceof ImageSensorReading) {
        ImageSensorReading imageSensorReading = (ImageSensorReading) reading;
        Logger.infoNoNewline(" " + imageSensorReading.getType());
      }
    }
    Logger.infoNoNewline(" :");
    actuators.debugPrint();
    Logger.info("");
  }

  /**
   * Toggle an actuator attached to this device.
   *
   * @param actuatorId The ID of the actuator to toggle
   * @throws IllegalArgumentException If no actuator with given configuration is
   *                                  found on this node
   */
  public void toggleActuator(int actuatorId) {
    Actuator actuator = getActuator(actuatorId);
    if (actuator == null) {
      throw new IllegalArgumentException("actuator[" + actuatorId + "] not found on node " + id);
    }
    actuator.toggle();
  }

  /**
   * Get an actuator by its ID.
   *
   * @param actuatorId The ID of the actuator to get
   * @return The actuator with the given ID, or null if not found
   */
  public Actuator getActuator(int actuatorId) {
    return actuators.get(actuatorId);
  }

  /**
   * Notify all listeners that the sensor values have changed.
   */
  private void notifySensorChanges() {
    for (SensorListener listener : sensorListeners) {
      listener.sensorsUpdated(sensors);
    }
  }

  /**
   * An actuator has been turned on or off. Apply an impact from it to all sensors
   * of given type. And notify the listeners.
   *
   * @param sensorType The type of sensors affected
   * @param impact     The impact to apply
   */
  @Override
  public void actuatorUpdated(int nodeId, Actuator actuator) {
    if (actuator == null) {
      Logger.error("Actuator is null for node " + nodeId);
      return;
    }
    actuator.applyImpact(this);
    this.notifyActuatorChange(actuator);
  }

  /**
   * Notify the listeners that an actuator has changed its state.
   *
   * @param actuator The actuator that has changed
   */
  private void notifyActuatorChange(Actuator actuator) {
    String onOff = actuator.isOn() ? "ON" : "off";
    Logger.info(" => " + actuator.getType() + " on node " + id + " " + onOff);
    for (ActuatorListener listener : this.actuatorListeners) {
      listener.actuatorUpdated(id, actuator);
    }
  }

  /**
   * Notify the listeners that the state of this node has changed.
   *
   * @param isReady When true, let them know that this node is ready;
   *                when false - that this node is shut down
   */
  private void notifyStateChanges(boolean isReady) {
    Logger.info("Notify state changes for node " + id);
    for (NodeStateListener listener : stateListeners) {
      if (isReady) {
        listener.onNodeReady(this);
      } else {
        listener.onNodeStopped(this);
      }
    }
  }

  /**
   * An actuator has been turned on or off. Apply an impact from it to all sensors
   * of given type.
   *
   * @param sensorType The type of sensors affected
   * @param impact     The impact to apply
   */
  public void applyActuatorImpact(SensorType sensorType, double impact) {
    if (sensorType == null) {
      throw new IllegalArgumentException("Sensor type cannot be null or empty");
    }
    for (Sensor sensor : this.sensors) {
      if (sensor.getType().equals(sensorType)) {
        sensor.applyImpact(impact);
      }
    }
  }

  /**
   * Get all the sensors available on the device.
   *
   * @return List of all the sensors
   */
  public List<Sensor> getSensors() {
    return this.sensors;
  }

  /**
   * Get the readings from all the sensors.
   *
   * @return A list of sensor readings
   */
  public List<SensorReading> getSensorReadings() {
    List<SensorReading> readings = new LinkedList<>();
    for (Sensor sensor : sensors) {
      readings.add(sensor.getReading());
    }
    return readings;
  }

  /**
   * Get all the actuators available on the node.
   *
   * @return A collection of the actuators
   */
  public ActuatorCollection getActuators() {
    return this.actuators;
  }

  /**
   * Handles the case when the communication channel is closed.
   */
  @Override
  public void onCommunicationChannelClosed() {
    Logger.info("Communication channel closed for node " + id);
    this.stop();
  }

  /**
   * Set an actuator to a desired state.
   *
   * @param actuatorId ID of the actuator to set.
   * @param on         Whether it should be on (true) or off (false)
   */
  public void setActuator(int actuatorId, boolean on) {
    Actuator actuator = getActuator(actuatorId);
    if (actuator == null) {
      Logger.error("Actuator not found on node " + actuatorId);
    }
    actuator.set(on, true);
  }

  /**
   * Set all actuators to desired state.
   *
   * @param on Whether the actuators should be on (true) or off (false)
   */
  public void setAllActuators(boolean on) {
    for (Actuator actuator : actuators) {
      actuator.set(on, true);
    }
  }
}
