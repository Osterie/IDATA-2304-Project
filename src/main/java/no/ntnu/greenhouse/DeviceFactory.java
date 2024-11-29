package no.ntnu.greenhouse;

import no.ntnu.greenhouse.sensors.ImageSensor;
import no.ntnu.greenhouse.sensors.NumericSensor;
import no.ntnu.greenhouse.sensors.Sensor;
import no.ntnu.greenhouse.sensors.AudioSensor;

/**
 * A factory for producing sensors and actuators of specific types.
 */
public class DeviceFactory {
  private static final double NORMAL_GREENHOUSE_TEMPERATURE = 27;
  private static final double MIN_TEMPERATURE = 15;
  private static final double MAX_TEMPERATURE = 40;
  private static final String TEMPERATURE_UNIT = "°C";
  private static final double MIN_HUMIDITY = 50;
  private static final double MAX_HUMIDITY = 100;
  private static final double NORMAL_GREENHOUSE_HUMIDITY = 80;
  private static final String HUMIDITY_UNIT = "%";
  private static final String SENSOR_TYPE_TEMPERATURE = "temperature";
  private static final String SENSOR_TYPE_HUMIDITY = "humidity";
  private static final String SENSOR_TYPE_IMAGE = "image";
  private static final String PATH_TO_IMAGES = "images/";
  private static final String PATH_TO_AUDIO = "audiofiles/";
  private static final String SENSOR_TYPE_AUDIO = "audio";


  private static int nextNodeId = 1;

  /**
   * Constructing the factory is not allowed.
   */
  private DeviceFactory() {
  }

  /**
   * Create a sensor/actuator device with specific number of sensors and actuators.
   *
   * @param temperatureSensorCount Number of temperature sensors to have on the node
   * @param humiditySensorCount    Number of humidity sensors to have on the device
   * @param windowCount            Number of windows the device is connected to
   * @param fanCount               Number of fans the device is connected to
   * @param heaterCount            Number of heaters the device is connected to
   * @param cameraCount            Number of cameras the device is connected to
   * @param audioCount             Number of audio sensors the device is connected to
   * @return The created sensor/actuator device, with a unique ID
   */
  public static SensorActuatorNode createNode(int temperatureSensorCount, int humiditySensorCount,
                                              int windowCount, int fanCount, int heaterCount, int cameraCount, int audioCount) {
    SensorActuatorNode node = new SensorActuatorNode(generateUniqueNodeId());
    if (temperatureSensorCount > 0) {
      node.addSensors(DeviceFactory.createTemperatureSensor(), temperatureSensorCount);
    }
    if (humiditySensorCount > 0) {
      node.addSensors(DeviceFactory.createHumiditySensor(), humiditySensorCount);
    }
    if (windowCount > 0) {
      addActuators(node, ActuatorFactory.createWindow(node.getId()), windowCount);
    }
    if (fanCount > 0) {
      addActuators(node, ActuatorFactory.createFan(node.getId()), fanCount);
    }
    if (heaterCount > 0) {
      addActuators(node, ActuatorFactory.createHeater(node.getId()), heaterCount);
    }
    if (cameraCount > 0) {
      node.addSensors(DeviceFactory.createImageSensor(), cameraCount);
    }
    if (audioCount > 0) {
      node.addSensors(DeviceFactory.createAudioSensor(), audioCount);
    }
    return node;
  }

  /**
   * Add actuators to a node.
   *
   * @param node     The node to which the actuators will be added
   * @param template The template actuator to use for creating new actuators
   * @param n        The number of actuators to add
   */
  static void addActuators(SensorActuatorNode node, Actuator template, int n) {
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
   * Create a typical temperature sensor.
   *
   * @return A typical temperature sensor, which can be used as a template
   */
  public static Sensor createTemperatureSensor() {
    return new NumericSensor(SENSOR_TYPE_TEMPERATURE, MIN_TEMPERATURE, MAX_TEMPERATURE,
        randomize(NORMAL_GREENHOUSE_TEMPERATURE, 1.0), TEMPERATURE_UNIT);
  }

  /**
   * Create a typical humidity sensor.
   *
   * @return A typical humidity sensor which can be used as a template
   */
  public static Sensor createHumiditySensor() {
    return new NumericSensor(SENSOR_TYPE_HUMIDITY, MIN_HUMIDITY, MAX_HUMIDITY,
        randomize(NORMAL_GREENHOUSE_HUMIDITY, 5.0), HUMIDITY_UNIT);
  }

  /**
   * Create a image sensor.
   *
   * @return The image sensor
   */
  public static Sensor createImageSensor() {
    return new ImageSensor(SENSOR_TYPE_IMAGE, PATH_TO_IMAGES);
  }

  public static Sensor createAudioSensor() {
    return new AudioSensor(SENSOR_TYPE_AUDIO, PATH_TO_AUDIO);
  }


  /**
   * Generate a random value within the range [x-d; x+d].
   *
   * @param x The central value
   * @param d The allowed difference range
   * @return a randomized value within the desired range
   */
  private static double randomize(double x, double d) {
    final double zeroToDoubleD = Math.random() * 2 * d;
    final double plusMinusD = zeroToDoubleD - d;
    return x + plusMinusD;
  }

  /**
   * Generate an integer that can be used as a unique ID of sensor/actuator nodes.
   *
   * @return a Unique ID for sensor/actuator nodes
   */
  private static int generateUniqueNodeId() {
    return nextNodeId++;
  }

}