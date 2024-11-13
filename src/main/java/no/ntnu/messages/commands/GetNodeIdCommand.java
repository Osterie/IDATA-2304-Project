package no.ntnu.messages.commands;

import no.ntnu.Clients;
import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.tools.Logger;

public class GetNodeIdCommand extends Command {

    public GetNodeIdCommand(MessageBody body) {
        super(body);
    }

    @Override
    public Message execute(NodeLogic logic) {
        Logger.info("Received request for node ID from server, sending response " + logic.getId());

        // TODO should not be 0, how to know what though?
        MessageHeader header = new MessageHeader(Clients.CONTROL_PANEL, "0", this.toProtocolString());
        // MessageBody body = new MessageBody(this, String.valueOf(logic.getId()));
        MessageBody body = new MessageBody(this.toProtocolString(), String.valueOf(logic.getId()));
        return new Message(header, body);
    }

    @Override
    public String toProtocolString() {
        return "GET_NODE_ID";
    }
}
