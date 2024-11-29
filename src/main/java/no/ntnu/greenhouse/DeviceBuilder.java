package no.ntnu.greenhouse;

import no.ntnu.greenhouse.sensors.NumericSensor;

public class DeviceBuilder {
    private static final double NORMAL_GREENHOUSE_TEMPERATURE = 27;
    private static final double MIN_TEMPERATURE = 15;
    private static final double MAX_TEMPERATURE = 40;
    private static final String TEMPERATURE_UNIT = "°C";
    private static final double MIN_HUMIDITY = 50;
    private static final double MAX_HUMIDITY = 100;
    private static final double NORMAL_GREENHOUSE_HUMIDITY = 80;
    private static final String HUMIDITY_UNIT = "%";
    private static final String SENSOR_TYPE_TEMPERATURE = "temperature";
    private static final String SENSOR_TYPE_HUMIDITY = "humidity";
    private static final String SENSOR_TYPE_IMAGE = "image";
    private static final String ACTUATOR_TYPE_FAN = "fan";
    private static final String ACTUATOR_TYPE_HEATER = "heater";
    private static final String ACTUATOR_TYPE_WINDOW = "window";
    private static final String PATH_TO_IMAGES = "images/";
    private static final String PATH_TO_AUDIO = "audiofiles/";
    private static final String SENSOR_TYPE_AUDIO = "audio";
  
  
    private static int nextNodeId = 1;

    private SensorActuatorNode node;

      /**
     * Constructing the bulder is not allowed.
     */
    public DeviceBuilder() {
        this.node = new SensorActuatorNode(generateUniqueNodeId());

    }

    public DeviceBuilder addTemperatureSensor(int count){
        if (count > 0) {
            node.addSensors(DeviceFactory.createTemperatureSensor(), count);
        }
        return this;
    }

    public SensorActuatorNode build(){
        return node;
    }


    /**
   * Generate a random value within the range [x-d; x+d].
   *
   * @param x The central value
   * @param d The allowed difference range
   * @return a randomized value within the desired range
   */
    private static double randomize(double x, double d) {
        final double zeroToDoubleD = Math.random() * 2 * d;
        final double plusMinusD = zeroToDoubleD - d;
        return x + plusMinusD;
    }


    /**
     * Generate an integer that can be used as a unique ID of sensor/actuator nodes.
     *
     * @return a Unique ID for sensor/actuator nodes
     */
    private static int generateUniqueNodeId() {
        return nextNodeId++;
    }
}
