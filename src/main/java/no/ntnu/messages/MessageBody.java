package no.ntnu.messages;

public class MessageBody {
    private static final String FIELD_DELIMITER = Delimiters.BODY_DELIMITER.getValue();
    private String command;
    private String data;

    // Constructor
    public MessageBody(String command, String data) {
        this.command = command;
        this.data = data;
    }

    public MessageBody(String command) {
        this.command = command;
        this.data = "";
    }

    // Setters and Getters
    public String getCommand() {
        return this.command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    // Convert to protocol string
    public String toProtocolString() {

        String result = "";
        if (data == null) {
            result = command;
        }
        else if (data.isEmpty()) {
            result = command;
        } else {
            result = String.join(FIELD_DELIMITER, command, data);
        }
        return result;
    }

    // Parse from protocol string
    public static MessageBody fromProtocolString(String protocolString) {
        String[] parts = protocolString.split(FIELD_DELIMITER, 2);
        if (parts.length < 1) {
            throw new IllegalArgumentException("Invalid message format");
        }
        return new MessageBody(parts[0], parts.length > 1 ? parts[1] : "");
    }
}
