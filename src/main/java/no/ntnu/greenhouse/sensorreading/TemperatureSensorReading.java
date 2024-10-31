package no.ntnu.greenhouse.sensorreading;

/**
 * Represents one temperature sensor reading.
 */
public class TemperatureSensorReading extends SensorReading {
    
    private double value;
    private String unit = "C";

    /**
     * Create a new temperature sensor reading.
     *
     * @param type  The type of sensor being read
     * @param value The current value of the sensor
     */
    public TemperatureSensorReading(String type, double value) {
        super(type);
        this.value = value;
    }
    
    /**
     * Get the current value of the temperature sensor reading.
     *
     * @return The current value of the sensor reading
     */
    public double getValue() {
        return value;
    }
    
    /**
     * Get the unit of the temperature sensor reading.
     *
     * @return The unit of the sensor reading
     */
    public String getUnit() {
        return unit;
    }
    
    /**
     * Set a new value for the temperature sensor reading.
     *
     * @param newValue The new value to set
     */
    public void setValue(double newValue) {
        this.value = newValue;
    }

    @Override
    public String readingAsString() {
        return super.getType() + ": " + value + " " + unit;
    }
}