package no.ntnu.greenhouse.sensors;

/**
 * Represents a generic sensor reading.
 */
public abstract class SensorReading {
  protected final String type;

  /**
   * Create a new sensor reading.
   *
   * @param type  The type of sensor being red
   */
  public SensorReading(String type) {
    this.type = type;
  }

  /**
   * Get the type of the sensor.
   *
   * @return The type of the sensor
   */
  public String getType() {
    return type;
  }

  /**
   * Get a human-readable (formatted) version of the current reading.
   *
   * @return The formatted sensor reading
   */
  public abstract String getFormatted();

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param o the reference object with which to compare.
   * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
   */
  @Override
  public abstract boolean equals(Object o);

  /**
   * Returns a hash code value for the object. This method is supported for the benefit of hash tables such as those provided by {@link java.util.HashMap}.
   *
   * @return a hash code value for this object.
   */
  @Override
  public abstract int hashCode();
}
