package no.ntnu.intermediaryserver;

import no.ntnu.Endpoints;
import no.ntnu.tools.Logger;

/**
 * The ClientIdentifier class is responsible for identifying a client by its type and unique ID.
 * It parses a client identification string to set both a valid client type and client ID.
 */
public class ClientIdentifier {

    //Enum representing the client type, such as Clients.CONTROL_PANEL or Clients.GREENHOUSE
    private Endpoints clientType;

    //Unique ID for the greenhouse node or control panel
    private String clientId;

    /**
     * Default constructor for ClientIdentifier.
     * Initializes an empty client identifier.
     */
    public ClientIdentifier() {
        // Empty
    }

    /**
     * Parses and identifies the client type and ID from the given identification string.
     * The string should be formatted as "clientType;clientId".
     *
     * @param identification the identification string in "clientType;clientId" format
     * @throws IllegalArgumentException if the identification string format is invalid
     *                                  or if clientType is not a recognized type
     */
    public void identifyClientType(String identification) {
        if (identification == null || identification.trim().isEmpty()) {
            Logger.error("Identification string cannot be null or empty");
            throw new IllegalArgumentException("Identification string cannot be null or empty");
        }
        try {
            String[] parts = this.identifyParts(identification);
            this.setClientType(parts[0]);
            this.setClientId(parts[1]);
        } catch (IllegalArgumentException e) {
            Logger.error("Failed to identify client: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves the identified client type.
     *
     * @return the client type as an instance of the Clients enum
     */
    public Endpoints getClientType() {
        return this.clientType;
    }

    /**
     * Retrieves the unique client ID.
     *
     * @return the client ID as a String
     */
    public String getClientId() {
        return this.clientId;
    }

    /**
     * Sets the client type after verifying it against known Clients values.
     *
     * @param identifiedClientType the client type as a String
     * @throws IllegalArgumentException if the client type is invalid
     */
    private void setClientType(String identifiedClientType) {
        if (!this.isValidClientType(identifiedClientType)) {
            Logger.error("Invalid client type: " + identifiedClientType);
            throw new IllegalArgumentException("Invalid client type: " + identifiedClientType);
        }
        this.clientType = Endpoints.fromString(identifiedClientType);
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
     * Parses the identification string to separate client type and client ID.
     *
     * @param identification the identification string in "clientType;clientId" format
     * @return a String array containing clientType at index 0 and clientId at index 1
     * @throws IllegalArgumentException if the identification string does not match
     *                                  the expected format
     */
    private String[] identifyParts(String identification) {
        String[] parts = identification.split(";");
        if (parts.length != 2) {
            Logger.error("Invalid identification message: " + identification);
            throw new IllegalArgumentException("Invalid identification message: " + identification);
        }
        return parts;
    }

    /**
     * Checks if the given client type is a valid Clients enum value.
     *
     * @param identifiedClientType the client type as a String
     * @return true if the client type is valid, false otherwise
     */
    private boolean isValidClientType(String identifiedClientType) {
        boolean isValid = false;
        if (Endpoints.fromString(identifiedClientType) != null) {
            Logger.info("Recognized client type: " + identifiedClientType);
            isValid = true;
        }
        return isValid;
    }
}