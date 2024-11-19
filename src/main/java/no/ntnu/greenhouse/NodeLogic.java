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
     * Returns the node 
     * @return
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
            SensorReading reading = sensor.getReading();
            sensorData += reading.getFormatted() + ",";
        }
        sensorData = sensorData.substring(0, sensorData.length() - 1);

        return sensorData;
    }
}
