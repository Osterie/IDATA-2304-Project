package no.ntnu.messages;

import no.ntnu.messages.commands.CommandTranslator;
import no.ntnu.messages.commands.Command;

/**
 * Represents the body of a message.
 * The body consists of a command and its associated data.
 * It serves as the content of the message, providing specific instructions or information.
 */
public class MessageBody {
    private static final String FIELD_DELIMITER = Delimiters.BODY_FIELD.getValue();
    // The command associated with this message body
    private Command command;
    // Optional additional data for the command
    private String data;

    /**
     * Constructs a MessageBody with both a command and data.
     *
     * @param command The command to include in the message body. Must not be null.
     * @param data    The data associated with the command. Can be empty or null.
     */
    public MessageBody(Command command, String data) {
        this.command = command;
        this.data = data;
    }

    /**
     * Constructs a MessageBody with only a command.
     *
     * @param command The command to include in the message body. Must not be null.
     */
    public MessageBody(Command command) {
        this.command = command;
        this.data = "";
    }

    /**
     * Gets the command of the message body.
     *
     * @return The command.
     */
    public Command getCommand() {
        return this.command;
    }

    /**
     * Sets the command of the message body.
     *
     * @param command The command to set. Must not be null.
     */
    public void setCommand(Command command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }
        this.command = command;
    }

    /**
     * Gets the data associated with the command.
     *
     * @return The data.
     */
    public String getData() {
        return data;
    }

    /**
     * Sets the data associated with the command.
     *
     * @param data The data to set. Can be null or empty.
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Converts this message body to its protocol string representation.
     * The format is: `command[FIELD_DELIMITER]data` or `command` if data is null or empty.
     *
     * @return The protocol string representation of the message body.
     */
    public String toProtocolString() {
        if (data == null || data.isEmpty()) {
            return command.toProtocolString();
        } else {
            return String.join(FIELD_DELIMITER, command.toProtocolString(), data);
        }
    }

    /**
     * Parses a MessageBody from its protocol string representation.
     *
     * @param protocolString The protocol string to parse. Expected format: `command[FIELD_DELIMITER]data`.
     * @return The parsed {@link MessageBody} object.
     * @throws IllegalArgumentException If the protocol string is invalid or malformed.
     */
    public static MessageBody fromProtocolString(String protocolString) {
        String[] parts = protocolString.split(Delimiters.BODY_FIELD.getValue(), 2);
        if (parts.length < 1) {
            throw new IllegalArgumentException("Invalid message format");
        }

        CommandTranslator commandTranslator = new CommandTranslator();
        Command command = commandTranslator.toCommand(parts[0]);
        String data = parts.length > 1 ? parts[1] : ""; // Use empty string if data is not provided
        return new MessageBody(command, data);
    }
}
