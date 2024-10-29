package no.ntnu.intermediaryserver;

import java.util.ArrayList;

import no.ntnu.Clients;

public class ClientIdentifier {
    private String clientType;  // "CONTROL_PANEL" or "GREENHOUSE" (currently)
    private String clientId;    // Unique ID for the greenhouse node or control panel

    private ArrayList<String> possibleClients;

    public ClientIdentifier() {
        this.possibleClients = new ArrayList<>();
        this.possibleClients.add(Clients.CONTROL_PANEL.getValue());
        this.possibleClients.add(Clients.GREENHOUSE.getValue());
    }

    public void identifyClientType(String identification) {
        String[] parts = this.identifyParts(identification);
        this.setClientType(parts[0]);
        this.setClientId(parts[1]);        
    }

    public String getClientType() {
        return clientType;
    }

    public String getClientId() {
        return clientId;
    }

    private void setClientType(String identifiedClientType) {
        if (!this.isValidClientType(identifiedClientType)) {
            throw new IllegalArgumentException("Invalid client type: " + identifiedClientType);
        }
        this.clientType = identifiedClientType;
    }

    private void setClientId(String clientId) {
        this.clientId = clientId;
    }

    private String[] identifyParts(String identification) {
        String[] parts = identification.split(";");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid identification message: " + identification);
        }
        return parts;
    }

    private boolean isValidClientType(String clientType) {
        return this.possibleClients.contains(clientType);
    }
}
