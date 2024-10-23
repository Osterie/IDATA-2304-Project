package no.ntnu.greenhouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
            }
        } catch (IOException e) {
            System.err.println("Connection lost for node " + node.getId() + ": " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    public void sendSensorData(String data) {
        socketWriter.println(data);
    }

    private void handleServerCommand(String command) {
        Logger.info("Received command for node! " + node.getId() + ": " + command);
        String[] commandParts = command.split(";");
        String sender = commandParts[0];
        String senderID = commandParts[1];
        String commandType = commandParts[2];

        if (commandType.equalsIgnoreCase("GET_NODE_ID")) {
            Logger.info("Received request for node ID from server, sending response " + node.getId());
            // socketWriter.println(sender + ";" + senderID + ";" + node.getId());
            socketWriter.println(Clients.CONTROL_PANEL + ";0;" + node.getId()); // TODO change the id. (from 0)
        }
        else if (commandType.equalsIgnoreCase("GET_NODE")){
            // spawner.spawnNode("4;2_heater;3_window", START_DELAY + 3);

            // List<Sensor> sensors = node.getSensors();
            ActuatorCollection actuators = node.getActuators();

            StringBuilder actuatorString = new StringBuilder();
            HashMap<String, Integer> actuatorCount = new HashMap<String, Integer>();
            for (Actuator actuator : actuators) {
                if (actuatorCount.containsKey(actuator.getType())) {
                    actuatorCount.put(actuator.getType(), actuatorCount.get(actuator.getType()) + 1);
                } else {
                    actuatorCount.put(actuator.getType(), 1);
                }
                // actuatorString.append(actuator "_" + actuator.getType());
            }

            for (String key : actuatorCount.keySet()) {
                actuatorString.append(";" + actuatorCount.get(key) + "_" + key);
            }

            String resultString = actuatorString.toString();


            Logger.info("Received request for node from server, sending response " + sender + ";" + senderID + ";" + node.getId());
            // socketWriter.println(sender + ";" + senderID + ";" + node.getId());
            Logger.info(resultString);
            socketWriter.println(Clients.CONTROL_PANEL + ";0;" + node.getId() + resultString); // TODO add sensor data and actuator data.
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
        } catch (IOException e) {
            System.err.println("Error closing connection for node " + node.getId() + ": " + e.getMessage());
        }
    }
}
