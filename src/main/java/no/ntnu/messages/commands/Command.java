package no.ntnu.messages.commands;

import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.messages.Message;
import no.ntnu.messages.Transmission;
import no.ntnu.messages.responses.Response;

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
    public abstract Response execute(NodeLogic logic);

    // /**
    //  * Abstract method for converting command to string
    //  */
    // public abstract String toProtocolString();
}
