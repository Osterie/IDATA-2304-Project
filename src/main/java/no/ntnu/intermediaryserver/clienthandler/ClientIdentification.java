package no.ntnu.intermediaryserver.clienthandler;

import no.ntnu.constants.Endpoints;

/**
 * Represents a client identification containing the type and ID of a client.
 */
public class ClientIdentification {
    private Endpoints clientType;
    private String clientId;

    /**
     * Creates a ClientIdentification object with the specified type and ID.
     *
     * @param clientType the type of the client as a Clients enum
     * @param clientId the unique ID of the client
     * @throws UnknownClientException if the clientType is null, empty, or invalid
     */
    public ClientIdentification(Endpoints clientType, String clientId) {
        if (clientType == null) {
            throw new UnknownClientException("clientType cannot be null");
        }
        if (clientId == null || clientId.trim().isEmpty()) {
            throw new UnknownClientException("clientId cannot be null or empty");
        }

        this.setClientType(clientType);
        this.setClientId(clientId);
    }

    /**
     * Sets the client type after verifying it against known Clients values.
     *
     * @param clientType the client type as a Clients enum
     * @throws UnknownClientException if the client type is invalid
     */
    private void setClientType(Endpoints clientType) {
        if (clientType == null) {
            throw new UnknownClientException("Invalid client type: " + clientType);
        }
        this.clientType = clientType;
    }

    /**
     * Sets the client ID.
     *
     * @param clientId the unique ID for the client as a String
     */
    private void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Retrieves the type of the client.
     *
     * @return the client type as an instance of the Clients enum
     */
    public Endpoints getClientType() {
        return this.clientType;
    }

    /**
     * Retrieves the unique ID of the client.
     *
     * @return the client ID as a String
     */
    public String getClientId() {
        return this.clientId;
    }
}