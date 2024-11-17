package no.ntnu.greenhouse;

import java.util.List;

import no.ntnu.greenhouse.sensors.Sensor;
import no.ntnu.greenhouse.sensors.SensorReading;
import no.ntnu.messages.Message;

public class NodeLogic {
    private final SensorActuatorNode node;

    public NodeLogic(SensorActuatorNode node) {
        this.node = node;
    }

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
