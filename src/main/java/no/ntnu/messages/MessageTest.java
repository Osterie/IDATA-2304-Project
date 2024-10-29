package no.ntnu.messages;

public class MessageTest {
    private static final String HEADER_BODY_DELIMITER = Delimiters.HEADER_BODY_DELIMITER.getValue();

    private MessageHeader header;
    private MessageBody body;

    // Constructor
    public MessageTest(MessageHeader header, MessageBody body) {
        this.header = header;
        this.body = body;
    }

    // Setters and Getters
    public MessageHeader getHeader() {
        return header;
    }

    public void setHeader(MessageHeader header) {
        this.header = header;
    }

    public MessageBody getBody() {
        return body;
    }

    public void setBody(MessageBody body) {
        this.body = body;
    }

    // Convert to protocol string
    public String toProtocolString() {
        return header.toProtocolString() + HEADER_BODY_DELIMITER + body.toProtocolString();
    }

    // Parse from protocol string
    public static MessageTest fromProtocolString(String protocolString) {
        String[] parts = protocolString.split(HEADER_BODY_DELIMITER, 2);
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid message format");
        }
        MessageHeader header = MessageHeader.fromProtocolString(parts[0]);
        MessageBody body = MessageBody.fromProtocolString(parts[1]);
        return new MessageTest(header, body);
    }
}

