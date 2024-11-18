package no.ntnu.messages;

import no.ntnu.messages.commands.CommandTranslator;
import no.ntnu.messages.commands.Command;

/**
 * Represents the body of a message.
 * The body contains the command and the data of the message.
 */
public class MessageBody {
    private static final String FIELD_DELIMITER = Delimiters.BODY_DELIMITER.getValue();
    private Command command;
    private String data;

    // Constructor
    public MessageBody(Command command, String data) {
        this.command = command;
        this.data = data;
    }

    public MessageBody(Command command) {
        this.command = command;
        this.data = "";
    }

    // Setters and Getters
    public Command getCommand() {
        return this.command;
    }

    public void setCommand(Command command) {
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
            result = command.toProtocolString();
        }
        else if (data.isEmpty()) {
            result = command.toProtocolString();
        } else {
            result = String.join(FIELD_DELIMITER, command.toProtocolString(), data);
        }
        return result;
    }

    // Parse from protocol string
    public static MessageBody fromProtocolString(String protocolString) {
        String[] parts = protocolString.split(Delimiters.BODY_DELIMITER.getValue(), 2);
        if (parts.length < 1) {
            throw new IllegalArgumentException("Invalid message format");
        }
        
        CommandTranslator commandTranslator = new CommandTranslator();
        Command command = commandTranslator.toCommand(parts[0]);
        String data = parts.length > 1 ? parts[1] : "";
        return new MessageBody(command, data);
    }
}
