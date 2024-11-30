package no.ntnu.greenhouse;

/**
 * Represents the different types of actuators in the greenhouse.
 */
public enum ActuatorInfo {
    FAN("fan", "On", "Off"),
    HEATER("heater", "On", "Off"),
    LIGHT("light", "On", "Off"),
    WINDOW("window", "Open", "Closed"),

    NONE("No sensor type", "On", "Off");

    private final String type;
    private final String turnOnText;
    private final String turnOffText;

    /**
     * Constructor for ActuatorInfo
     * 
     * @param type        the type of actuator
     * @param turnOnText  the text to display when the actuator is turned on
     * @param turnOffText the text to display when the actuator is turned off
     */
    ActuatorInfo(String type, String turnOnText, String turnOffText) {
        this.type = type;
        this.turnOnText = turnOnText;
        this.turnOffText = turnOffText;
    }

    /**
     * Get the type of the actuator.
     * 
     * @return the type of the actuator
     */
    public String getType() {
        return type;
    }

    /**
     * Get the text to display when the actuator is turned on.
     * 
     * @return the text to display when the actuator is turned on
     */
    public String getTurnOnText() {
        return turnOnText;
    }

    /**
     * Get the text to display when the actuator is turned off.
     * 
     * @return the text to display when the actuator is turned off
     */
    public String getTurnOffText() {
        return turnOffText;
    }
}
