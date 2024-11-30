package no.ntnu.greenhouse;

public class ActuatorFactory {
    private static final SensorType SENSOR_TYPE_TEMPERATURE = SensorType.TEMPERATURE;
    private static final SensorType SENSOR_TYPE_HUMIDITY = SensorType.HUMIDITY;
    private static final SensorType SENSOR_TYPE_LIGHT = SensorType.LIGHT;
    
    private static final ActuatorInfo ACTUATOR_INFO_FAN = ActuatorInfo.FAN;
    private static final ActuatorInfo ACTUATOR_INFO_HEATER = ActuatorInfo.HEATER;
    private static final ActuatorInfo ACTUATOR_INFO_WINDOW = ActuatorInfo.WINDOW;
    private static final ActuatorInfo ACTUATOR_INFO_LIGHT = ActuatorInfo.LIGHT;

    /**
     * Constructing the factory is not allowed.
     */
    private ActuatorFactory() {
    }


   /**
   * Create a typical window-actuator.
   *
   * @param nodeId ID of the node to which this actuator will be connected
   * @return The window actuator
   */
    public static Actuator createWindow(int nodeId) {
        Actuator actuator = new Actuator(ACTUATOR_INFO_WINDOW.getType(), nodeId, ACTUATOR_INFO_WINDOW.getTurnOnText(), ACTUATOR_INFO_WINDOW.getTurnOffText());
        actuator.setImpact(SENSOR_TYPE_TEMPERATURE, -5.0);
        actuator.setImpact(SENSOR_TYPE_HUMIDITY, -10.0);
        actuator.setImpact(SENSOR_TYPE_LIGHT, 1000.0);
        return actuator;
    }

    
    /**
     * Create a typical fan-actuator.
     *
     * @param nodeId ID of the node to which this actuator will be connected
     * @return The fan actuator
     */
    public static Actuator createFan(int nodeId) {
        Actuator actuator = new Actuator(ACTUATOR_INFO_FAN.getType(), nodeId, ACTUATOR_INFO_FAN.getTurnOnText(), ACTUATOR_INFO_FAN.getTurnOffText());
        actuator.setImpact(SENSOR_TYPE_TEMPERATURE, -1.0);
        return actuator;
    }

    /**
     * Create a typical heater-actuator.
     *
     * @param nodeId ID of the node to which this actuator will be connected
     * @return The heater actuator
     */
    public static Actuator createHeater(int nodeId) {
        Actuator actuator = new Actuator(ACTUATOR_INFO_HEATER.getType(), nodeId, ACTUATOR_INFO_HEATER.getTurnOnText(), ACTUATOR_INFO_HEATER.getTurnOffText());
        actuator.setImpact(SENSOR_TYPE_TEMPERATURE, 4.0);
        return actuator;
    }

    /**
     * Create a typical light-actuator.
     *
     * @param nodeId ID of the node to which this actuator will be connected
     * @return The light actuator
     */
    public static Actuator createLight(int nodeId) {
        Actuator actuator = new Actuator(ACTUATOR_INFO_LIGHT.getType(), nodeId, ACTUATOR_INFO_LIGHT.getTurnOnText(), ACTUATOR_INFO_LIGHT.getTurnOffText());
        actuator.setImpact(SENSOR_TYPE_LIGHT, 50000.0);
        return actuator;
    }
}
