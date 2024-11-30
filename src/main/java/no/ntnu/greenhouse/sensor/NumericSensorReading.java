package no.ntnu.greenhouse.sensor;

import java.util.Objects;

/**
 * Represents one sensor reading (value).
 */
public class NumericSensorReading extends SensorReading {

  private double value;
  private final String unit;

  /**
   * Create a new sensor reading.
   *
   * @param type  The type of sensor being red
   * @param value The current value of the sensor
   * @param unit  The unit, for example: %, lux
   */
  public NumericSensorReading(SensorType type, double value, String unit) {
    super(type);
    this.value = value;
    this.unit = unit;
  }

  /**
   * Get the current value of the sensor.
   *
   * @return The current value of the sensor
   */
  public double getValue() {
    return value;
  }

  /**
   * Get the unit of the sensor reading.
   *
   * @return The unit of the sensor reading
   */
  public String getUnit() {
    return unit;
  }

  /**
   * Set a new value for the sensor reading.
   *
   * @param newValue The new value to set
   */
  public void setValue(double newValue) {
    this.value = newValue;
  }

  // TODO change to fit protocol
  /**
   * Get a string representation of the sensor reading.
   *
   * @return A string representation of the sensor reading
   */
  @Override
  public String toString() {
    return "{ type=" + this.type + ", value=" + value + ", unit=" + unit + " }";
  }

  // TODO use to string instead
  /**
   * Get a human-readable (formatted) version of the current reading, including
   * the unit.
   *
   * @return The sensor reading and the unit
   */
  public String getFormatted() {
    return this.getType() + " = " + this.value + " " + this.unit;
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   * 
   * @param o the reference object with which to compare.
   * @return {@code true} if this object is the same as the obj argument;
   *         {@code false} otherwise.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NumericSensorReading that = (NumericSensorReading) o;
    return Double.compare(value, that.value) == 0
        && Objects.equals(type, that.type)
        && Objects.equals(unit, that.unit);
  }
}
