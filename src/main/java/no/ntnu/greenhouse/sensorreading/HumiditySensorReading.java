package no.ntnu.greenhouse.sensorreading;

/**
 * Represents one humidity sensor reading.
 */
public class HumiditySensorReading extends SensorReading {
    
    private double value;
    private String unit = "%";

    /**
     * Create a new humidity sensor reading.
     *
     * @param type  The type of sensor being read
     * @param value The current value of the sensor
     */
    public HumiditySensorReading(String type, double value) {
        super(type);
        this.value = value;
    }
    
    /**
     * Get the current value of the humidity sensor reading.
     *
     * @return The current value of the sensor reading
     */
    public double getValue() {
        return value;
    }
    
    /**
     * Get the unit of the humidity sensor reading.
     *
     * @return The unit of the sensor reading
     */
    public String getUnit() {
        return unit;
    }
    
    /**
     * Set a new value for the humidity sensor reading.
     *
     * @param newValue The new value to set
     */
    public void setValue(double newValue) {
        this.value = newValue;
    }

    /**
     * Get the sensor reading as a string.
     *
     * @return The sensor reading as a string
     */
    @Override
    public String readingAsString() {
        return super.getType() + ": " + value + " " + unit;
    }
}