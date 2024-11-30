package no.ntnu.greenhouse;

import java.util.List;

import no.ntnu.greenhouse.sensors.Sensor;
import no.ntnu.greenhouse.sensors.SensorReading;
import no.ntnu.messages.Delimiters;

/**
 * The logic of a node in the greenhouse.
 */
public class NodeLogic {
    private final SensorActuatorNode node;

    /**
     * Create new node logic.
     *
     * @param node The node to create logic for.
     */
    public NodeLogic(SensorActuatorNode node) {
        this.node = node;
    }

    /**
     * Returns the node.
     *
     * @return The node.
     */
    public SensorActuatorNode getNode(){
        return this.node;
    }

    /**
     * Returns the ID of the node.
     *
     * @return The ID of the node.
     */
    public int getId() {
        return this.node.getId();
    }

    /**
     * Returns the sensor data of the node.
     *
     * @return The sensor data of the node.
     */
    public String getSensorData(){

        List<Sensor> sensors = this.node.getSensors();
        String sensorData = this.node.getId() + Delimiters.BODY_FIELD.getValue();

        for (Sensor sensor : sensors) {
            try {
                SensorReading reading = sensor.getReading();
                String sensorType = sensor.getDataFormat();
                sensorData += sensorType + ":" + reading.getFormatted() + Delimiters.BODY_FIELD_PARAMETERS.getValue();
            } catch (IllegalStateException e) {
                // TODO what is this
                if (e.getMessage().equals("The image-sensor is off.")) {
                    sensorData += "IMG: =  NoImage,";
                } else if (e.getMessage().equals("The audio-sensor is off.")) {
                    sensorData += "AUD: =  NoAudio,";
                }else {
                    sensorData += "NUM: =  NoData,";
                }
            }
        }
        // Remove the last comma
        sensorData = sensorData.substring(0, sensorData.length() - 1);

        return sensorData;
    }
}
