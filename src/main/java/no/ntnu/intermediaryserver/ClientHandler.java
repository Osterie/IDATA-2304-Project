package no.ntnu.intermediaryserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import no.ntnu.tools.Logger;

public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private final ProxyServer server;
    private BufferedReader socketReader;
    private PrintWriter socketWriter;
    private String clientType;  // "CONTROL_PANEL" or "GREENHOUSE"
    private String clientId;    // Unique ID for the greenhouse node or control panel

    public ClientHandler(Socket socket, ProxyServer server) {
        this.clientSocket = socket;
        this.server = server;
        initializeStreams();
    }

    private void initializeStreams() {
        try {
            this.socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.socketWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            System.out.println("New client connected from " + clientSocket.getRemoteSocketAddress());
        } catch (IOException e) {
            System.err.println("Could not open reader or writer: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        identifyClientType();  
        handleClient();
    }

    private void identifyClientType() {
        try {
            String identification = socketReader.readLine();
            String[] parts = identification.split(";");
            clientType = parts[0];
            clientId = parts.length > 1 ? parts[1] : null;

            if ("CONTROL_PANEL".equalsIgnoreCase(clientType) && clientId != null) {
                server.addControlPanel(clientId, clientSocket);
                System.out.println("Connected Control Panel with ID: " + clientId);
            } else if ("GREENHOUSE".equalsIgnoreCase(clientType) && clientId != null) {
                server.addGreenhouseNode(clientId, clientSocket);
                System.out.println("Connected Greenhouse Node with ID: " + clientId);
            } else {
                System.out.println("Unknown client type. Closing connection. Received: " + identification + " Client type: " + clientType + " Client ID: " + clientId);
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error identifying client type: " + e.getMessage());
        }
    }

    private void handleClient() {
        String clientRequest;
        try {
            while ((clientRequest = socketReader.readLine()) != null) {
                String response = processRequest(clientRequest);
                if (response != null) {
                    socketWriter.println(response);
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling client request: " + e.getMessage());
        } finally {
            server.removeClient(clientId, "GREENHOUSE".equalsIgnoreCase(clientType));
        }
    }

    private String processRequest(String clientRequest) {
        if ("CONTROL_PANEL".equalsIgnoreCase(clientType)) {
            String[] commandParts = clientRequest.split(" ");
            String nodeId = commandParts[1];
            Socket nodeSocket = server.getGreenhouseNode(nodeId);

            if (nodeSocket != null) {
                try (PrintWriter nodeWriter = new PrintWriter(nodeSocket.getOutputStream(), true)) {
                    nodeWriter.println(clientRequest);  
                    return "Command sent to node " + nodeId;
                } catch (IOException e) {
                    System.err.println("Failed to send command to node: " + e.getMessage());
                }
            } else {
                return "Error: Node " + nodeId + " not found";
            }
        } else if ("GREENHOUSE".equalsIgnoreCase(clientType)) {
            // Parse sensor data or status update to forward to control panel(s)
            Logger.info("Received data from greenhouse " + clientId + ": " + clientRequest);
            return "Data received";
        }
        return null;
    }
}