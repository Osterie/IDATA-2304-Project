package no.ntnu.messages;

import java.util.HashMap;

import no.ntnu.messages.commands.Parameters;
import no.ntnu.messages.commands.common.ClientIdentificationTransmission;
import no.ntnu.messages.commands.greenhouse.*;
import no.ntnu.messages.responses.FailureResponse;
import no.ntnu.messages.responses.Response;
import no.ntnu.messages.responses.SuccessResponse;
import no.ntnu.tools.Logger;

/**
 * A class for translating transmissions to and from strings.
 */
public class TransmissionTranslator {

    private final HashMap<String, Transmission> transmissionMap;

    /**
     * Initializes a TransmissionTranslator.
     */
    public TransmissionTranslator() {
        this.transmissionMap = new HashMap<>();
        this.initializeTransmissions();
    }

    /**
     * Populates the transmission map with available transmissions.
     */
    private void initializeTransmissions() {
        // Transmissions
        this.addTransmission(new ClientIdentificationTransmission());

        // Commands
        this.addTransmission(new ActuatorChangeCommand());
        this.addTransmission(new GetNodeIdCommand());
        this.addTransmission(new GetNodeCommand());
        this.addTransmission(new GetSensorDataCommand());
        this.addTransmission(new TurnOnAllActuatorInNodeCommand());
        this.addTransmission(new TurnOffAllActuatorInNodeCommand());

        // Responses
        this.addTransmission(new FailureResponse());
        this.addTransmission(new SuccessResponse());
    }

    /**
     * Adds a transmission to the map using its transmission string as the key.
     * 
     * @param transmission the transmission to add
     */
    private void addTransmission(Transmission transmission) {
        this.transmissionMap.put(transmission.getTransmissionString(), transmission);
    }

    /**
     * Converts a string to a Transmission object.
     * 
     * @param string the string to convert
     * @return the corresponding Transmission object, or null if not found
     */
    public Transmission toTransmission(String string) {
        Logger.info("Converting string to transmission: " + string);
        String transmissionType = identifyTransmissionType(string);

        Transmission transmission = getTransmission(transmissionType);
        if (transmission == null) {
            Logger.error("Transmission not found: " + transmissionType);
            return null;
        }

        this.populateTransmission(transmission, string);
        return transmission;
    }

    /**
     * Identifies the type of transmission from a string.
     * 
     * @param string the string to analyze
     * @return the transmission type, or an empty string if not found
     */
    private String identifyTransmissionType(String string) {
        String[] fields = string.split(Delimiters.BODY_FIELD.getValue(), 2);
        String[] headerFields = fields[0].split(Delimiters.BODY_FIELD_PARAMETERS.getValue(), 2);
        return headerFields[0];
    }

    /**
     * Retrieves a Transmission object from the map by type.
     * 
     * @param transmissionType the type of transmission
     * @return the corresponding Transmission object, or null if not found
     */
    private Transmission getTransmission(String transmissionType) {
        return this.transmissionMap.get(transmissionType);
    }

    /**
     * Populates a transmission object with data from a string.
     * 
     * @param transmission the transmission to populate
     * @param string       the data string
     */
    private void populateTransmission(Transmission transmission, String string) {
        String[] parts = string.split(Delimiters.BODY_FIELD_PARAMETERS.getValue(), 2);

        if (transmission instanceof Parameters && parts.length > 1) {
            String[] parameters = parts[1].split(Delimiters.BODY_FIELD_PARAMETERS.getValue());
            ((Parameters) transmission).setParameters(parameters);
        } else if (transmission instanceof Response && parts.length > 1) {
            String[] responseParts = parts[1].split(Delimiters.BODY_FIELD_PARAMETERS.getValue(), 2);
            if (responseParts.length == 2) {
                Transmission command = toTransmission(responseParts[0]);
                ((Response) transmission).setTransmission(command);
                ((Response) transmission).setResponseData(responseParts[1]);
            }
        }
    }

    /**
     * Converts a Transmission object to a string.
     * 
     * @param transmission the transmission to convert
     * @return the resulting string
     */
    public String toString(Transmission transmission) {
        return transmission.toString();
    }
}
