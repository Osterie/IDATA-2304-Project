package no.ntnu.messages.greenhousecommands;

import no.ntnu.Endpoints;
import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;

public class GetSensorDataCommand extends GreenhouseCommand {
    public GetSensorDataCommand() {
        super("GET_SENSOR_DATA");
    }

    @Override
    public Message execute(NodeLogic nodeLogic) {
        MessageHeader header = new MessageHeader(Endpoints.CONTROL_PANEL, "0", this.toProtocolString());

        // spawner.advertiseSensorData("4;temperature=27.4 °C,temperature=26.8 °C,humidity=80 %", START_DELAY + 2);

        String sensorData = nodeLogic.getSensorData();
        MessageBody response = new MessageBody(this, sensorData);
        return new Message(header, response);
    }

    @Override
    public String toProtocolString() {
        return this.getCommandString();
    }
}
