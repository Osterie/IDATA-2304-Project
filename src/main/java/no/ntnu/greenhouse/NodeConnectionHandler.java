package no.ntnu.greenhouse;

import java.io.IOException;
import java.util.HashMap;

import no.ntnu.Clients;
import no.ntnu.SocketCommunicationChannel;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.MessageTest;
import no.ntnu.tools.Logger;

public class NodeConnectionHandler extends SocketCommunicationChannel implements Runnable {
    private final SensorActuatorNode node;

    public NodeConnectionHandler(SensorActuatorNode node, String host, int port) throws IOException {
        super(host, port);
        this.node = node;

        // Send initial identifier to server
        String identifierMessage = "GREENHOUSE;" + node.getId();
        socketWriter.println(identifierMessage);
        Logger.info("connecting node " + node.getId() + " with identifier: " + identifierMessage);
    }

    @Override
    public void run() {
        this.listenForMessages();
    }

    public void sendSensorData(String data) {
        socketWriter.println(data);
    }

    @Override
    protected void handleMessage(String serverMessage) {
        
        Logger.info("Received message for node! " + node.getId() + ": " + serverMessage);

        MessageTest message = MessageTest.fromProtocolString(serverMessage);

        MessageHeader header = message.getHeader();
        MessageBody body = message.getBody();

        Clients sender = header.getReceiver();
        String senderID = header.getId();
        String commandType = body.getCommand();

        Logger.info("Received message from " + sender + " with ID " + senderID + " and command " + commandType);

        if (commandType.equalsIgnoreCase("GET_NODE_ID")) {
            Logger.info("Received request for node ID from server, sending response " + node.getId());

            // TODO change the id. (from 0)
            MessageHeader responseHeader = new MessageHeader(Clients.CONTROL_PANEL, "0", "STRING");
            MessageBody responseBody = new MessageBody("GET_NODE_ID;" + node.getId());
            MessageTest responseMessage = new MessageTest(responseHeader, responseBody);

            socketWriter.println(responseMessage.toProtocolString());
        }
        else if (commandType.equalsIgnoreCase("GET_NODE")){
            Logger.info("Received request for node from server, sending response " + sender + ";" + senderID + ";" + node.getId());
            // spawner.spawnNode("4;2_heater;3_window", START_DELAY + 3);

            // List<Sensor> sensors = node.getSensors();
            ActuatorCollection actuators = node.getActuators();

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


            Logger.info("Received request for node from server, sending response " + sender + ";" + senderID + ";" + node.getId());
            // socketWriter.println(sender + ";" + senderID + ";" + node.getId());
            Logger.info(resultString);
            socketWriter.println(Clients.CONTROL_PANEL + ";0-GET_NODE;" + node.getId() + resultString); // TODO add sensor data and actuator data.
        }
        else if (commandType.contains("ACTUATOR_CHANGE")){
            Logger.info("Received actuator change command from server: " + serverMessage + " " + body.getCommand());

            String[] bodyParts = body.toProtocolString().split(";");
            int actuatorId = Integer.parseInt(bodyParts[1]);
            boolean isOn = bodyParts[2].equalsIgnoreCase("ON");
            node.setActuator(actuatorId, isOn);
        }
        else{
            Logger.info("Received unknown command from server: " + serverMessage);
        }

        // spawner.advertiseSensorData("4;temperature=27.4 °C,temperature=26.8 °C,humidity=80 %", START_DELAY + 2);


        // Parse and execute commands received from the server for this node
        // Example: Control actuators based on command type
    }
}
