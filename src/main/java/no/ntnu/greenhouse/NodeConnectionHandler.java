package no.ntnu.greenhouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import no.ntnu.Clients;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.commands.Command;
import no.ntnu.tools.Logger;

public class NodeConnectionHandler implements Runnable {
    private final NodeLogic nodeLogic;
    private final Socket socket;
    private final PrintWriter socketWriter;
    private final BufferedReader socketReader;

    public NodeConnectionHandler(SensorActuatorNode node, String host, int port) throws IOException {
        this.nodeLogic = new NodeLogic(node);
        this.socket = new Socket(host, port);
        this.socket.setKeepAlive(true);
        this.socketWriter = new PrintWriter(socket.getOutputStream(), true);
        this.socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Send initial identifier to server
        String identifierMessage = "GREENHOUSE;" + node.getId();
        socketWriter.println(identifierMessage);
        Logger.info("connecting node " + node.getId() + " with identifier: " + identifierMessage);
    }

    @Override
    public void run() {
        try {
            while (!socket.isClosed()) {
                String serverMessage = socketReader.readLine();
                if (serverMessage != null) {
                    Logger.info("Received for node " + this.nodeLogic.getId() + ": " + serverMessage);
                    handleServerCommand(serverMessage);
                }
                else{
                    Logger.info("Invalid request from server: " + serverMessage);
                }
            }
        } catch (IOException e) {
            Logger.error("Connection lost for node " + this.nodeLogic.getId() + ": " + e.getMessage());
        } finally {
            this.close();
        }
    }

    private void handleServerCommand(String command) {
        Logger.info("Received command for node! " + this.nodeLogic.getNode().getId() + ": " + command);
        String[] commandParts = command.split("-");

        String header = commandParts[0];
        String body = commandParts[1];

        String[] headerParts = header.split(";");
        String[] bodyParts = body.split(";");

        String sender = headerParts[0];
        String senderID = headerParts[1];
        String commandType = bodyParts[0];

        if (commandType.equalsIgnoreCase("GET_NODE_ID")) {
            Logger.info("Received request for node ID from server, sending response " + this.nodeLogic.getNode().getId());
            // socketWriter.println(sender + ";" + senderID + ";" + node.getId());
            socketWriter.println(Clients.CONTROL_PANEL + ";0-GET_NODE_ID;" + this.nodeLogic.getNode().getId()); // TODO change the id. (from 0)
        }
        else if (commandType.equalsIgnoreCase("GET_NODE")){
            Logger.info("Received request for node from server, sending response " + sender + ";" + senderID + ";" + this.nodeLogic.getNode().getId());
            // spawner.spawnNode("4;2_heater;3_window", START_DELAY + 3);

            // List<Sensor> sensors = node.getSensors();
            ActuatorCollection actuators = this.nodeLogic.getNode().getActuators();

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


            Logger.info("Received request for node from server, sending response " + sender + ";" + senderID + ";" + this.nodeLogic.getNode().getId());
            // socketWriter.println(sender + ";" + senderID + ";" + node.getId());
            Logger.info(resultString);
            socketWriter.println(Clients.CONTROL_PANEL + ";0-GET_NODE;" + this.nodeLogic.getNode().getId() + resultString); // TODO add sensor data and actuator data.
        }
        else if (commandType.equalsIgnoreCase("ACTUATOR_CHANGE")){
            int actuatorId = Integer.parseInt(bodyParts[1]);
            boolean isOn = bodyParts[2].equalsIgnoreCase("ON");
            this.nodeLogic.getNode().setActuator(actuatorId, isOn);
        }
        else{
            Logger.info("Received unknown command from server: " + command);
        }

        // spawner.advertiseSensorData("4;temperature=27.4 °C,temperature=26.8 °C,humidity=80 %", START_DELAY + 2);


        // Parse and execute commands received from the server for this node
        // Example: Control actuators based on command type
    }

    // TODO fix, improve, refactor. Use command classes and message classes
    // private void handleServerCommandNew(String message) {

    //     Logger.info("Received message for node! " + this.nodeLogic.getId() + ": " + message);
    //     // String[] commandParts = message.split("-");

    //     // String header = commandParts[0];
    //     // String body = commandParts[1];

