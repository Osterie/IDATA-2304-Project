package no.ntnu.messages.commands.greenhouse;

import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.ActuatorCollection;
import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.messages.Delimiters;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.responses.SuccessResponse;

/**
 * Represents a command to retrieve information about a node in the greenhouse system.
 * This command is sent to a greenhouse node to request details about its actuators and state.
 */
public class GetNodeCommand extends GreenhouseCommand {

    /**
     * Constructs a new {@code GetNodeCommand} with the command type "GET_NODE".
     */
    public GetNodeCommand() {
        super("GET_NODE");
    }

    /**
     * Executes the "GET_NODE" command on a given greenhouse node's logic and returns a response message.
     * This method gathers information about the node's actuators and their state, formats it,
     * and includes it in a response message.
     *
     * @param nodeLogic  The {@link NodeLogic} instance representing the logic of the greenhouse node.
     * @param fromHeader The {@link MessageHeader} of the message that initiated this command.
     * @return A {@link Message} containing the response with the node's details.
     */
    public Message execute(NodeLogic nodeLogic, MessageHeader fromHeader) {
        // Retrieve the collection of actuators from the node
        ActuatorCollection actuators = nodeLogic.getNode().getActuators();

        // Build a string representing the actuators (type and ID)
        StringBuilder actuatorStringBuild = new StringBuilder();
        for (Actuator actuator : actuators) {
            actuatorStringBuild.append(Delimiters.BODY_FIELD_PARAMETERS.getValue())
            .append(actuator.getType())
            .append("_")
            .append(actuator.getId())
            .append("_")
            .append(actuator.isOn() ? "1" : "0");
        }

        // Include the node's ID as part of the response
        String actuatorString = actuatorStringBuild.toString();
        if (actuatorString.length() > 0) {
            actuatorString = actuatorString.substring(1); // Remove the first delimiter
        }
        
        String resultString = nodeLogic.getId() + Delimiters.BODY_FIELD.getValue() + actuatorString;

        // Create a success response with the gathered data
        SuccessResponse response = new SuccessResponse(this, resultString);
        MessageBody body = new MessageBody(response);
        return new Message(fromHeader, body);
    }

    /**
     * Converts the command to its protocol string representation.
     *
     * @return The transmission string of the command.
     */
    @Override
    public String toString() {
        return this.getTransmissionString();
    }
}