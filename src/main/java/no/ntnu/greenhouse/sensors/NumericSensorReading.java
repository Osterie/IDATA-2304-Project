package no.ntnu.greenhouse.sensors;

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
  public NumericSensorReading(String type, double value, String unit) {
    super(type);
    this.value = value;
    this.unit = unit;
  }

  public double getValue() {
    return value;
  }

  public String getUnit() {
    return unit;
  }

  public void setValue(double newValue) {
    this.value = newValue;
  }

  @Override
  public String toString() {
    return "{ type=" + this.type + ", value=" + value + ", unit=" + unit + " }";
  }

  /**
   * Get a human-readable (formatted) version of the current reading, including the unit.
   *
   * @return The sensor reading and the unit
   */
  public String getFormatted() {
    return this.getType() + "=" + this.value + " " + this.unit;
  }

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

  @Override
  public int hashCode() {
    return Objects.hash(type, value, unit);
  }
}
