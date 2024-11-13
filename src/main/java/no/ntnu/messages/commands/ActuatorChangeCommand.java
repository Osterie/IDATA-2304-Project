package no.ntnu.messages.commands;

import no.ntnu.Clients;
import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.messages.Delimiters;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;

public class ActuatorChangeCommand extends Command {
    private String actuatorId;
    private boolean isOn;
    
    public ActuatorChangeCommand(MessageBody body, String actuatorId, boolean isOn) {
        super(body);

        this.actuatorId = actuatorId;
        this.isOn = isOn;
    }

    @Override
    public Message execute(NodeLogic nodeLogic) {
        String[] bodyParts = body.getData().split(";");

        int actuatorId = Integer.parseInt(bodyParts[1]);
        boolean isOn = bodyParts[2].equalsIgnoreCase("ON");
        nodeLogic.getNode().setActuator(actuatorId, isOn);

        MessageHeader header = new MessageHeader(Clients.CONTROL_PANEL, "0", this.toProtocolString());
        // MessageBody response = new MessageBody(this, "ACTUATOR_CHANGE_SUCCESS");
        MessageBody response = new MessageBody(this.toProtocolString(), "ACTUATOR_CHANGE_SUCCESS");
        return new Message(header, response);
    }

    @Override
    public String toProtocolString() {
        return "ACTUATOR_CHANGE" + Delimiters.BODY_DELIMITER + this.actuatorId + Delimiters.BODY_DELIMITER + (this.isOn ? "ON" : "OFF");
    }
}
