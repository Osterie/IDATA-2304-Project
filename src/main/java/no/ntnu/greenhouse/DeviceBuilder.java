package no.ntnu.greenhouse;

import no.ntnu.greenhouse.actuator.Actuator;
import no.ntnu.greenhouse.actuator.ActuatorFactory;
import no.ntnu.greenhouse.sensor.SensorFactory;

/**
 * The DeviceBuilder class is used to construct a SensorActuatorNode with various
 * sensors and actuators.
 * It provides methods to add different types of sensors and actuators to the node.
 *
 * <p>Usage example:
 * <pre>
 * DeviceBuilder builder = new DeviceBuilder();
 * builder.addTemperatureSensor(2)
 *        .addHumiditySensor(1)
 *        .addFanActuator(1);
 * SensorActuatorNode node = builder.build();
 * </pre>
 *
 * <p>Methods:
 * - {@link #addTemperatureSensor(int)}: Adds temperature sensors to the node.
 * - {@link #addHumiditySensor(int)}: Adds humidity sensors to the node.
 * - {@link #addImageSensor(int)}: Adds image sensors to the node.
 * - {@link #addAudioSensor(int)}: Adds audio sensors to the node.
 * - {@link #addWindowActuator(int)}: Adds window actuators to the node.
 * - {@link #addFanActuator(int)}: Adds fan actuators to the node.
 * - {@link #addHeaterActuator(int)}: Adds heater actuators to the node.
 * - {@link #build()}: Builds and returns the configured SensorActuatorNode.
 *
 * <p>Note:
 * - The builder ensures that a unique node ID is generated for each instance.
 * - The number of sensors or actuators to add must be greater than zero.
 */
public class DeviceBuilder {

  private final SensorActuatorNode node;

  private static int nextNodeId = 1;

  /**
   * Constructs a DeviceBuilder instance and initializes a new SensorActuatorNode
   * with a unique ID.
   */
  public DeviceBuilder() {
    this.node = new SensorActuatorNode(generateUniqueNodeId());
  }

  /**
   * Builds and returns the configured SensorActuatorNode.
   *
   * @return The configured SensorActuatorNode
   */
  public SensorActuatorNode build() {
    return node;
  }

  /**
   * Adds a specified number of temperature sensors to the node.
   *
   * @param count The number of temperature sensors to add
   * @return The builder object
   */
  public DeviceBuilder addTemperatureSensor(int count) {
    if (count > 0) {
      this.node.addSensors(SensorFactory.createTemperatureSensor(), count);
    }
    return this;
  }

  /**
   * Adds a specified number of humidity sensors to the node.
   *
   * @param count The number of humidity sensors to add
   * @return The builder object
   */
  public DeviceBuilder addHumiditySensor(int count) {
    if (count > 0) {
      this.node.addSensors(SensorFactory.createHumiditySensor(), count);
    }
    return this;
  }

  /**
   * Adds a specified number of light sensors to the node.
   *
   * @param count The number of light sensors to add
   * @return The builder object
   */
  public DeviceBuilder addLightSensor(int count) {
    if (count > 0) {
      this.node.addSensors(SensorFactory.createLightSensor(), count);
    }
    return this;
  }

  /**
   * Adds a specified number of pH sensors to the node.
   *
   * @param count The number of pH sensors to add
   * @return The builder object
   */
  public DeviceBuilder addPhSensor(int count) {
    if (count > 0) {
      this.node.addSensors(SensorFactory.createPhSensor(), count);
    }
    return this;
  }

  /**
   * Adds a specified number of image sensors to the node.
   *
   * @param count The number of image sensors to add
   * @return The builder object
   */
  public DeviceBuilder addImageSensor(int count) {
    if (count > 0) {
      this.node.addSensors(SensorFactory.createImageSensor(), count);
    }
    return this;
  }

  /**
   * Adds a specified number of audio sensors to the node.
   *
   * @param count The number of audio sensors to add
   * @return The builder object
   */
  public DeviceBuilder addAudioSensor(int count) {
    if (count > 0) {
      this.node.addSensors(SensorFactory.createAudioSensor(), count);
    }
    return this;
  }

  /**
   * Adds a specified number of window actuators to the node.
   *
   * @param count The number of window actuators to add
   * @return The builder object
   */
  public DeviceBuilder addWindowActuator(int count) {
    if (count > 0) {
      this.addActuators(node, ActuatorFactory.createWindow(node.getId()), count);
    }
    return this;
  }

  /**
   * Adds a specified number of fan actuators to the node.
   *
   * @param count The number of fan actuators to add
   * @return The builder object
   */
  public DeviceBuilder addFanActuator(int count) {
    if (count > 0) {
      this.addActuators(node, ActuatorFactory.createFan(node.getId()), count);
    }
    return this;
  }

  /**
   * Adds a specified number of heater actuators to the node.
   *
   * @param count The number of heater actuators to add
   * @return The builder object
   */
  public DeviceBuilder addHeaterActuator(int count) {
    if (count > 0) {
      this.addActuators(node, ActuatorFactory.createHeater(node.getId()), count);
    }
    return this;
  }

  /**
   * Adds a specified number of light actuators to the node.
   *
   * @param count The number of light actuators to add
   * @return The builder object
   */
  public DeviceBuilder addLightActuator(int count) {
    if (count > 0) {
      this.addActuators(node, ActuatorFactory.createLight(node.getId()), count);
    }
    return this;
  }

  /**
   * Adds a specified number of actuators to a node using a template actuator.
   *
   * @param node     The node to which the actuators will be added
   * @param template The template actuator to use for creating new actuators
   * @param n        The number of actuators to add
   */
  private void addActuators(SensorActuatorNode node, Actuator template, int n) {
    if (template == null) {
      throw new IllegalArgumentException("Actuator template is missing");
    }
    if (n <= 0) {
      throw new IllegalArgumentException("Can't add a negative number of actuators");
    }

    for (int i = 0; i < n; ++i) {
      Actuator actuator = template.createClone();
      node.addActuator(actuator);
    }
  }

  /**
   * Generates a unique integer ID for sensor/actuator nodes.
   *
   * @return A unique ID for sensor/actuator nodes
   */
  private static int generateUniqueNodeId() {
    return nextNodeId++;
  }
}
