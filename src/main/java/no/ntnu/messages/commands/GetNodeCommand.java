package no.ntnu.messages.commands;

import java.util.HashMap;

import no.ntnu.Clients;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.ActuatorCollection;
import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.tools.Logger;

public class GetNodeCommand extends Command {
    

    public GetNodeCommand(MessageBody body) {
        super(body);
    }

    public Message execute(NodeLogic nodeLogic) {
        // Logger.info("Received request for node from server, sending response " + sender + ";" + senderID + ";" + this.nodeLogic.getId());
        
        ActuatorCollection actuators = nodeLogic.getNode().getActuators();

        // TODO send state of actuator, on/off?

        StringBuilder actuatorString = new StringBuilder();
        HashMap<String, Integer> actuatorCount = new HashMap<String, Integer>();
        for (Actuator actuator : actuators) {
            actuatorString.append(";" + actuator.getType() + "_" + actuator.getId());
            // if (actuatorCount.containsKey(actuator.getType())) {
            //     actuatorCount.put(actuator.getType(), actuatorCount.get(actuator.getType()) + 1);
            // } else {
            //     actuatorCount.put(actuator.getType(), 1);
            // }
            // actuatorString.append(actuator "_" + actuator.getType());
        }

        // for (String key : actuatorCount.keySet()) {
        //     actuatorString.append(";" + actuatorCount.get(key) + "_" + key);
        // }

        String resultString = actuatorString.toString();


        
        // socketWriter.println(sender + ";" + senderID + ";" + node.getId());
        Logger.info(resultString);

        MessageHeader header = new MessageHeader(Clients.CONTROL_PANEL, "0", this.toProtocolString());
        // MessageBody body = new MessageBody(this, resultString);
        MessageBody body = new MessageBody(this.toProtocolString(), resultString);
        return new Message(header, body);
        
        // socketWriter.println(Clients.CONTROL_PANEL + ";0-GET_NODE;" + this.nodeLogic.getId() + resultString); // TODO add sensor data and actuator data.
    }

    public String toProtocolString() {
        return "GET_NODE";
    }
}