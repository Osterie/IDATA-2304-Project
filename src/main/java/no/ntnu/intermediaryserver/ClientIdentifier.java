package no.ntnu.intermediaryserver;

import no.ntnu.Clients;
import no.ntnu.tools.Logger;

public class ClientIdentifier {
    private Clients clientType;  // Clients.CONTROL_PANEL or Clients.GREENHOUSE
    private String clientId;    // Unique ID for the greenhouse node or control panel

    public ClientIdentifier() {
        // Empty
    }

    public void identifyClientType(String identification) {
        String[] parts = this.identifyParts(identification);
        this.setClientType(parts[0]);
        this.setClientId(parts[1]);        
    }

    public Clients getClientType() {
        return this.clientType;
    }

    public String getClientId() {
        return this.clientId;
    }

    private void setClientType(String identifiedClientType) {
        if (!this.isValidClientType(identifiedClientType)) {
            Logger.error("Invalid client type: " + identifiedClientType);
            throw new IllegalArgumentException("Invalid client type: " + identifiedClientType);
        }
        this.clientType = Clients.fromString(identifiedClientType);
    }

    private void setClientId(String clientId) {
        this.clientId = clientId;
    }

    private String[] identifyParts(String identification) {
        String[] parts = identification.split(";");
        if (parts.length != 2) {
            Logger.error("Invalid identification message: " + identification);
            throw new IllegalArgumentException("Invalid identification message: " + identification);
        }
        return parts;
    }

    private boolean isValidClientType(String identifiedClientType) {
        boolean isValid = false;
        if (Clients.fromString(identifiedClientType) != null) {
            Logger.info("yaaaz");
            isValid = true;
        }
        return isValid;
    }
}
