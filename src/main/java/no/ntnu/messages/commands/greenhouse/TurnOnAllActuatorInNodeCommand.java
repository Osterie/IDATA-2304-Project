package no.ntnu.messages.commands.greenhouse;

import no.ntnu.constants.Endpoints;
import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.responses.SuccessResponse;

/**
 * Command to turn on all actuators in a node.
 */
public class TurnOnAllActuatorInNodeCommand extends GreenhouseCommand {

    public TurnOnAllActuatorInNodeCommand() {
        super("TURN_ON_ALL_ACTUATORS");
    }

    //TODO Change id to what is should be.
    @Override
    public Message execute(NodeLogic nodeLogic, MessageHeader fromHeader) {
        nodeLogic.getNode().setAllActuators(true);
        // MessageHeader header = new MessageHeader(Endpoints.CONTROL_PANEL, "0", this);

        SuccessResponse response = new SuccessResponse(this, "TURN_ON_ALL_ACTUATORS_SUCCESS");
        MessageBody body = new MessageBody(response);
        return new Message(fromHeader, body);
    }

    @Override
    public String toString() {
        return this.getTransmissionString();
    }
    
}
