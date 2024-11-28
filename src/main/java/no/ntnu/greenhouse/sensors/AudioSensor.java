package no.ntnu.greenhouse.sensors;

/**
 * An audio sensor which can sense the environment in a specific way.
 */
public class AudioSensor extends Sensor {
  private final AudioSensorReading reading;
  private final String audioFilePath;
  private final String dataFormat = "AUD";

  /**
   * Create an audio sensor.
   *
   * @param type    The type of the sensor.
   * @param audioFilePath The file path to the audio data
   */
  public AudioSensor(String type, String audioFilePath) {
    this.reading = new AudioSensorReading(type);
    this.audioFilePath = audioFilePath;
  }

    /**
     * Create an audio sensor.
     *
     * @param type    The type of the sensor.
     * @param audioFilePath The file path to the audio data
     * @param audio The initial audio
     */
    private AudioSensor(String type, String audioFilePath, AudioSensorReading audio) {
        this.reading = audio;
        this.audioFilePath = audioFilePath;
    }

  /**
   * Returns the type of the sensor.
   * 
   * @return The type of the sensor.
   */
  public String getType() {
    return reading.getType();
  }

  /**
   * Get the file path to the audio data.
   *
   * @return The file path to the audio data.
   */
  public String getAudioFilePath() {
    return audioFilePath;
  }

  /**
   * Get the data format of the sensor.
   *
   * @return The data format of the sensor as a string
   */
  @Override
  public String getDataFormat() {
    return dataFormat;
  }

  /**
   * Get the current reading of the sensor.
   *
   * @return The current reading of the sensor
   */
  public AudioSensorReading getReading() {
    return this.reading;
  }

/**
 * Creates and returns a clone of this AudioSensor object.
 * 
 * @return a new AudioSensor object with the same data format, audio file path, and reading as this AudioSensor.
 */
@Override
public Sensor createClone() {
    return new AudioSensor(this.getType(), this.audioFilePath, this.reading);
}

/**
 * Adds random noise to the audio sensor reading.
 * This method has a 50% chance of generating random audio noise
 * using the specified audio file path.
 */
@Override
public void addRandomNoise() {
    if (Math.random() < 0.5) {
        this.reading.generateRandomAudio(this.audioFilePath);
    }
}

/**
 * TODO: implement some kind of impact, not just generate a random
 * Applies an impact to the audio sensor, which triggers the generation of a random audio reading.
 *
 * @param impact the magnitude of the impact to be applied
 */
@Override
public void applyImpact(double impact) {
    this.reading.generateRandomAudio(this.audioFilePath);
}

  /**
   * Get a string representation of the sensor reading.
   *
   * @return A string representation of the sensor reading
   */
  @Override
  public String toString() {
    return reading.toString();
  }

}