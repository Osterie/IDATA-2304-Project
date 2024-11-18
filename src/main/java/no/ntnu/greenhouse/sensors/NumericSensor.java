package no.ntnu.greenhouse.sensors;

/**
 * A numeric sensor which can sense the environment in a specific way.
 */
public class NumericSensor extends Sensor {
  private final NumericSensorReading reading;
  private final double min;
  private final double max;

  /**
   * Create a numeric sensor.
   *
   * @param type    The type of the sensor. Examples: "temperature", "humidity"
   * @param min     Minimum allowed value
   * @param max     Maximum allowed value
   * @param current The current (starting) value of the sensor
   * @param unit    The measurement unit. Examples: "%", "C", "lux"
   */
  public NumericSensor(String type, double min, double max, double current, String unit) {
    super();
    this.reading = new NumericSensorReading(type, current, unit);
    this.min = min;
    this.max = max;
    ensureValueBoundsAndPrecision(current);
  }

  /**
   * Get the type of the sensor.
   *
   * @return The type of the sensor
   */
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
   * @throws IllegalStateException if the sensor reading is null
   */
  public NumericSensor createClone() {
    if (this.reading == null) {
      throw new IllegalStateException("The sensor reading is null");
    }
    return new NumericSensor(this.reading.getType(), this.min, this.max,
        this.reading.getValue(), this.reading.getUnit());
  }

  /**
   * Add a random noise to the sensor to simulate realistic values.
   */
  public void addRandomNoise() {
    double newValue = this.reading.getValue() + generateRealisticNoise();
    ensureValueBoundsAndPrecision(newValue);
  }

  /**
   * Ensure the value is within bounds and has the correct precision.
   *
   * @param newValue The new value to check
   */
  private void ensureValueBoundsAndPrecision(double newValue) {
    newValue = roundToTwoDecimals(newValue);
    if (newValue < min) {
      newValue = min;
    } else if (newValue > max) {
      newValue = max;
    }
    reading.setValue(newValue);
  }

  /**
   * Round a value to two decimal places.
   *
   * @param value The value to round
   * @return The rounded value
   */
  private double roundToTwoDecimals(double value) {
    return Math.round(value * 100.0) / 100.0;
  }

  /**
   * Generate realistic noise to add to the sensor value.
   *
   * @return The generated noise
   */
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
   * Get a string representation of the sensor.
   *
   * @return A string representation of the sensor
   */
  @Override
  public String toString() {
    return reading.toString();
  }
}