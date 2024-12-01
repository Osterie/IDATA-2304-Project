package no.ntnu.greenhouse.sensor;


import java.sql.SQLOutput;

/**
 * An image sensor which can sense the environment in a specific way.
 */
public class ImageSensor extends Sensor<ImageSensorReading> {
  // private final ImageSensorReading reading;
  private final String imagesFilePath;
  private final String dataFormat = "IMG";
  private boolean isOn;

  /**
   * Create an image sensor.
   *
   * @param type    The type of the sensor.
   * @param imagesFilePath The file path to the images
   */
  public ImageSensor(SensorType type, String imagesFilePath) {
    this.reading = new ImageSensorReading(type);
    this.imagesFilePath = imagesFilePath;
    this.reading.generateRandomImage(imagesFilePath);
    this.turnOn();
  }

  /**
   * Create an image sensor.
   *
   * @param type    The type of the sensor.
   * @param imagesFilePath The file path to the images
   * @param image The initial image
   */
  private ImageSensor(SensorType type, String imagesFilePath, ImageSensorReading image) {
    this.reading = image;
    this.imagesFilePath = imagesFilePath;
    this.turnOn();
  }

  /**
   * Get the file path to the images.
   *
   * @return The file path to the images.
   */
  public String getImagesFilePath() {
    return imagesFilePath;
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
   * Create a clone of this sensor.
   *
   * @return A clone of this sensor, where all the fields are the same
   */
  public ImageSensor createClone() {
    return new ImageSensor(this.getType(), this.imagesFilePath, this.reading);
  }

  /**
   * Add a random noise to the sensors to simulate realistic values.
   */
  public void addRandomNoise() {
    // 50/50 chance of changing the image
    if (Math.random() < 0.5) {
      this.reading.generateRandomImage(imagesFilePath);
    }
  }

  /**
   * Apply an external impact (from an actuator) to the current value of the sensor.
   *
   * @param impact The impact to apply - the delta for the value
   */

    public void applyImpact(double impact) {
      toggle();
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
  

    /**
     * Turn on the sensor.
     */
  private void turnOn() {
    isOn = true;
  }

  private void toggle() {
    isOn = !isOn;
  }
}
