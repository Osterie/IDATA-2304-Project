package no.ntnu.messages.commands;

import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.messages.Filler;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;

/**
 * Represents a general command
 *
 * excecuteCommand: Abstract method used for carrying out command
 */
public abstract class Command {

    protected MessageBody body;
    
    public Command(){
        this.body = null;
    }

    public Command(MessageBody body){
        this.body = body;
    }

    public void setBody(MessageBody body){
        this.body = body;
    }

    /**
     * Abstract method for executing command
     */
    public abstract Message execute(NodeLogic logic);
    // TODO: Fill in with the right logic class
    /**
     * Abstract method for converting command to string
     */
    public abstract String toProtocolString();
}
