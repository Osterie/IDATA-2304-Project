package no.ntnu.messages.greenhousecommands;

import no.ntnu.constants.Endpoints;
import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.responses.SuccessResponse;

public class GetSensorDataCommand extends GreenhouseCommand {
    public GetSensorDataCommand() {
        super("GET_SENSOR_DATA");
    }

    @Override
    public Message execute(NodeLogic nodeLogic) {
        // TODO change id.
        MessageHeader header = new MessageHeader(Endpoints.CONTROL_PANEL, "0", this.toProtocolString());

        String sensorData = nodeLogic.getSensorData();
        SuccessResponse response = new SuccessResponse(this, sensorData);
        MessageBody body = new MessageBody(response);
        return new Message(header, body);
    }

    @Override
    public String toProtocolString() {
        return this.getTransmissionString();
    }
}