    //     // String[] headerParts = header.split(";");
    //     // String[] bodyParts = body.split(";");

    //     // String sender = headerParts[0];
    //     // String senderID = headerParts[1];
    //     // String commandType = bodyParts[0];

    //     Message messageObject = Message.fromProtocolString(message);
    //     MessageHeader header = messageObject.getHeader();
    //     MessageBody body = messageObject.getBody();
        
    //     String sender = header.getReceiver().toString();
    //     String senderID = header.getId();
    //     String commandType = body.getCommand();
    //     // command.setBody(body);
        
    //     // Message response = command.execute(this.nodeLogic);

    //     // Logger.info("Received request for node from server, sending response " + sender + ";" + senderID + ";" + nodeLogic.getId());
    //     // socketWriter.println(response.toProtocolString());
    //     // Logger.info("Received unknown message from server: " + message);



    //     if (commandType.equalsIgnoreCase("GET_NODE_ID")) {
            
    //         // Message response = commandType.excecuteCommand(this.nodeLogic);



    //         Logger.info("Received request for node ID from server, sending response " + this.nodeLogic.getId());
    //         // socketWriter.println(sender + ";" + senderID + ";" + node.getId());
    //         socketWriter.println(Clients.CONTROL_PANEL + ";0-GET_NODE_ID;" + this.nodeLogic.getId()); // TODO change the id. (from 0)
    //     }
    //     else if (commandType.equalsIgnoreCase("GET_NODE")){
    //         Logger.info("Received request for node from server, sending response " + sender + ";" + senderID + ";" + this.nodeLogic.getId());
    //         // spawner.spawnNode("4;2_heater;3_window", START_DELAY + 3);

    //         // List<Sensor> sensors = node.getSensors();
    //         ActuatorCollection actuators = this.nodeLogic.getActuators();

    //         // TODO send state of actuator, on/off?

    //         StringBuilder actuatorString = new StringBuilder();
    //         HashMap<String, Integer> actuatorCount = new HashMap<String, Integer>();
    //         for (Actuator actuator : actuators) {
    //             actuatorString.append(";" + actuator.getType() + "_" + actuator.getId());
    //             // if (actuatorCount.containsKey(actuator.getType())) {
    //             //     actuatorCount.put(actuator.getType(), actuatorCount.get(actuator.getType()) + 1);
    //             // } else {
    //             //     actuatorCount.put(actuator.getType(), 1);
    //             // }
    //             // actuatorString.append(actuator "_" + actuator.getType());
    //         }

    //         // for (String key : actuatorCount.keySet()) {
    //         //     actuatorString.append(";" + actuatorCount.get(key) + "_" + key);
    //         // }

    //         String resultString = actuatorString.toString();


    //         Logger.info("Received request for node from server, sending response " + sender + ";" + senderID + ";" + nodeLogic.getId());
    //         // socketWriter.println(sender + ";" + senderID + ";" + node.getId());
    //         Logger.info(resultString);
    //         socketWriter.println(Clients.CONTROL_PANEL + ";0-GET_NODE;" + this.nodeLogic.getId() + resultString); // TODO add sensor data and actuator data.
    //     }
    //     else if (commandType.equalsIgnoreCase("ACTUATOR_CHANGE")){
    //         int actuatorId = Integer.parseInt(bodyParts[1]);
    //         boolean isOn = bodyParts[2].equalsIgnoreCase("ON");
    //         this.nodeLogic.setActuator(actuatorId, isOn);
    //     }
    //     else{
    //         Logger.info("Received unknown message from server: " + message);
    //     }

    //     // spawner.advertiseSensorData("4;temperature=27.4 °C,temperature=26.8 °C,humidity=80 %", START_DELAY + 2);


    //     // TODO?
    //     // Parse and execute commands received from the server for this node
    //     // Example: Control actuators based on command type
    // }

    public void close() {
        try {
            socket.close();
            socketWriter.close();
            socketReader.close();
            Logger.info("Connection closed for node " + nodeLogic.getId());
        } catch (IOException e) {
            Logger.error("Error closing connection for node " + nodeLogic.getId() + ": " + e.getMessage());
        }
    }
}
