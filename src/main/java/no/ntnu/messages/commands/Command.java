package no.ntnu.messages.commands;

import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.messages.Message;

/**
 * Represents a general command
 *
 * excecuteCommand: Abstract method used for carrying out command
 */
public abstract class Command {

    public Command(String commandString) {
        this.setCommandString(commandString);
    }

    protected String commandString;

    public String getCommandString(){
        return this.commandString;
    }

    private void setCommandString(String commandString){
        this.commandString = commandString;
    }

    // /**
    //  * Abstract method for executing command
    //  */
    // public abstract Message execute(NodeLogic logic);

    /**
     * Abstract method for converting command to string
     */
    public abstract String toProtocolString();
}
