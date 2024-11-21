package no.ntnu.messages.commands;

import java.util.HashMap;

import no.ntnu.messages.Delimiters;
import no.ntnu.messages.Message;
import no.ntnu.messages.Transmission;
import no.ntnu.messages.greenhousecommands.ActuatorChangeCommand;
import no.ntnu.messages.greenhousecommands.GetNodeCommand;
import no.ntnu.messages.greenhousecommands.GetNodeIdCommand;
import no.ntnu.messages.greenhousecommands.GetSensorDataCommand;
import no.ntnu.tools.Logger;

public class TransmissionTranslator {

    private HashMap<String, Transmission> transmissionMap;

    /**
     * Initializes a command translator
     */
    public TransmissionTranslator() {

        this.transmissionMap = new HashMap<>();
        this.transmissionMap.put(new GetNodeIdCommand().getTransmissionString(), new GetNodeIdCommand());
        this.transmissionMap.put(new GetNodeCommand().getTransmissionString(), new GetNodeCommand());
        this.transmissionMap.put(new ActuatorChangeCommand().getTransmissionString(), new ActuatorChangeCommand());
        this.transmissionMap.put(new GetSensorDataCommand().getTransmissionString(), new GetSensorDataCommand());
        this.transmissionMap.put(new ClientIdentificationTransmission().getTransmissionString(), new ClientIdentificationTransmission());

        // TODO: Add commands
    }

    /**
     * Converts a string to a message object
     * 
     * @param string the string to convert
     * @return the message object
     */
    public Transmission toTransmission(String string) {

        Logger.info("Converting string to command: " + string);
        String[] parts = string.split(Delimiters.BODY_FIELD_PARAMETERS.getValue(), 2);
        String commandString = parts[0];
        
        Transmission command = null;
        if (this.transmissionMap.containsKey(commandString)) {
            command = this.transmissionMap.get(commandString);
            if (command instanceof Parameters) {
                String[] parameters = parts[1].split(Delimiters.BODY_FIELD_PARAMETERS.getValue());
                ((Parameters) command).setParameters(parameters);
            }
        }
        else{
            Logger.error("Command not found: " + commandString);
        }
        return command;
    }

    /**
     * Converts a transmission object to a string
     * 
     * @param transmission the transmission object to convert
     * @return the string
     */
    public String toString(Transmission transmission) {
        return transmission.toString();
    }
}