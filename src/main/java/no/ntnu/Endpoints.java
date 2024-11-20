package no.ntnu;

public enum Endpoints {
    
    CONTROL_PANEL("CONTROL_PANEL"),
    GREENHOUSE("GREENHOUSE"),
    SERVER("SERVER"),
    NONE("NONE");

    private final String value;

    Endpoints(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Endpoints fromString(String target) {
        for (Endpoints client : Endpoints.values()) {
            if (client.getValue().equalsIgnoreCase(target)) {
                return client;
            }
        }
        return null; // or throw an IllegalArgumentException if appropriate
    }
}
