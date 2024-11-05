package no.ntnu.intermediaryserver;

import no.ntnu.Clients;

/**
 * Represents a client identification containing the type and ID of a client.
 */
public class ClientIdentification {
    private Clients clientType;
    private String clientId;

    /**
     * Creates a ClientIdentification object with the specified type and ID.
     *
     * @param clientType the type of the client as a String (must match a Clients enum value)
     * @param clientId the unique ID of the client
     * @throws UnknownClientException if the clientType is null, empty, or invalid
     */
    public ClientIdentification(String clientType, String clientId) {
        if (clientType == null || clientType.trim().isEmpty()) {
            throw new UnknownClientException("clientType cannot be null or empty");
        }
        if (clientId == null || clientId.trim().isEmpty()) {
            throw new UnknownClientException("clientId cannot be null or empty");
        }

        // Convert the clientType string to a Clients enum value
        this.clientType = Clients.fromString(clientType);
        if (this.clientType == null) {
            throw new UnknownClientException("Invalid client type: " + clientType);
        }

        this.clientId = clientId;
    }

    /**
     * Retrieves the client type as a Clients enum value.
     *
     * @return the client type
     */
    public Clients getClientType() {
        return clientType;
    }

    /**
     * Retrieves the unique client ID.
     *
     * @return the client ID
     */
    public String getClientId() {
        return clientId;
    }
}