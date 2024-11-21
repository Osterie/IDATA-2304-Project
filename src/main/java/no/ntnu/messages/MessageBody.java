package no.ntnu.messages;

import no.ntnu.messages.commands.TransmissionTranslator;
import no.ntnu.messages.commands.Command;

/**
 * Represents the body of a message.
 * The body consists of a transmission and its associated data.
 * It serves as the content of the message, providing specific instructions or information.
 */
public class MessageBody {
    private static final String FIELD_DELIMITER = Delimiters.BODY_FIELD.getValue();
    // The transmission associated with this message body
    private Transmission transmission;
    // Optional additional data for the transmission
    private String data;

    /**
     * Constructs a MessageBody with both a transmission and data.
     *
     * @param transmission The transmission to include in the message body. Must not be null.
     * @param data    The data associated with the transmission. Can be empty or null.
     */
    public MessageBody(Transmission transmission, String data) {
        this.transmission = transmission;
        this.data = data;
    }

    /**
     * Constructs a MessageBody with only a transmission.
     *
     * @param transmission The transmission to include in the message body. Must not be null.
     */
    public MessageBody(Transmission transmission) {
        this.transmission = transmission;
        this.data = "";
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
     * Gets the data associated with the transmission.
     *
     * @return The data.
     */
    public String getData() {
        return data;
    }

    /**
     * Sets the data associated with the transmission.
     *
     * @param data The data to set. Can be null or empty.
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Converts this message body to its protocol string representation.
     * The format is: `transmission[FIELD_DELIMITER]data` or `transmission` if data is null or empty.
     *
     * @return The protocol string representation of the message body.
     */
    public String toProtocolString() {
        if (data == null || data.isEmpty()) {
            return transmission.toProtocolString();
        } else {
            return String.join(FIELD_DELIMITER, transmission.toProtocolString(), data);
        }
    }

    /**
     * Parses a MessageBody from its protocol string representation.
     *
     * @param protocolString The protocol string to parse. Expected format: `transmission[FIELD_DELIMITER]data`.
     * @return The parsed {@link MessageBody} object.
     * @throws IllegalArgumentException If the protocol string is invalid or malformed.
     */
    public static MessageBody fromProtocolString(String protocolString) {
        String[] parts = protocolString.split(Delimiters.BODY_FIELD.getValue(), 2);
        if (parts.length < 1) {
            throw new IllegalArgumentException("Invalid message format");
        }

        TransmissionTranslator transmissionTranslator = new TransmissionTranslator();
        Transmission transmission = transmissionTranslator.toTransmission(parts[0]);
        String data = parts.length > 1 ? parts[1] : ""; // Use empty string if data is not provided
        return new MessageBody(transmission, data);
    }
}
