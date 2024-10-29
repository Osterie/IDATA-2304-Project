package no.ntnu.messages;

public enum Delimiters {
    
    HEADER_BODY_DELIMITER("-"),
    HEADER_DELIMITER(";"),
    BODY_DELIMITER(HEADER_DELIMITER.getValue()),;

    private final String value;

    Delimiters(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Delimiters fromString(String target) {
        for (Delimiters client : Delimiters.values()) {
            if (client.getValue().equalsIgnoreCase(target)) {
                return client;
            }
        }
        return null; // or throw an IllegalArgumentException if appropriate
    }
}
