package no.ntnu.messages.commands.greenhouse;

import static no.ntnu.tools.parsing.Parser.parseBooleanOrError;

import no.ntnu.constants.Endpoints;
import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.messages.Delimiters;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.commands.Parameters;
import no.ntnu.messages.responses.SuccessResponse;

/**
 * Command to change the state of an actuator.
 */
public class ActuatorChangeCommand extends GreenhouseCommand implements Parameters {

    private int actuatorId;
    private boolean isOn;

    /**
     * Constructs an ActuatorChangeCommand for the actuator ID and state.
     *
     * @param actuatorId the ID of the actuator to change
     * @param isOn       the new state of the actuator (true for on, false for off)
     */
    public ActuatorChangeCommand(int actuatorId, boolean isOn) {
        super("ACTUATOR_CHANGE");
        this.actuatorId = actuatorId;
        this.isOn = isOn;
    }

    /**
     * Constructs an ActuatorChangeCommand with no parameters set.
     */
    public ActuatorChangeCommand() {
        super("ACTUATOR_CHANGE");
    }

    /**
     * Executes the command to change the state of an actuator for the node of nodeLogic.
     *
     * @param nodeLogic  the logic of the node where the actuator is located
     * @param fromHeader the header of the message from which this command
     *                   originated
     * @return a Message indicating the result of the command execution
     */
    @Override
    public Message execute(NodeLogic nodeLogic, MessageHeader fromHeader) {

        nodeLogic.getNode().setActuator(this.actuatorId, this.isOn);


        SuccessResponse successResponse = new SuccessResponse(this, this.createResponseData(nodeLogic));
        MessageBody response = new MessageBody(successResponse);
        
        MessageHeader header = new MessageHeader(fromHeader.getReceiver(), Endpoints.BROADCAST.getValue());
        return new Message(header, response);
    }

    /**
     * Creates the response data for the command execution.
     *
     * @param nodeLogic the logic of the node where the actuator is located
     * @return the response data
     */
    private String createResponseData(NodeLogic nodeLogic) {
        String nodeId = Integer.toString(nodeLogic.getId());

        StringBuilder sb = new StringBuilder();
        sb.append(nodeId);
        sb.append(Delimiters.BODY_FIELD.getValue());
        sb.append(this.actuatorId);
        sb.append(Delimiters.BODY_FIELD.getValue());
        sb.append(this.isOn ? "1" : "0");
        return sb.toString();
    }

    /**
     * Sets the parameters for the ActuatorChangeCommand.
     *
     * @param parameters an array of parameters where the first element is the
     *                   actuator ID and the second element is the state (1 for on,
     *                   0 for off)
     * @throws IllegalArgumentException if the parameters are invalid
     */
    @Override
    public void setParameters(String[] parameters) throws IllegalArgumentException {

        if (parameters.length != 2) {
            throw new IllegalArgumentException("Invalid parameters for ActuatorChangeCommand");
        }

        this.actuatorId = Integer.parseInt(parameters[0]);
        this.isOn = parseBooleanOrError(parameters[1], "Invalid actuator state: " + parameters[1]);
    }

    /**
     * Sets the actuator ID to change state for.
     *
     * @param actuatorId the ID of the actuator to change state for.
     */
    public void setActuatorId(int actuatorId) {
        this.actuatorId = actuatorId;
    }

    /**
     * Sets the state of the actuator.
     *
     * @param isOn the new state of the actuator (true for on, false for off)
     */
    public void setIsOn(boolean isOn) {
        this.isOn = isOn;
    }

    /**
     * Returns a string representation of the ActuatorChangeCommand, which follows the protocol.
     *
     * @return a string representation of the command, formatted according to the protocol.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getTransmissionString());
        sb.append(Delimiters.BODY_FIELD_PARAMETERS.getValue());
        sb.append(this.actuatorId);
        sb.append(Delimiters.BODY_FIELD_PARAMETERS.getValue());
        sb.append(this.isOn ? "1" : "0");
        return sb.toString();
    }
}