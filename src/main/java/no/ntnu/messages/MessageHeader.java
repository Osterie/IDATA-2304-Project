package no.ntnu.messages;

import no.ntnu.constants.Endpoints;
import no.ntnu.tools.Logger;

/**
 * Represents the header of a message.
 * The header contains metadata about the message, including:
 * <ul>
 *     <li>The receiver of the message (an {@link Endpoints} object).</li>
 *     <li>The ID of the receiver (e.g., a specific target identifier or "BRODACAST").</li>
 * </ul>
 */
public class MessageHeader {

    private static final String FIELD_DELIMITER = Delimiters.HEADER_FIELD.getValue();
    private Endpoints receiver;  // The receiver of the message
    private String id;           // The ID of the receiver

    /**
     * Constructs a MessageHeader
     *
     * @param receiver The receiver endpoint. Must not be null.
     * @param id       The receiver's ID. Must not be null or empty.
     */
    public MessageHeader(Endpoints receiver, String id) {
        this.setReceiver(receiver);
        this.setId(id);
    }

    /**
     * Gets the receiver endpoint.
     *
     * @return The receiver of the message.
     */
    public Endpoints getReceiver() {
        return receiver;
    }

    /**
     * Sets the receiver endpoint.
     *
     * @param receiver The receiver to set. Must not be null.
     */
    public void setReceiver(Endpoints receiver) {
        if (receiver == null) {
            throw new IllegalArgumentException("Receiver cannot be null");
        }
        this.receiver = receiver;
    }

    /**
     * Gets the receiver's ID.
     *
     * @return The receiver's ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the receiver's ID.
     *
     * @param id The ID to set. Must not be null or empty.
     *
     */
    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
        this.id = id;
    }

    /**
     * Parses a MessageHeader from its protocol string representation.
     *
     * @param protocolString The protocol string representing a header.
     * @return The parsed {@link MessageHeader} object.
     * @throws IllegalArgumentException If the protocol string is invalid or malformed.
     */
    public static MessageHeader fromString(String protocolString) throws IllegalArgumentException{
        // TODO refactor
        if (protocolString == null || protocolString.trim().isEmpty()) {
            throw new IllegalArgumentException("Protocol string cannot be null or empty");
        }

        String[] parts = protocolString.split(FIELD_DELIMITER);
        if (parts.length < 2 || parts.length > 3) {
            throw new IllegalArgumentException("Invalid header format. Expected 2 or 3 parts separated by '" + FIELD_DELIMITER + "'");
        }

        Endpoints clientType = Endpoints.fromString(parts[0]);
        if (clientType == null) {
            throw new IllegalArgumentException("Unknown client type: " + parts[0]);
        }

        String targetId = parts[1];

        if (parts.length == 2) {
            return new MessageHeader(clientType, targetId);
        } 
        else{
            Logger.error("Invalid header format. Expected 2 or 3 parts separated by '" + FIELD_DELIMITER + "'");
            return null;
        }
    }

    /**
     * Converts this header to its protocol string representation.
     * The format is: `receiver_id[FIELD_DELIMITER]target_id`
     * For example 'GREENHOUSE;1'
     *
     * @return The protocol string representation of the header.
     * @throws IllegalArgumentException If any required field is null.
     */
    @Override
    public String toString() throws IllegalArgumentException {
        if (receiver == null || id == null) {
            throw new IllegalArgumentException("Receiver and ID cannot be null");
        }
        return String.join(FIELD_DELIMITER, receiver.getValue(), id);
    }
}