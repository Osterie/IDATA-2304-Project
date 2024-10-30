package no.ntnu.greenhouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import no.ntnu.Clients;
import no.ntnu.tools.Logger;

/**
 * Handles the connection between a SensorActuatorNode and the server.
 * This class manages the communication, sending sensor data, and handling server commands.
 */
public class NodeConnectionHandler implements Runnable {
    private final SensorActuatorNode node;
    private final Socket socket;
    private final PrintWriter socketWriter;
    private final BufferedReader socketReader;

    /**
     * Constructs a NodeConnectionHandler for a given node, host, and port.
     *
     * @param node The SensorActuatorNode to be managed
     * @param host The server host
     * @param port The server port
     * @throws IOException If an I/O error occurs when creating the socket or streams
     */
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

    /**
     * Runs the NodeConnectionHandler thread, listening for messages from the server and handling them.
     */
    @Override
    public void run() {
        try {
            while (!socket.isClosed()) {
                String serverMessage = socketReader.readLine();
                if (serverMessage != null) {
                    Logger.info("Received for node " + node.getId() + ": " + serverMessage);
                    handleServerCommand(serverMessage);
                } else {
                    Logger.info("Invalid request from server: " + serverMessage);
                }
            }
        } catch (IOException e) {
            Logger.error("Connection lost for node " + node.getId() + ": " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    /**
     * Sends sensor data to the server.
     *
     * @param data The sensor data to send
     */
    public void sendSensorData(String data) {
        socketWriter.println(data);
    }

    /**
     * Handles commands received from the server.
     *
     * @param command The command received from the server
     */
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
            socketWriter.println(Clients.CONTROL_PANEL + ";0-GET_NODE_ID;" + node.getId()); // TODO change the id. (from 0)
        } else if (commandType.equalsIgnoreCase("GET_NODE")) {
            Logger.info("Received request for node from server, sending response " + sender + ";" + senderID + ";" + node.getId());

            ActuatorCollection actuators = node.getActuators();
            StringBuilder actuatorString = new StringBuilder();
            for (Actuator actuator : actuators) {
                actuatorString.append(";" + actuator.getType() + "_" + actuator.getId());
            }

            String resultString = actuatorString.toString();
            Logger.info(resultString);
            socketWriter.println(Clients.CONTROL_PANEL + ";0-GET_NODE;" + node.getId() + resultString); // TODO add sensor data and actuator data.
        } else if (commandType.equalsIgnoreCase("ACTUATOR_CHANGE")) {
            int actuatorId = Integer.parseInt(bodyParts[1]);
            boolean isOn = bodyParts[2].equalsIgnoreCase("ON");
            node.setActuator(actuatorId, isOn);
        } else {
            Logger.info("Received unknown command from server: " + command);
        }
    }

    /**
     * Closes the connection to the server, including the socket and streams.
     */
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