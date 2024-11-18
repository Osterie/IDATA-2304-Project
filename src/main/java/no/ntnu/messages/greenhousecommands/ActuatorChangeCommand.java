package no.ntnu.messages.greenhousecommands;

import no.ntnu.Clients;
import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.messages.Delimiters;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.commands.Parameters;

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

        MessageHeader header = new MessageHeader(Clients.CONTROL_PANEL, "0", this.toProtocolString());
        // MessageBody response = new MessageBody(this, "ACTUATOR_CHANGE_SUCCESS");
        MessageBody response = new MessageBody(this, "ACTUATOR_CHANGE_SUCCESS");
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
