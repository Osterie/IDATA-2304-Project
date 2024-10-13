package no.ntnu.greenhouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
        System.out.println("connecting node " + node.getId() + " with identifier: " + identifierMessage);
    }

    @Override
    public void run() {
        try {
            while (!socket.isClosed()) {
                String serverMessage = socketReader.readLine();
                if (serverMessage != null) {
                    System.out.println("Received for node " + node.getId() + ": " + serverMessage);
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
        System.out.println("Received command for node! " + node.getId() + ": " + command);
        String[] commandParts = command.split(";");
        String sender = commandParts[0];
        String senderID = commandParts[1];
        String commandType = commandParts[2];

        if (commandType.equalsIgnoreCase("GET_NODE_ID")) {
            Logger.info("Received request for node ID from server, sending response " + node.getId());
            socketWriter.println(sender + ";" + senderID + ";" + node.getId());
        }
        else if (commandType.equalsIgnoreCase("GET_NODE")){
            Logger.info("Received request for node from server, sending response " + sender + ";" + senderID + ";" + node.getId());
            // socketWriter.println(sender + ";" + senderID + ";" + node.getId());
            socketWriter.println("CONTROL_PANEL;0;" + node.getId());
        }
        else{
            Logger.info("Received unknown command from server: " + command);
        }
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
