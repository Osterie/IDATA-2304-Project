package no.ntnu.greenhouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import no.ntnu.Clients;
import no.ntnu.tools.Logger;

public class NodeConnectionHandler implements Runnable {
    private final SensorActuatorNode node;
    private final Socket socket;
    private final PrintWriter socketWriter;
    private final BufferedReader socketReader;

    public NodeConnectionHandler(SensorActuatorNode node, String host, int port) throws IOException {
        this.node = node;
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
                    Logger.info("Received for node " + node.getId() + ": " + serverMessage);
                    handleServerCommand(serverMessage);
                }
                else{
                    Logger.info("Invalid request from server: " + serverMessage);
                }
            }
        } catch (IOException e) {
            Logger.error("Connection lost for node " + node.getId() + ": " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    public void sendSensorData(String data) {
        socketWriter.println(data);
    }

    private void handleServerCommand(String command) {
        Logger.info("Received command for node! " + node.getId() + ": " + command);
        String[] commandParts = command.split("-");

        String header = commandParts[0];
        String body = commandParts[1];

        String[] headerParts = header.split(";");
        String[] bodyParts = body.split(";");

        String sender = headerParts[0];
        String senderID = headerParts[1];
        String commandType = bodyParts[0];

        if (commandType.equalsIgnoreCase("GET_NODE_ID")) {
            Logger.info("Received request for node ID from server, sending response " + node.getId());
            // socketWriter.println(sender + ";" + senderID + ";" + node.getId());
            socketWriter.println(Clients.CONTROL_PANEL + ";0-GET_NODE_ID;" + node.getId()); // TODO change the id. (from 0)
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
        else if (commandType.equalsIgnoreCase("ACTUATOR_CHANGE")){
            int actuatorId = Integer.parseInt(bodyParts[1]);
            boolean isOn = bodyParts[2].equalsIgnoreCase("ON");
            node.setActuator(actuatorId, isOn);
        }
        else{
            Logger.info("Received unknown command from server: " + command);
        }

        // spawner.advertiseSensorData("4;temperature=27.4 °C,temperature=26.8 °C,humidity=80 %", START_DELAY + 2);


        // Parse and execute commands received from the server for this node
        // Example: Control actuators based on command type
    }

    public void closeConnection() {
        try {
            socket.close();
            socketWriter.close();
            socketReader.close();
            Logger.info("Connection closed for node " + node.getId());
        } catch (IOException e) {
            Logger.error("Error closing connection for node " + node.getId() + ": " + e.getMessage());
        }
    }
}
