package no.ntnu.greenhouse;

import java.util.List;

import no.ntnu.greenhouse.sensors.Sensor;
import no.ntnu.greenhouse.sensors.SensorReading;
import no.ntnu.messages.Message;

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

    public int getId() {
        return this.node.getId();
    }

    public String getSensorData(){

        List<Sensor> sensors = this.node.getSensors();
        String sensorData = this.node.getId() + ";";
        for (Sensor sensor : sensors) {
            try {
                SensorReading reading = sensor.getReading();
                String sensorType = sensor.getDataFormat();
                sensorData += sensorType + ":" + reading.getFormatted() + ",";
            } catch (IllegalStateException e) {
                if (e.getMessage().equals("The image-sensor is off.")) {
                    sensorData += "IMG: =  NoImage,";
                } else if (e.getMessage().equals("The audio-sensor is off.")) {
                    sensorData += "AUD: =  NoAudio,";
                }else {
                    sensorData += "NUM: =  NoData,";
            }
            }
        }
        sensorData = sensorData.substring(0, sensorData.length() - 1);

        return sensorData;
    }
}
