package no.ntnu.messages;

import no.ntnu.Clients;

/**
 * Represents the header of a message.
 * The header contains information about the receiver of the message, the ID of the receiver, and the data type of the message.
 */
public class MessageHeader {
    private static final String FIELD_DELIMITER = Delimiters.HEADER_DELIMITER.getValue();
    private Clients receiver;
    private String id;
    private String dataType;

    // Constructor
    public MessageHeader(Clients receiver, String id, String dataType) {
        this.setReceiver(receiver);
        this.setId(id);
        this.setDataType(dataType);
    }

    public MessageHeader(Clients receiver, String id) {
        this.setReceiver(receiver);
        this.setId(id);
        this.dataType = "";
    }

    // Setters and Getters
    public Clients getReceiver() {
        return receiver;
    }

    public void setReceiver(Clients receiver) {
        this.receiver = receiver;
    }

    public String getId() {
        return id;
    }

    // TODO: Check if new code is good: old TODO: (refactor me.)
    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }

        if (id.equalsIgnoreCase("ALL")) {
            this.id = id;
            return;
        }

        this.id = id;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    // Convert to protocol string
    public String toProtocolString() {
        
        String result = "";
        if (dataType == null) {
            throw new IllegalArgumentException("Data type cannot be null");
        }
        else if (dataType.isEmpty()) {
            result = String.join(FIELD_DELIMITER, receiver.getValue(), id);
        }
        else {
            result = String.join(FIELD_DELIMITER, receiver.getValue(), id, dataType);
        }
        return result;
    }

    // TODO: Check if new code is good: old TODO: (make better. CHeck if Clients.fromString is possible and such.)
    public static MessageHeader fromProtocolString(String protocolString) {
        if (protocolString == null || protocolString.trim().isEmpty()) {
            throw new IllegalArgumentException("Protocol string cannot be null or empty");
        }

        String[] parts = protocolString.split(FIELD_DELIMITER);
        if (parts.length < 2 || parts.length > 3) {
            throw new IllegalArgumentException("Invalid header format. Expected 2 or 3 parts separated by '" + FIELD_DELIMITER + "'");
        }

        Clients clientType = Clients.fromString(parts[0]);
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
