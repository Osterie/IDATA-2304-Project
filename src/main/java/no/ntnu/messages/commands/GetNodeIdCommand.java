package no.ntnu.messages.commands;

import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.messages.ErrorMessage;
import no.ntnu.messages.Filler;
import no.ntnu.messages.Message;
import no.ntnu.messages.ResponseMessage;

public class GetNodeIdCommand extends Command {

    public GetNodeIdCommand() {
    }

    @Override
    public Message execute(NodeLogic logic) {
        // empty
        return null;
    }

    @Override
    public String toProtocolString() {
        return "GetNodeIdCommand";
    }
}
