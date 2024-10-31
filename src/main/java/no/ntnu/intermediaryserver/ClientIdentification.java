package no.ntnu.intermediaryserver;

public class ClientIdentification {
    private String clientType;
    private String clientId;

    public ClientIdentification(String clientType, String clientId) {
        if (clientType == null || clientType.trim().isEmpty()) {
            throw new IllegalArgumentException("clientType cannot be null or empty");
        }
        if (clientId == null || clientId.trim().isEmpty()) {
            throw new IllegalArgumentException("clientId cannot be null or empty");
        }

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
