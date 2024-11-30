package no.ntnu.messages.commands.greenhouse;

import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.responses.SuccessResponse;
import no.ntnu.tools.Logger;

public class GetNodeIdCommand extends GreenhouseCommand {

    /**
     * Creates a new instance of this command.
     */
    public GetNodeIdCommand() {
        super("GET_NODE_ID");
    }

    /**
     * Executes the command to get the node ID.
     * 
     * @param logic      The node logic to execute the command on.
     * @param fromHeader The header of the message that triggered this command.
     * @return A message containing the node ID.
     */
    @Override
    public Message execute(NodeLogic logic, MessageHeader fromHeader) {
        Logger.info("Received request for node ID from server, sending response " + logic.getId());
        SuccessResponse response = new SuccessResponse(this, String.valueOf(logic.getId()));
        MessageBody body = new MessageBody(response);
        return new Message(fromHeader, body);
    }

    /**
     * Converts the command to a string which follows transmission protocol.
     * 
     * @return The command as a string following the transmission protocol.
     */
    @Override
    public String toString() {
        return this.getTransmissionString();
    }
}
