package no.ntnu.greenhouse.sensor;

public enum SensorType {
    TEMPERATURE("temperature"),
    HUMIDITY("humidity"),
    LIGHT("Illuminance"),
    PH("pH"),
    IMAGE("image"),
    AUDIO("audio"),

    NONE("No sensor type");

    private final String type;

    SensorType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }

    public static SensorType fromString(String text) {
        text = text.trim().toLowerCase();
        for (SensorType b : SensorType.values()) {
            if (b.type.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return NONE;
    }

    public boolean equals(SensorType other) {
        return this.type.equals(other.type);
    }

    public boolean equals(String other) {
        return this.type.equals(other);
    }
}
