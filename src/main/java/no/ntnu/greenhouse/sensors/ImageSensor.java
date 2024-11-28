package no.ntnu.greenhouse.sensors;


import java.sql.SQLOutput;

/**
 * An image sensor which can sense the environment in a specific way.
 */
public class ImageSensor extends Sensor {
  private final ImageSensorReading reading;
  private final String imagesFilePath;
  private final String dataFormat = "IMG";
  private boolean isOn;

  // TODO do not give image, just have a constant image

  /**
   * Create an image sensor.
   *
   * @param type    The type of the sensor.
   * @param imagesFilePath The file path to the images
   */
  public ImageSensor(String type, String imagesFilePath) {
    this.reading = new ImageSensorReading(type);
    this.imagesFilePath = imagesFilePath;
    this.turnOn();
  }

  /**
   * Create an image sensor.
   *
   * @param type    The type of the sensor.
   * @param imagesFilePath The file path to the images
   * @param image The initial image
   */
  private ImageSensor(String type, String imagesFilePath, ImageSensorReading image) {
    this.reading = image;
    this.imagesFilePath = imagesFilePath;
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
   * Get the current sensor reading.
   *
   * @return The current sensor reading (value)
   */
  public ImageSensorReading getReading() {
    if (isOn) {
      return reading;
    } else {
      throw new IllegalStateException("The sensor is off.");
    }
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
  /**
   * public void applyImpact(double impact) {
   * toggle();
  }*/

  /**
   * Get a string representation of the sensor reading.
   *
   * @return A string representation of the sensor reading
   */
  @Override
  public String toString() {
    return reading.toString();
  }

  @Override
  public void applyImpact(double impact) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'applyImpact'");
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
