package no.ntnu.messages.commands.greenhouse;

import no.ntnu.constants.Endpoints;
import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.responses.SuccessResponse;
import no.ntnu.tools.Logger;

public class GetNodeIdCommand extends GreenhouseCommand {

    public GetNodeIdCommand() {
        super("GET_NODE_ID");
    }

    @Override
    public Message execute(NodeLogic logic, MessageHeader fromHeader) {
        Logger.info("Received request for node ID from server, sending response " + logic.getId());

        // TODO should not be 0, how to know what though?
        // MessageHeader header = new MessageHeader(Endpoints.CONTROL_PANEL, "0");
        
        SuccessResponse response = new SuccessResponse(this, String.valueOf(logic.getId()));
        MessageBody body = new MessageBody(response);
        return new Message(fromHeader, body);
    }

    @Override
    public String toString() {
        return this.getTransmissionString();
    }
}
