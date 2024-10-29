package no.ntnu.intermediaryserver;

public class ClientIdentification {
    private String clientType;
    private String clientId;

    public ClientIdentification(String clientType, String clientId) {
        this.clientType = clientType;
        this.clientId = clientId;
    }

    public String getClientType() {
        return clientType;
    }

    public String getClientId() {
        return clientId;
    }
}
