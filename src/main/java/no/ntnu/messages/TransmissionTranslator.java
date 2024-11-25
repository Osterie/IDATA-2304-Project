package no.ntnu.messages;

import java.util.HashMap;

import no.ntnu.messages.commands.Command;
import no.ntnu.messages.commands.Parameters;
import no.ntnu.messages.commands.common.ClientIdentificationTransmission;
import no.ntnu.messages.commands.greenhouse.ActuatorChangeCommand;
import no.ntnu.messages.commands.greenhouse.GetNodeCommand;
import no.ntnu.messages.commands.greenhouse.GetNodeIdCommand;
import no.ntnu.messages.commands.greenhouse.GetSensorDataCommand;
import no.ntnu.messages.commands.greenhouse.TurnOffAllActuatorInNodeCommand;
import no.ntnu.messages.commands.greenhouse.TurnOnAllActuatorInNodeCommand;
import no.ntnu.messages.responses.FailureResponse;
import no.ntnu.messages.responses.Response;
import no.ntnu.messages.responses.SuccessResponse;
import no.ntnu.tools.Logger;

public class TransmissionTranslator {

    private HashMap<String, Transmission> transmissionMap;

    /**
     * Initializes a command translator
     */
    public TransmissionTranslator() {

        this.transmissionMap = new HashMap<>();

        // Transmissions
        this.transmissionMap.put(new ClientIdentificationTransmission().getTransmissionString(), new ClientIdentificationTransmission());
        
        // Commands
        this.transmissionMap.put(new ActuatorChangeCommand().getTransmissionString(), new ActuatorChangeCommand());
        this.transmissionMap.put(new GetNodeIdCommand().getTransmissionString(), new GetNodeIdCommand());
        this.transmissionMap.put(new GetNodeCommand().getTransmissionString(), new GetNodeCommand());
        this.transmissionMap.put(new GetSensorDataCommand().getTransmissionString(), new GetSensorDataCommand());
        this.transmissionMap.put(new TurnOnAllActuatorInNodeCommand().getTransmissionString(), new TurnOnAllActuatorInNodeCommand());
        this.transmissionMap.put(new TurnOffAllActuatorInNodeCommand().getTransmissionString(), new TurnOffAllActuatorInNodeCommand());

        
        // Responses
        this.transmissionMap.put(new FailureResponse(null, null).getTransmissionString(), new FailureResponse(null, null));
        this.transmissionMap.put(new SuccessResponse(null, null).getTransmissionString(), new SuccessResponse(null, null));

        // TODO: Add commands
    }

    /**
     * Converts a string to a message object
     * 
     * @param string the string to convert
     * @return the message object
     */
    public Transmission toTransmission(String string) {

        Logger.info("Converting string to transmission: " + string);
        String[] parts = string.split(Delimiters.BODY_FIELD_PARAMETERS.getValue(), 2);
        String transmissionType = parts[0];
        
        Transmission transmission = this.getTransmission(transmissionType);
        if (transmission == null){
            Logger.error("Transmission not found: " + transmissionType);
            return null;
        }

        if (transmission instanceof Parameters) {
            if (parts.length > 1) {
                String[] parameters = parts[1].split(Delimiters.BODY_FIELD_PARAMETERS.getValue());
                ((Parameters) transmission).setParameters(parameters);
            }
        }
        else if (transmission instanceof Response) {

            String[] parameters = parts[1].split(Delimiters.BODY_FIELD_PARAMETERS.getValue(), 2);
            String commandAsString = parameters[0];
            Command command = (Command) this.toTransmission(commandAsString);

            String responseData = parameters[1];

            ((Response) transmission).setCommand(command);
            ((Response) transmission).setResponseData(responseData);
        }

        return transmission;
    }

    private Transmission getTransmission(String transmissionType) {
        return this.transmissionMap.get(transmissionType);
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