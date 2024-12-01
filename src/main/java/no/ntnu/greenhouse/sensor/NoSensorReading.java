package no.ntnu.greenhouse.sensor;

/**
 * Represents a sensor reading that contains no data.
 */
public class NoSensorReading extends SensorReading {
  public NoSensorReading() {
    super(SensorType.NONE);
  }

  /**
   * Get a human-readable (formatted) version of the current reading.
   */
  @Override
  public String getFormatted() {
    return "NoSensor";
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
    return o instanceof NoSensorReading;
  }
}
