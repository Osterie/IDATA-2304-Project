package no.ntnu.constants;

public enum Endpoints {
    
    // Clients
    CONTROL_PANEL("CONTROL_PANEL"), // Used for the receiver in header of a Packet
    GREENHOUSE("GREENHOUSE"), // Used for the receiver in header of a Packet
    SERVER("SERVER"), // Used for the receiver in header of a Packet
    
    // Ids
    BROADCAST("BROADCAST"), // Used for the ID in header of a Packet, sends to all clients, no matter the actual ID.
    NOT_PREDEFINED("?"), // Used for the ID in header of a Packet, a server will assign an ID to the client.

    NONE("NONE");

    private final String value;

    Endpoints(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Endpoints fromString(String target) {

        if (target == null) {
            return null;
        }

        for (Endpoints client : Endpoints.values()) {
            if (client.getValue().equalsIgnoreCase(target)) {
                return client;
            }
        }
        return null; // or throw an IllegalArgumentException if appropriate
    }
}
