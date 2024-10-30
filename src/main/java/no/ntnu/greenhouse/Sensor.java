package no.ntnu.greenhouse;

import no.ntnu.greenhouse.sensorreading.HumiditySensorReading;
import no.ntnu.greenhouse.sensorreading.CameraSensorReading;
import no.ntnu.greenhouse.sensorreading.SensorReading;
import no.ntnu.greenhouse.sensorreading.TemperatureSensorReading;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;


//TODO: make abstract class?

/**
 * A sensor which can sense the environment in a specific way.
 */
public class Sensor {
  private final SensorReading reading;
  private final double min;
  private final double max;

  /**
   * Create a sensor.
   *
   * @param type    The type of the sensor. Examples: "temperature", "humidity"
   * @param min     Minimum allowed value
   * @param max     Maximum allowed value
   * @param current The current (starting) value of the sensor
   * @param unit    The measurement unit. Examples: "%", "C", "lux"
   */
  public Sensor(String type, double min, double max, double current, String unit) {
    this.reading = createSensorReading(type, current, unit);
    this.min = min;
    this.max = max;
    ensureValueBoundsAndPrecision(current);
  }

  /**
   * Create a sensor.
   *
   * @param type    The type of the sensor. Examples: "temperature", "humidity"
   * @param min     Minimum allowed value
   * @param max     Maximum allowed value
   * @param current The current (starting) value of the sensor
   * @param unit    The measurement unit. Examples: "%", "C", "lux"
   * @param image   The image data for the sensor
   */
  public Sensor(String type, double min, double max, double current, String unit, BufferedImage image) {
    this.reading = createSensorReading(type, current, unit, image);
    this.min = min;
    this.max = max;
    ensureValueBoundsAndPrecision(current);
  }

  public String getType() {
    return reading.getType();
  }

  /**
   * Get the current sensor reading.
   *
   * @return The current sensor reading (value)
   */
  public SensorReading getReading() {
    return reading;
  }

  /**
   * Create a clone of this sensor.
   *
   * @return A clone of this sensor, where all the fields are the same
   */
  public Sensor createClone() {
    if (reading instanceof CameraSensorReading) {
      CameraSensorReading pictureReading = (CameraSensorReading) reading;
      BufferedImage imageClone = deepCopy(pictureReading.getImage());
      return new Sensor(this.reading.getType(), this.min, this.max,
          this.reading.getValue(), this.reading.getUnit(), imageClone);
    }
    return new Sensor(this.reading.getType(), this.min, this.max,
        this.reading.getValue(), this.reading.getUnit());
  }


/**
     * Create a deep copy of a BufferedImage.
     *
     * @param image The BufferedImage to copy.
     * @return A deep copy of the BufferedImage.
     */
    private BufferedImage deepCopy(BufferedImage image) {
      BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
      Graphics2D g = copy.createGraphics();
      g.drawRenderedImage(image, null);
      g.dispose();
      return copy;
  }

  /**
   * Add a random noise to the sensors to simulate realistic values.
   */
  public void addRandomNoise() {
    double newValue = this.reading.getValue() + generateRealisticNoise();
    ensureValueBoundsAndPrecision(newValue);
  }

  private void ensureValueBoundsAndPrecision(double newValue) {
    newValue = roundToTwoDecimals(newValue);
    if (newValue < min) {
      newValue = min;
    } else if (newValue > max) {
      newValue = max;
    }
    reading.setValue(newValue);
  }

  private double roundToTwoDecimals(double value) {
    return Math.round(value * 100.0) / 100.0;
  }

  private double generateRealisticNoise() {
    final double wholeRange = max - min;
    final double onePercentOfRange = wholeRange / 100.0;
    final double zeroToTwoPercent = Math.random() * onePercentOfRange * 2;
    return zeroToTwoPercent - onePercentOfRange; // In the range [-1%..+1%]
  }

  /**
   * Apply an external impact (from an actuator) to the current value of the sensor.
   *
   * @param impact The impact to apply - the delta for the value
   */
  public void applyImpact(double impact) {
    double newValue = this.reading.getValue() + impact;
    ensureValueBoundsAndPrecision(newValue);
  }

  @Override
  public String toString() {
    return reading.toString();
  }

  /**
   * Create a sensor reading based on the type.
   * 
   * @param type the type of the sensor
   * @param value the value of the sensor
   * @param unit the unit of the sensor
   * @return the sensor reading
   */
  private SensorReading createSensorReading(String type, double value, String unit) {
    SensorReading reading = null;

    switch (type) {
      case "temperature":
        reading = new TemperatureSensorReading(type, value, unit);
        break;
      case "humidity":
        reading = new HumiditySensorReading(type, value, unit);
        break;
      default:
        throw new IllegalArgumentException("Unknown sensor type: " + type);
    }
    return reading;
  }

  /**
   * Create a sensor reading based on the type, with an image.
   * 
   * @param type the type of the sensor
   * @param value the value of the sensor
   * @param unit the unit of the sensor
   * @param image the image data for the sensor
   * @return the sensor reading
   */
  private SensorReading createSensorReading(String type, double value, String unit, BufferedImage image) {
    SensorReading reading = null;

    switch (type) {
      case "camera":
        reading = new CameraSensorReading(type, value, unit, image);
        break;
      default:
        throw new IllegalArgumentException("Unknown sensor type: " + type);
    }
    return reading;
  }
}
