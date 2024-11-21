package no.ntnu.messages;

/**
 * Represents the body of a message.
 * The body consists of a transmission and its associated data.
 * It serves as the content of the message, providing specific instructions or information.
 */
public class MessageBody {
    private static final String FIELD_DELIMITER = Delimiters.BODY_FIELD.getValue();
    // The transmission associated with this message body
    private Transmission transmission;

    /**
     * Constructs a MessageBody
     *
     * @param transmission The transmission to include in the message body. Must not be null.
     */
    public MessageBody(Transmission transmission) {
        this.transmission = transmission;
    }

    /**
     * Gets the transmission of the message body.
     *
     * @return The transmission.
     */
    public Transmission getTransmission() {
        return this.transmission;
    }

    /**
     * Sets the transmission of the message body.
     *
     * @param transmission The transmission to set. Must not be null.
     */
    public void setTransmission(Transmission transmission) {
        if (transmission == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }
        this.transmission = transmission;
    }

    /**
     * Converts this message body to its protocol string representation.
     * The format is: `transmission[FIELD_DELIMITER]data` or `transmission` if data is null or empty.
     *
     * @return The protocol string representation of the message body.
     */
    public String toProtocolString() {
        return transmission.toProtocolString();
    }

    /**
     * Parses a MessageBody from its protocol string representation.
     *
     * @param protocolString The protocol string to parse. Expected format: `transmission[FIELD_DELIMITER]data`.
     * @return The parsed {@link MessageBody} object.
     * @throws IllegalArgumentException If the protocol string is invalid or malformed.
     */
    public static MessageBody fromProtocolString(String protocolString) {
        // String[] parts = protocolString.split(FIELD_DELIMITER, 2);
        // if (parts.length < 1) {
        //     throw new IllegalArgumentException("Invalid message format");
        // }

        TransmissionTranslator transmissionTranslator = new TransmissionTranslator();
        Transmission transmission = transmissionTranslator.toTransmission(protocolString);
        // String data = parts.length > 1 ? parts[1] : ""; // Use empty string if data is not provided
        return new MessageBody(transmission);
    }
}
