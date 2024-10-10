package no.ntnu.greenhouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NodeConnectionHandler implements Runnable {
    private final SensorActuatorNode node;
    private final Socket socket;
    private final PrintWriter socketWriter;
    private final BufferedReader socketReader;

    public NodeConnectionHandler(SensorActuatorNode node, String host, int port) throws IOException {
        this.node = node;
        this.socket = new Socket(host, port);
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
