package no.ntnu.greenhouse.sensors;

import java.awt.image.BufferedImage;

/**
 * An image sensor which can sense the environment in a specific way.
 */
public class ImageSensor extends Sensor {
  private final ImageSensorReading reading;
  private final String imagesFilePath;

  // TODO do not give image, just have a constant image

  /**
   * Create a sensor.
   *
   * @param type    The type of the sensor.
   * @param currentImage The current (starting) image of the sensor
   */
  public ImageSensor(String type, String imagesFilePath) {
    this.reading = new ImageSensorReading(type);
    this.imagesFilePath = imagesFilePath;
  }

  public String getType() {
    return reading.getType();
  }

  /**
   * Get the current sensor reading.
   *
   * @return The current sensor reading (value)
   */
  public ImageSensorReading getReading() {
    return reading;
  }

  /**
   * Create a clone of this sensor.
   *
   * @return A clone of this sensor, where all the fields are the same
   */
  public ImageSensor createClone() {
    return new ImageSensor(this.getType(), this.imagesFilePath);
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
    this.reading.generateRandomImage(imagesFilePath);
  }

  @Override
  public String toString() {
    return reading.toString();
  }
}