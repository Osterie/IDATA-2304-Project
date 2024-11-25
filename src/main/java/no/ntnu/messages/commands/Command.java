package no.ntnu.messages.commands;

import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.Transmission;

/**
 * Represents a general command
 *
 * excecuteCommand: Abstract method used for carrying out command
 */
public abstract class Command extends Transmission {

    protected Command(String commandString) {
        super(commandString);
    }

    /**
     * Abstract method for executing command
     */
    public abstract Message execute(NodeLogic logic, MessageHeader fromHeader);

    /**
     * Abstract method for converting command to string
     * ensuring that all commands implement the toString method
     */
    public abstract String toString();
}
