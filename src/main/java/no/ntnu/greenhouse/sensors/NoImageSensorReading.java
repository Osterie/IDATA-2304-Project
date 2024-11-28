package no.ntnu.greenhouse.sensors;

/**
 * Represents a sensor reading indicating no image.
 */
public class NoImageSensorReading extends SensorReading {
  public NoImageSensorReading() {
    super("NoImage");
  }


  /**
   * Get the formatted string representation of the sensor reading.
   *
   * @return the string "NoImage"
   */
  @Override
  public String getFormatted() {
    return "NoImage";
  }


    /**
     * Indicates whether some other object is "equal to" to the string "NoImage".
     *
     * @param o the reference object with which to compare.
     * @return {@code true} if this object is the same as the string "NoIMG" argument; {@code false} otherwise.
     */
  @Override
  public boolean equals(Object o) {
    return o instanceof NoImageSensorReading;
  }

  /**
   * returns the hashcode value of the string "NoImage"
   *
   * @return the hashcode value of the string "NoImage"
   */
  @Override
  public int hashCode() {
    return "NoImage".hashCode();
  }
}