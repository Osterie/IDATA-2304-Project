package no.ntnu.messages;

import java.util.HashMap;

public class CommandTranslator {

    private HashMap<String, Command> commandMap;

    /**
     * Initializes a command translator
     */
    public CommandTranslator() {

        this.commandMap = new HashMap<>();
        // TODO: Add commands
    }

    /**
     * Converts a string to a message object
     * 
     * @param string the string to convert
     * @return the message object
     */
    public Message toMessage(String string) {
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