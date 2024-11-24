package no.ntnu.messages.commands.greenhouse;

import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.commands.Command;

/**
 * Represents a general command
 *
 * excecuteCommand: Abstract method used for carrying out command
 */
public abstract class GreenhouseCommand extends Command {

    public GreenhouseCommand(String commandString) {
        super(commandString);
    }

    /**
     * Abstract method for executing command
     */
    public abstract Message execute(NodeLogic logic, MessageHeader fromHeader);
}