package no.ntnu.greenhouse;

public enum ActuatorInfo {
    FAN("fan","On","Off"),
    HEATER("heater", "On", "Off"),
    LIGHT("light", "On", "Off"),
    WINDOW("window", "Open", "Closed"),


    NONE("No sensor type", "On", "Off");

    private final String type;
    private final String turnOnText;
    private final String turnOffText;

    ActuatorInfo(String type, String turnOnText, String turnOffText) {
        this.type = type;
        this.turnOnText = turnOnText;
        this.turnOffText = turnOffText;
    }

    public String getType() {
        return type;
    }

    public String getTurnOnText() {
        return turnOnText;
    }

    public String getTurnOffText() {
        return turnOffText;
    }

    @Override
    public String toString() {
        return type;
    }

    public static ActuatorInfo fromString(String text) {
        text = text.trim().toLowerCase();
        for (ActuatorInfo b : ActuatorInfo.values()) {
            if (b.type.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return NONE;
    }

    public boolean equals (ActuatorInfo other) {
        return this.type.equals(other.type);
    }

    public boolean equals(String other) {
        return this.type.equals(other);
    }
}
