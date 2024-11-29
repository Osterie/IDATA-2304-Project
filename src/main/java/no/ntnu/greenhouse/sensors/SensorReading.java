package no.ntnu.greenhouse.sensors;

import no.ntnu.greenhouse.SensorType;

/**
 * Represents a generic sensor reading.
 */
public abstract class SensorReading {
  protected final SensorType type;

  /**
   * Create a new sensor reading.
   *
   * @param type  The type of sensor being red
   */
  public SensorReading(SensorType type) {
    this.type = type;
  }

  /**
   * Get the type of the sensor.
   *
   * @return The type of the sensor
   */
  public SensorType getType() {
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
}
