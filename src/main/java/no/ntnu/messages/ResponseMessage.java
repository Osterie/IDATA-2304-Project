package no.ntnu.messages;

/**
 * If the client or the server needs to send a response
 * with a message
 */
public class ResponseMessage implements Message {
    private String message;

    public ResponseMessage(String message) {
        this.message = message;
    }

    public String toString() {
        return message;
    }
}
