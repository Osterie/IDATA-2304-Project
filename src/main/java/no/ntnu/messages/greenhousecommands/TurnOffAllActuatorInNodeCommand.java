package no.ntnu.messages.greenhousecommands;

import no.ntnu.constants.Endpoints;
import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.responses.Response;
import no.ntnu.messages.responses.SuccessResponse;

/**
 * Command to turn on all actuators in a node.
 */
public class TurnOffAllActuatorInNodeCommand extends GreenhouseCommand {

    public TurnOffAllActuatorInNodeCommand() {
        super("TURN_OFF_ALL_ACTUATORS");
    }

    //TODO Change id to what is should be.
    @Override
    public Response execute(NodeLogic nodeLogic) {
        nodeLogic.getNode().setAllActuators(false);
        // MessageHeader header = new MessageHeader(Endpoints.CONTROL_PANEL, "0", this.toProtocolString());
        // MessageBody response = new MessageBody(this, "TURN_OFF_ALL_ACTUATORS_SUCCESS");
        // return new Message(header, response);
        SuccessResponse response = new SuccessResponse(this, "TURN_OFF_ALL_ACTUATORS_SUCCESS");
        return response;
    }

    @Override
    public String toProtocolString() {
        return this.getTransmissionString();
    }
    
}
