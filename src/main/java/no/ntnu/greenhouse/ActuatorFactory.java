package no.ntnu.greenhouse;

public class ActuatorFactory {
    private static final String SENSOR_TYPE_TEMPERATURE = "temperature";
    private static final String SENSOR_TYPE_HUMIDITY = "humidity";
    private static final String ACTUATOR_TYPE_FAN = "fan";
    private static final String ACTUATOR_TYPE_HEATER = "heater";
    private static final String ACTUATOR_TYPE_WINDOW = "window";


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
        Actuator actuator = new Actuator(ACTUATOR_TYPE_WINDOW, nodeId);
        actuator.setImpact(SENSOR_TYPE_TEMPERATURE, -5.0);
        actuator.setImpact(SENSOR_TYPE_HUMIDITY, -10.0);
        return actuator;
    }

    
    /**
     * Create a typical fan-actuator.
     *
     * @param nodeId ID of the node to which this actuator will be connected
     * @return The fan actuator
     */
    public static Actuator createFan(int nodeId) {
        Actuator actuator = new Actuator(ACTUATOR_TYPE_FAN, nodeId);
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
        Actuator actuator = new Actuator(ACTUATOR_TYPE_HEATER, nodeId);
        actuator.setImpact(SENSOR_TYPE_TEMPERATURE, 4.0);
        return actuator;
    }
}