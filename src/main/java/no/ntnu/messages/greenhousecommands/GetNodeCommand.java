package no.ntnu.messages.greenhousecommands;

import no.ntnu.constants.Endpoints;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.ActuatorCollection;
import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.responses.SuccessResponse;

public class GetNodeCommand extends GreenhouseCommand {
    

    public GetNodeCommand() {
        super("GET_NODE");
    }

    public Message execute(NodeLogic nodeLogic) {
        // Logger.info("Received request for node from server, sending response " + sender + ";" + senderID + ";" + this.nodeLogic.getId());
        
        ActuatorCollection actuators = nodeLogic.getNode().getActuators();

        // TODO send state of actuator, on/off?

        StringBuilder actuatorString = new StringBuilder();
        for (Actuator actuator : actuators) {
            actuatorString.append(";" + actuator.getType() + "_" + actuator.getId());
        }

        String resultString = actuatorString.toString();
        
        resultString = nodeLogic.getId() + resultString;
        MessageHeader header = new MessageHeader(Endpoints.CONTROL_PANEL, "0");
        SuccessResponse response = new SuccessResponse(this, resultString);
        MessageBody body = new MessageBody(response);
        return new Message(header, body);
        
    }

    @Override
    public String toProtocolString() {
        return this.getTransmissionString();
    }
}