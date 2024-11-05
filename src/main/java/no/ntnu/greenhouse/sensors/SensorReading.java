package no.ntnu.greenhouse.sensors;

import java.util.Objects;

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

  public String getType() {
    return type;
  }

  public abstract String getFormatted();

  @Override
  public abstract boolean equals(Object o);

  @Override
  public abstract int hashCode();
}
