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
public class NumericSensor extends Sensor {
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
  public NumericSensor(String type, double min, double max, double current, String unit) {
    this.reading = createSensorReading(type, current, unit);
    this.min = min;
    this.max = max;
    ensureValueBoundsAndPrecision(current);
  }

  /**
   * Create a clone of this sensor.
   *
   * @return A clone of this sensor, where all the fields are the same
   */
  public NumericSensor createClone() {
    if (reading instanceof CameraSensorReading) {
      CameraSensorReading pictureReading = (CameraSensorReading) reading;
      BufferedImage imageClone = deepCopy(pictureReading.getImage());
      return new NumericSensor(this.reading.getType(), this.min, this.max,
          this.reading.getValue(), this.reading.getUnit(), imageClone);
    }
    return new NumericSensor(this.reading.getType(), this.min, this.max,
        this.reading.getValue(), this.reading.getUnit());
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

  /**
   * Create a sensor reading based on the type.
   * 
   * @param type the type of the sensor
   * @param value the value of the sensor
   * @param unit the unit of the sensor
   * @return the sensor reading
   */
  private SensorReading createSensorReading(String type, double value) {
    SensorReading reading = null;

    switch (type) {
      case "temperature":
        reading = new TemperatureSensorReading(type, value);
        break;
      case "humidity":
        reading = new HumiditySensorReading(type, value);
        break;
      default:
        throw new IllegalArgumentException("Unknown sensor type: " + type);
    }
    return reading;
  }
}
