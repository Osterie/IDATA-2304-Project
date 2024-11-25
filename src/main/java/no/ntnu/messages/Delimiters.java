package no.ntnu.messages;

/**
 * Enum representing various delimiters used in message formatting and parsing.
 * These delimiters are essential for separating different components of a message,
 * such as headers, body, and body parameters.
 */
public enum Delimiters {

    /** Delimiter between the header and body of a message. */
    HEADER_BODY("-"),

    /** Delimiter between fields in the header of a message. */
    HEADER_FIELD(";"),

    /** Delimiter between fields in the body of a message, defaulting to the header delimiter. */
    BODY_FIELD(HEADER_FIELD.getValue()),

    /** Delimiter between a fields parameters in the body of a message. */
    BODY_FIELD_PARAMETERS(",");

    private final String value;

    /**
     * Constructs a delimiter with the specified string value.
     *
     * @param value The string representation of the delimiter.
     */
    Delimiters(String value) {
        this.value = value;
    }

    /**
     * Gets the string value of the delimiter.
     *
     * @return The string value of the delimiter.
     */
    public String getValue() {
        return value;
    }

    /**
     * Retrieves a {@link Delimiters} instance based on its string value.
     *
     * @param target The string value to match.
     * @return The matching {@link Delimiters} instance, or {@code null} if no match is found.
     * @throws IllegalArgumentException If {@code target} is null or empty.
     */
    public static Delimiters fromString(String target) {
        if (target == null || target.isEmpty()) {
            throw new IllegalArgumentException("Target string cannot be null or empty.");
        }
        for (Delimiters delimiter : Delimiters.values()) {
            if (delimiter.getValue().equalsIgnoreCase(target)) {
                return delimiter;
            }
        }
        return null;
    }
}
