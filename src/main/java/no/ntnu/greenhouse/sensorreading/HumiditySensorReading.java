package no.ntnu.greenhouse.sensorreading;

/**
 * Represents one humidity sensor reading.
 */
public class HumiditySensorReading extends SensorReading {

    public HumiditySensorReading(String type, double value, String unit) {
        super(type, value, unit);
    }
    
}
