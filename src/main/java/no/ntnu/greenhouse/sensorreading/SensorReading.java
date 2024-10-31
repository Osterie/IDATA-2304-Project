package no.ntnu.greenhouse.sensorreading;

import java.util.Objects;


//TODO: Refactor the methods to fit an abstract class

/**
 * Represents one sensor reading (value).
 */
public abstract class SensorReading {
  private final String type;

  /**
   * Create a new sensor reading.
   *
   * @param type  The type of sensor being red
   * @param value The current value of the sensor
   * @param unit  The unit, for example: %, lux
   */
  public SensorReading(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
  
  public abstract String readingAsString();


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SensorReading that = (SensorReading) o;
    return Objects.equals(type, that.type)
        && Objects.equals(readingAsString(), that.readingAsString());
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, readingAsString());
  }

}
