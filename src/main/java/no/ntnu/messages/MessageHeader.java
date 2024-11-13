package no.ntnu.messages;

import no.ntnu.Clients;

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

    // TODO refactor me.
    public void setId(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
        if (id.equalsIgnoreCase("ALL")){
            this.id = id;
        }
        else{
            try{
                Integer idInt = Integer.parseInt(id);
                this.id = id;
            }
            catch (NumberFormatException e){
                throw new IllegalArgumentException("ID must be an integer");
            }
        }
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

    // TODO make better. CHeck if Clients.fromString is possible and such.
    // Parse from protocol string
    public static MessageHeader fromProtocolString(String protocolString) {
        String[] parts = protocolString.split(FIELD_DELIMITER);
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid header format");
        }
        if (parts.length == 2) {
            return new MessageHeader(Clients.fromString(parts[0]), parts[1]);
        }
        return new MessageHeader(Clients.fromString(parts[0]), parts[1], parts[2]);
    }
}
