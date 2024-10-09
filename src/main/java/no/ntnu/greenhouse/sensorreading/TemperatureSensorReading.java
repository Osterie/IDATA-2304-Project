package no.ntnu.greenhouse.sensorreading;

/**
 * Represents one temperature sensor reading.
 */
public class TemperatureSensorReading extends SensorReading {
    
    public TemperatureSensorReading(String type, double value, String unit) {
        super(type, value, unit);
    }

}
