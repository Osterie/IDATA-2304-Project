package no.ntnu.greenhouse.sensors;

/**
 * A sensor that can read the environment in a way.
 * The sensor has a unique ID, a type and a reading.
 */
public abstract class Sensor {
    protected SensorReading reading;

    // The next ID to be assigned to a sensor, every time a new sensor is created, this value will be incremented.
    private static int nextId = 1;
    
    /**
     * Create a sensor. An ID will be auto-generated.
     */
    protected Sensor() {
        this.reading = null;
        nextId = generateUniqueId();
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
     * Returns the sensor reading of the sensor.
     * 
     * @return The sensor reading of the sensor.
     */
    public SensorReading getReading() {
        return reading;
    }
    
    public abstract Sensor createClone();

    public abstract void addRandomNoise();

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
