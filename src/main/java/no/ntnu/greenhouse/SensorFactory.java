package no.ntnu.greenhouse;

import no.ntnu.greenhouse.sensors.ImageSensor;
import no.ntnu.greenhouse.sensors.NumericSensor;
import no.ntnu.greenhouse.sensors.Sensor;
import no.ntnu.constants.Resources;
import no.ntnu.greenhouse.sensors.AudioSensor;

/**
 * A factory for producing sensors and actuators of specific types.
 */
public class SensorFactory {
  private static final SensorType SENSOR_TYPE_TEMPERATURE = SensorType.TEMPERATURE;
  private static final double NORMAL_GREENHOUSE_TEMPERATURE = 27;
  private static final double MIN_TEMPERATURE = 15;
  private static final double MAX_TEMPERATURE = 40;
  private static final String TEMPERATURE_UNIT = "°C";

  private static final SensorType SENSOR_TYPE_HUMIDITY = SensorType.HUMIDITY;
  private static final double MIN_HUMIDITY = 50;
  private static final double MAX_HUMIDITY = 100;
  private static final double NORMAL_GREENHOUSE_HUMIDITY = 80;
  private static final String HUMIDITY_UNIT = "%";

  private static final SensorType SENSOR_TYPE_LIGHT = SensorType.LIGHT;
  private static final double MIN_LIGHT = 0;
  private static final int MAX_LIGHT = 100000;
  private static final int NORMAL_GREENHOUSE_LIGHT = 30000;
  private static final String LIGHT_UNIT = "Lux";

  private static final SensorType SENSOR_TYPE_PH = SensorType.PH;
  private static final double MIN_PH = 4;
  private static final double MAX_PH = 10;
  private static final double NORMAL_GREENHOUSE_PH = 7;
  private static final String PH_UNIT = "";
    
  private static final SensorType SENSOR_TYPE_IMAGE = SensorType.IMAGE;
  private static final String PATH_TO_IMAGES = Resources.IMAGES.getPath();

  private static final SensorType SENSOR_TYPE_AUDIO = SensorType.AUDIO;
  private static final String PATH_TO_AUDIO = Resources.AUDIO.getPath();

  /**
   * Constructing the factory is not allowed.
   */
  private SensorFactory() {
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
   * Create a typical light sensor.
   *
   * @return A typical light sensor which can be used as a template
   */
  public static Sensor createLightSensor() {
    return new NumericSensor(SENSOR_TYPE_LIGHT, MIN_LIGHT, MAX_LIGHT,
        randomize(NORMAL_GREENHOUSE_LIGHT, 1000.0), LIGHT_UNIT);
  }

  /**
   * Create a typical pH sensor.
   *
   * @return A typical pH sensor which can be used as a template
   */
  public static Sensor createPhSensor() {
    return new NumericSensor(SENSOR_TYPE_PH, MIN_PH, MAX_PH,
        randomize(NORMAL_GREENHOUSE_PH, 0.5), PH_UNIT);
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
}