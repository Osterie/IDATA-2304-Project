package no.ntnu.messages;

import no.ntnu.constants.Endpoints;

/**
 * Represents the header of a message.
 * The header contains metadata about the message, including:
 * <ul>
 *     <li>The receiver of the message (an {@link Endpoints} object).</li>
 *     <li>The ID of the receiver (e.g., a specific target identifier or "ALL").</li>
 *     <li>The data type of the message, if applicable.</li>
 * </ul>
 * This class provides methods to convert between protocol string representations and objects.
 */
public class MessageHeader {
    private static final String FIELD_DELIMITER = Delimiters.HEADER_FIELD.getValue();
    private Endpoints receiver;  // The receiver of the message
    private String id;           // The ID of the receiver
    private String dataType;     // Optional data type of the message

    /**
     * Constructs a MessageHeader with all fields.
     *
     * @param receiver The receiver endpoint. Must not be null.
     * @param id       The receiver's ID. Must not be null or empty.
     * @param dataType The type of data being sent. Can be empty but not null.
     */
    public MessageHeader(Endpoints receiver, String id, String dataType) {
        this.setReceiver(receiver);
        this.setId(id);
        this.setDataType(dataType);
    }

    /**
     * Constructs a MessageHeader without a data type.
     *
     * @param receiver The receiver endpoint. Must not be null.
     * @param id       The receiver's ID. Must not be null or empty.
     */
    public MessageHeader(Endpoints receiver, String id) {
        this.setReceiver(receiver);
        this.setId(id);
        this.dataType = ""; // Default to an empty data type
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
     *           Accepts the special value "ALL".
     */
    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
        if (id.equalsIgnoreCase("ALL")) {
            this.id = id;
        } else {
            this.id = id;
        }
    }

    /**
     * Gets the data type of the message.
     *
     * @return The data type of the message.
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * Sets the data type of the message.
     *
     * @param dataType The data type to set. Must not be null.
     */
    public void setDataType(String dataType) {
        if (dataType == null) {
            throw new IllegalArgumentException("Data type cannot be null");
        }
        this.dataType = dataType;
    }

    /**
     * Converts this header to its protocol string representation.
     * The format is: `receiver_id[FIELD_DELIMITER]target_id[FIELD_DELIMITER]data_type`
     * or `receiver_id[FIELD_DELIMITER]target_id` if the data type is empty.
     *
     * @return The protocol string representation of the header.
     * @throws IllegalArgumentException If any required field is null.
     */
    public String toProtocolString() {
        if (receiver == null || id == null) {
            throw new IllegalArgumentException("Receiver and ID cannot be null");
        }
        if (dataType.isEmpty()) {
            return String.join(FIELD_DELIMITER, receiver.getValue(), id);
        } else {
            return String.join(FIELD_DELIMITER, receiver.getValue(), id, dataType);
        }
    }

    /**
     * Parses a MessageHeader from its protocol string representation.
     *
     * @param protocolString The protocol string representing a header.
     * @return The parsed {@link MessageHeader} object.
     * @throws IllegalArgumentException If the protocol string is invalid or malformed.
     */
    public static MessageHeader fromProtocolString(String protocolString) {
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
        } else {
            String optionalField = parts[2];
            return new MessageHeader(clientType, targetId, optionalField);
        }
    }
}