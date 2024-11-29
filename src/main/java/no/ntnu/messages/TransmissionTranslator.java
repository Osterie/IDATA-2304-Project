package no.ntnu.messages;

import java.util.HashMap;

import no.ntnu.messages.Transmission;
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

/**
 * A class for translating transmissions to and from strings.
 */
public class TransmissionTranslator {

    private HashMap<String, Transmission> transmissionMap;

    /**
     * Initializes a transmission translator
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
        this.transmissionMap.put(new FailureResponse().getTransmissionString(), new FailureResponse());
        this.transmissionMap.put(new SuccessResponse().getTransmissionString(), new SuccessResponse());

        // TODO: Add commands if missing
    }

    /**
     * Converts a string to a transmission object
     * 
     * @param string the string to convert
     * @return the transmission object
     */
    public Transmission toTransmission(String string) {

        this.identifyTransmissionType(string);
        // TODO refactor 
        Logger.info("Converting string to transmission: " + string);
        // String[] parts = string.split(Delimiters.BODY_FIELD.getValue(), 2);
        String[] parts = string.split(Delimiters.BODY_FIELD_PARAMETERS.getValue(), 2);
        String transmissionType = this.identifyTransmissionType(string);
        
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
            Transmission command = (Transmission) this.toTransmission(commandAsString);

            String responseData = parameters[1];

            ((Response) transmission).setTransmission(command);
            ((Response) transmission).setResponseData(responseData);
        }

        return transmission;
    }

    /**
     * Identifies the type of transmission from a string
     * 
     * @param string the string to identify
     */
    private String identifyTransmissionType(String string) {
        String[] fields = string.split(Delimiters.BODY_FIELD.getValue());
        String[] fieldWithParameters = fields[0].split(Delimiters.BODY_FIELD_PARAMETERS.getValue(), 2);
        String transmissionType = fieldWithParameters[0];
        if (!this.transmissionMap.containsKey(transmissionType)) {
            Logger.error("Transmission not found: " + transmissionType);
        }

        return transmissionType;
    }

    /**
     * Gets a transmission object from the transmission map.
     * 
     * @param transmissionType the type of transmission
     * @return the transmission object
     */
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