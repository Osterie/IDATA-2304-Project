package no.ntnu.messages;

class MessageHeader {
    private static final String FIELD_DELIMITER = Delimiters.HEADER_DELIMITER.getValue();
    private String receiver;
    private String id;
    private String dataType;

    // Constructor
    public MessageHeader(String receiver, String id, String dataType) {
        this.receiver = receiver;
        this.id = id;
        this.dataType = dataType;
    }

    public MessageHeader(String receiver, String id) {
        this.receiver = receiver;
        this.id = id;
        this.dataType = "";
    }

    // Setters and Getters
    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
        if (dataType.isEmpty()) {
            result = String.join(FIELD_DELIMITER, receiver, id);
        }
        else {
            result = String.join(FIELD_DELIMITER, receiver, id, dataType);
        }
        return result;
    }

    // Parse from protocol string
    public static MessageHeader fromProtocolString(String protocolString) {
        String[] parts = protocolString.split(FIELD_DELIMITER);
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid header format");
        }
        return new MessageHeader(parts[0], parts[1], parts[2]);
    }
}
