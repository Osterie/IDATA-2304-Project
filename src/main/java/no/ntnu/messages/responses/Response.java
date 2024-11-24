package no.ntnu.messages.responses;

import no.ntnu.messages.Delimiters;
import no.ntnu.messages.Transmission;
import no.ntnu.messages.commands.Command;

/**
 * Represents an abstract response to a command in the messaging system.
 * A response contains a protocol string, the command that triggered the response,
 * and any additional response data.
 *
 * Subclasses should define specific types of responses, such as success or failure responses.
 */
public abstract class Response extends Transmission {
    private String responseData;
    private Command command;

    /**
     * Constructs a {@code Response} with the specified protocol string, command, and response data.
     *
     * @param responseProtocolString The protocol string representing the type of response (e.g., "SUCCESS", "FAILURE").
     * @param command                The {@link Command} that triggered this response.
     * @param responseData           Additional data describing the response. Can be null or empty.
     */
    protected Response(String responseProtocolString, Command command, String responseData) {
        super(responseProtocolString);
        this.command = command;
        this.responseData = responseData;
    }

    /**
     * Sets the command associated with this response.
     *
     * @param command The {@link Command} to associate with this response.
     */
    public void setCommand(Command command) {
        this.command = command;
    }

    /**
     * Gets the command associated with this response.
     *
     * @return The {@link Command} that triggered this response.
     */
    public Command getCommand() {
        return command;
    }

    /**
     * Sets the response data.
     *
     * @param responseData The data to associate with this response.
     */
    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    /**
     * Gets the response data.
     *
     * @return The data associated with this response.
     */
    public String getResponseData() {
        return responseData;
    }

    /**
     * Converts this response into its protocol string representation.
     * The format is: `TRANSMISSION_STRING | COMMAND | RESPONSE_DATA`.
     *
     * @return The protocol string representation of this response.
     */
    @Override
    public String toProtocolString() {
        String protocolString = this.getTransmissionString();
        protocolString += Delimiters.BODY_FIELD_PARAMETERS.getValue();
        protocolString += this.command.getTransmissionString();
        protocolString += Delimiters.BODY_FIELD_PARAMETERS.getValue();
        protocolString += this.responseData;
        return protocolString;
    }
}