package no.ntnu.messages.commands.greenhouse;

import no.ntnu.constants.Endpoints;
import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.messages.Delimiters;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.commands.Parameters;
import no.ntnu.messages.responses.SuccessResponse;

// TODO refactor class. what should be in command and what should be in data?
public class ActuatorChangeCommand extends GreenhouseCommand implements Parameters {

    private int actuatorId;
    private boolean isOn;

    public ActuatorChangeCommand(int actuatorId, boolean isOn) {
        super("ACTUATOR_CHANGE");
        this.actuatorId = actuatorId;
        this.isOn = isOn;
    }

    public ActuatorChangeCommand() {
        super("ACTUATOR_CHANGE");
    }

    @Override
    public Message execute(NodeLogic nodeLogic, MessageHeader fromHeader) {

        nodeLogic.getNode().setActuator(this.actuatorId, this.isOn);

        // TODO improve.
        // MessageHeader header = new MessageHeader(fromHeader.getReceiver(), Endpoints.BROADCAST.getValue(), this);
        MessageHeader header = new MessageHeader(fromHeader.getReceiver(), Endpoints.BROADCAST.getValue());
        // MessageBody response = new MessageBody(this, "ACTUATOR_CHANGE_SUCCESS");

        String actuatorState = this.isOn ? "ON" : "OFF";
        String nodeId = Integer.toString(nodeLogic.getId());
        
        String responseData = nodeId;
        responseData += Delimiters.BODY_FIELD_PARAMETERS.getValue() + this.actuatorId;
        responseData += Delimiters.BODY_FIELD_PARAMETERS.getValue() + actuatorState;
        
        SuccessResponse successResponse = new SuccessResponse(this, responseData);
        MessageBody response = new MessageBody(successResponse);
        return new Message(header, response);
    }

    @Override
    public void setParameters(String parameters[]) {
        this.actuatorId = Integer.parseInt(parameters[0]);
        this.isOn = parameters[1].equals("ON");
    }

    public void setActuatorId(int actuatorId) {
        this.actuatorId = actuatorId;
    }

    public void setIsOn(boolean isOn) {
        this.isOn = isOn;
    }

    @Override
    public String toString() {
        return this.getTransmissionString() + Delimiters.BODY_FIELD_PARAMETERS.getValue() + this.actuatorId + Delimiters.BODY_FIELD_PARAMETERS.getValue() + (this.isOn ? "ON" : "OFF");
    }
}
