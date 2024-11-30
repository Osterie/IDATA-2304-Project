package no.ntnu.greenhouse.sensor;

/**
 * The type of a sensor.
 */
public enum SensorType {
    TEMPERATURE("temperature"),
    HUMIDITY("humidity"),
    LIGHT("Illuminance"),
    PH("pH"),
    IMAGE("image"),
    AUDIO("audio"),

    NONE("No sensor type");

    private final String type;

    /**
     * Create a sensor type.
     * 
     * @param type The type of the sensor.
     */
    SensorType(String type) {
        this.type = type;
    }

    /**
     * Get the type of the sensor.
     * 
     * @return The type of the sensor.
     */
    public String getType() {
        return type;
    }

    /**
     * Get the sensor type from a string.
     * 
     * @param text The string to get the sensor type from.
     * @return The sensor type.
     */
    public static SensorType fromString(String text) {
        text = text.trim().toLowerCase();
        for (SensorType b : SensorType.values()) {
            if (b.type.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return NONE;
    }

    /**
     * Get the type of the sensor.
     * 
     * @return The type of the sensor.
     */
    @Override
    public String toString() {
        return type;
    }
}
