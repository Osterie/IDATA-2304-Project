package no.ntnu.messages.commands.common;

import no.ntnu.constants.Endpoints;
import no.ntnu.intermediaryserver.clienthandler.ClientIdentification;
import no.ntnu.messages.Delimiters;
import no.ntnu.messages.Transmission;
import no.ntnu.messages.commands.Parameters;

/**
 * Represents a transmission for client identification.
 * This is used to identify a client (e.g., control panel or greenhouse) by its
 * endpoint type
 * and unique identifier.
 */
public class ClientIdentificationTransmission extends Transmission implements Parameters {
    protected String id;
    protected Endpoints client;

    private static final String TRANSMISSION_STRING = "CLIENT_IDENTIFICATION";

    /**
     * Constructs a {@code ClientIdentificationTransmission} with the specified
     * client type and ID.
     *
     * @param client The {@link Endpoints} type representing the client.
     * @param id     The unique identifier of the client. Must not be null.
     * @throws IllegalArgumentException If the ID is null.
     */
    public ClientIdentificationTransmission(Endpoints client, String id) {
        super(TRANSMISSION_STRING);
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        this.client = client;
        this.id = id;
    }

    /**
     * Constructs a {@code ClientIdentificationTransmission} with the specified
     * client identification.
     *
     * @param clientIdentification The client identification to use.
     */
    public ClientIdentificationTransmission(ClientIdentification clientIdentification) {
        super(TRANSMISSION_STRING);
        this.client = clientIdentification.getClientType();
        this.id = clientIdentification.getClientId();
    }

    /**
     * Constructs a {@code ClientIdentificationTransmission} without initializing
     * the client or ID.
     * The fields can be set later using setters or the {@code setParameters}
     * method.
     */
    public ClientIdentificationTransmission() {
        super(TRANSMISSION_STRING);
    }

    /**
     * Gets the unique identifier of the client.
     *
     * @return The client ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the client.
     *
     * @param id The client ID to set.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the client type.
     *
     * @return The {@link Endpoints} type of the client.
     */
    public Endpoints getClient() {
        return this.client;
    }

    /**
     * Sets the client type.
     *
     * @param client The {@link Endpoints} type to set.
     */
    public void setClient(Endpoints client) {
        this.client = client;
    }

    /**
     * Sets the parameters of this transmission from an array of strings.
     *
     * @param parameters An array containing exactly two parameters: client type and
     *                   client ID.
     * @throws IllegalArgumentException If the number of parameters is not 2, if
     *                                  parameters are null,
     *                                  or if the client type is invalid.
     */
    @Override
    public void setParameters(String[] parameters) {
        if (parameters.length != 2) {
            throw new IllegalArgumentException("Invalid number of parameters: " + parameters.length);
        }
        if (parameters[0] == null || parameters[1] == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        if (Endpoints.valueOf(parameters[0]) == null) {
            throw new IllegalArgumentException("Invalid client");
        }
        this.client = Endpoints.valueOf(parameters[0]);
        this.id = parameters[1];
    }

    /**
     * Converts the transmission to its protocol string representation.
     * The format is: `TRANSMISSION_STRING | CLIENT | ID`
     *
     * @return The protocol string representation of this transmission.
     */
    @Override
    public String toString() {
        String protocolString = this.getTransmissionString();
        protocolString += Delimiters.BODY_FIELD_PARAMETERS.getValue();
        protocolString += this.client;
        protocolString += Delimiters.BODY_FIELD_PARAMETERS.getValue();
        protocolString += this.id;
        return protocolString;
    }
}