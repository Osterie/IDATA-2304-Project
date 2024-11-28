package no.ntnu.greenhouse.sensors;

/**
 * Represents a sensor reading indicating no audio.
 */
public class NoAudioSensorReading extends SensorReading {
  public NoAudioSensorReading() {
    super("NoAudio");
  }


  /**
   * Get the formatted string representation of the sensor reading.
   *
   * @return the string "NoAudio"
   */
  @Override
  public String getFormatted() {
    return "NoAudio";
  }


    /**
     * Indicates whether some other object is "equal to" to the string "NoAudio".
     *
     * @param o the reference object with which to compare.
     * @return {@code true} if this object is the same as the string "NoIMG" argument; {@code false} otherwise.
     */
  @Override
  public boolean equals(Object o) {
    return o instanceof NoAudioSensorReading;
  }

  /**
   * returns the hashcode value of the string "NoAudio"
   *
   * @return the hashcode value of the string "NoAudio"
   */
  @Override
  public int hashCode() {
    return "NoAudio".hashCode();
  }
}