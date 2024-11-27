package no.ntnu.messages.commands.greenhouse;

import no.ntnu.constants.Endpoints;
import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.messages.Delimiters;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.commands.Parameters;
import no.ntnu.messages.responses.SuccessResponse;

// TODO refactor class.
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

        // TODO improve.
        MessageHeader header = new MessageHeader(fromHeader.getReceiver(), Endpoints.BROADCAST.getValue());

        String nodeId = Integer.toString(nodeLogic.getId());
        
        String responseData = nodeId;
        responseData += Delimiters.BODY_FIELD_PARAMETERS.getValue() + this.actuatorId;
        responseData += Delimiters.BODY_FIELD_PARAMETERS.getValue() + (this.isOn ? "1" : "0");
        
        SuccessResponse successResponse = new SuccessResponse(this, responseData);
        MessageBody response = new MessageBody(successResponse);
        return new Message(header, response);
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
    public void setParameters(String parameters[]) throws IllegalArgumentException {

        // TODO currently the parameters are in a specific order, would it be better to instead send som information about what the parameters are?
        // So that the order of the parameters does not matter. For example: "actuatorId=1, isOn=1"

        if (parameters.length != 2) {
            throw new IllegalArgumentException("Invalid parameters for ActuatorChangeCommand");
        }

        this.actuatorId = Integer.parseInt(parameters[0]);
        if (parameters[1].equals("1")) {
            this.isOn = true;
        } else if (parameters[1].equals("0")) {
            this.isOn = false;
        }
        else{
            throw new IllegalArgumentException("Invalid parameters for ActuatorChangeCommand");
        }
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
        // TODO refactor
        return this.getTransmissionString() + Delimiters.BODY_FIELD_PARAMETERS.getValue() + this.actuatorId + Delimiters.BODY_FIELD_PARAMETERS.getValue() + (this.isOn ? "1" : "0");
    }
}