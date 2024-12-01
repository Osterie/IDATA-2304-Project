package no.ntnu.greenhouse.sensor;

/**
 * A sensor that can read the environment in a way.
 * The sensor has a unique ID and a reading.
 */
public abstract class Sensor<T extends SensorReading> {

  protected T reading;

  private final int id;

  // The next ID to be assigned to a sensor, every time a new sensor is created,
  // this value will be incremented.
  private static int nextId = 0;

  /**
   * Create a sensor. An ID will be auto-generated.
   */
  protected Sensor() {
    this.reading = null;
    this.id = generateUniqueId();
  }

  /**
   * Get the ID of the sensor.
   *
   * @return An ID which is guaranteed to be unique at a node level, not
   *         necessarily unique at the whole greenhouse-network level.
   */
  public int getId() {
    return id;
  }

  /**
   * Returns the type of the sensor.
   *
   * @return The type of the sensor.
   */
  public SensorType getType() {
    return reading.getType();
  }

  /**
   * Abstract method to get the format of the data.
   *
   * @return The form of the sensor.
   */
  public abstract String getDataFormat();

  /**
   * Returns the sensor reading of the sensor.
   *
   * @return The sensor reading of the sensor.
   */
  public T getReading() {
    return reading;
  }

  /**
   * Create a clone of the sensor.
   *
   * @return A clone of the sensor.
   */
  public abstract Sensor createClone();

  /**
   * Add random noise to the sensor reading.
   */
  public abstract void addRandomNoise();

  /**
   * Apply an impact to the sensor reading.
   *
   * @param impact The impact to apply.
   */
  public abstract void applyImpact(double impact);

  /**
   * Generate a unique ID for the sensor.
   *
   * @return A unique ID for the sensor.
   */
  private static int generateUniqueId() {
    return nextId++;
  }
}
