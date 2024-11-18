package no.ntnu.messages.commands;

import java.util.HashMap;

import no.ntnu.messages.Delimiters;
import no.ntnu.messages.Message;
import no.ntnu.messages.greenhousecommands.ActuatorChangeCommand;
import no.ntnu.messages.greenhousecommands.GetNodeCommand;
import no.ntnu.messages.greenhousecommands.GetNodeIdCommand;
import no.ntnu.messages.greenhousecommands.GetSensorDataCommand;
import no.ntnu.tools.Logger;

public class CommandTranslator {

    private HashMap<String, Command> commandMap;

    /**
     * Initializes a command translator
     */
    public CommandTranslator() {

        this.commandMap = new HashMap<>();
        this.commandMap.put(new GetNodeIdCommand().getCommandString(), new GetNodeIdCommand());
        this.commandMap.put(new GetNodeCommand().getCommandString(), new GetNodeCommand());
        this.commandMap.put(new ActuatorChangeCommand().getCommandString(), new ActuatorChangeCommand());
        this.commandMap.put(new GetSensorDataCommand().getCommandString(), new GetSensorDataCommand());
        this.commandMap.put(new ClientIdentificationCommand().getCommandString(), new ClientIdentificationCommand());

        // TODO: Add commands
    }

    /**
     * Converts a string to a message object
     * 
     * @param string the string to convert
     * @return the message object
     */
    public Command toCommand(String string) {

        Logger.info("Converting string to command: " + string);
        String[] parts = string.split(Delimiters.BODY_PARAMETERS_DELIMITER.getValue(), 2);
        String commandString = parts[0];
        
        Command command = null;
        if (this.commandMap.containsKey(commandString)) {
            command = this.commandMap.get(commandString);
            if (command instanceof Parameters) {
                String[] parameters = parts[1].split(Delimiters.BODY_PARAMETERS_DELIMITER.getValue());
                ((Parameters) command).setParameters(parameters);
            }
        }
        else{
            Logger.error("Command not found: " + commandString);
        }
        return command;
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