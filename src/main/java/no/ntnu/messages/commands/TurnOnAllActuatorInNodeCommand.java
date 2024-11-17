package no.ntnu.messages.commands;

import no.ntnu.Clients;
import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;

/**
 * Command to turn on all actuators in a node.
 */
public class TurnOnAllActuatorInNodeCommand extends Command {

    public TurnOnAllActuatorInNodeCommand() {
        super("TURN_ON_ALL_ACTUATORS");
    }

    //TODO Change id to what is should be.
    @Override
    public Message execute(NodeLogic nodeLogic) {
        nodeLogic.getNode().setAllActuators(true);;
        MessageHeader header = new MessageHeader(Clients.CONTROL_PANEL, "123", this.toProtocolString());
        MessageBody response = new MessageBody(this, "TURN_ON_ALL_ACTUATORS_SUCCESS");
        return new Message(header, response);
    }

    @Override
    public String toProtocolString() {
        return this.getCommandString();
    }
    
}
