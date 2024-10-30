package no.ntnu;

public enum Clients {
    CONTROL_PANEL("CONTROL_PANEL"),
    GREENHOUSE("GREENHOUSE");



    private final String value;

    Clients(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Clients fromString(String target) {
        for (Clients client : Clients.values()) {
            if (client.getValue().equalsIgnoreCase(target)) {
                return client;
            }
        }
        return null; // or throw an IllegalArgumentException if appropriate
    }
}
