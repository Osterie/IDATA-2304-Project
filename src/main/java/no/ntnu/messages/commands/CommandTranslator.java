package no.ntnu.messages.commands;

import java.util.HashMap;

import no.ntnu.messages.Message;

public class CommandTranslator {

    private HashMap<String, Command> commandMap;

    /**
     * Initializes a command translator
     */
    public CommandTranslator() {

        this.commandMap = new HashMap<>();
        this.commandMap.put(new GetNodeIdCommand(null).toProtocolString(), new GetNodeIdCommand(null));
        this.commandMap.put(new GetNodeCommand(null).toProtocolString(), new GetNodeCommand(null));
        this.commandMap.put(new ActuatorChangeCommand(null, "", false).toProtocolString(), new ActuatorChangeCommand(null, "", false));

        // TODO: Add commands
    }

    /**
     * Converts a string to a message object
     * 
     * @param string the string to convert
     * @return the message object
     */
    public Command toCommand(String string) {
        if (this.commandMap.containsKey(string)) {
            return this.commandMap.get(string);
        }
        return null;
    }

    /**
     * Converts a message object to a string
     * 
     * @param message the message object to convert
     * @return the string
     */
    public String toString(Message message) {
        return message.toString();
    }
}