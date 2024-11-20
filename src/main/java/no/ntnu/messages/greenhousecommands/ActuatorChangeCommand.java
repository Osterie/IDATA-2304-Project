package no.ntnu.messages.greenhousecommands;

import no.ntnu.Endpoints;
import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.messages.Delimiters;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.commands.Parameters;

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
    public Message execute(NodeLogic nodeLogic) {

        nodeLogic.getNode().setActuator(this.actuatorId, this.isOn);

        // TODO improve.
        MessageHeader header = new MessageHeader(Endpoints.CONTROL_PANEL, "ALL", this.toProtocolString());
        // MessageBody response = new MessageBody(this, "ACTUATOR_CHANGE_SUCCESS");

        String actuatorState = this.isOn ? "ON" : "OFF";
        String nodeId = Integer.toString(nodeLogic.getId());
        
        String responseData = nodeId;
        responseData += Delimiters.BODY_PARAMETERS_DELIMITER.getValue() + this.actuatorId;
        responseData += Delimiters.BODY_PARAMETERS_DELIMITER.getValue() + actuatorState;

        MessageBody response = new MessageBody(this, responseData);
        return new Message(header, response);
    }


    @Override
    public String toProtocolString() {
        return this.getCommandString() + Delimiters.BODY_PARAMETERS_DELIMITER.getValue() + this.actuatorId + Delimiters.BODY_PARAMETERS_DELIMITER.getValue() + (this.isOn ? "ON" : "OFF");
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
}
