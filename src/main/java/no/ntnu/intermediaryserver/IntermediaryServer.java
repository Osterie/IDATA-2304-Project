

package no.ntnu.intermediaryserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import no.ntnu.Clients;
import no.ntnu.tools.Logger;

/**
 * The IntermediaryServer class is responsible for managing the connections between the greenhouse nodes and control panels.
 * It listens for incoming connections from clients and creates a new thread to handle each client connection.
 * 
 */
public class IntermediaryServer implements Runnable {
    public static final int PORT_NUMBER = 50500;
    private boolean isTcpServerRunning;

    // Using ConcurrentHashMap for thread-safe access
    private final ConcurrentHashMap<String, Socket> controlPanels = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Socket> greenhouseNodes = new ConcurrentHashMap<>();
    private ServerSocket listeningSocket;

    public static void main(String[] args) throws IOException {
        new IntermediaryServer().startServer();
    }

    // Start the server to listen for client connections
    public void startServer() {
        listeningSocket = this.openListeningSocket(PORT_NUMBER);
        if (listeningSocket != null) {
            this.isTcpServerRunning = true;
            Logger.info("Server started on port " + PORT_NUMBER);
            
            while (isTcpServerRunning) {
                Socket clientSocket = acceptNextClientConnection();
                if (clientSocket != null) {
                    new Thread(new ClientHandler(clientSocket, this)).start();
                }
            }
        }
    }

    public synchronized void stopServer() {
        if (isTcpServerRunning) {
            isTcpServerRunning = false;
            try {
                if (listeningSocket != null && !listeningSocket.isClosed()) {
                    listeningSocket.close();
                }
                Logger.info("Server stopped.");
            } catch (IOException e) {
                Logger.error("Error closing server socket: " + e.getMessage());
            }
        }
    }

    public synchronized void addClient(String clientType, String clientId, Socket socket) {
        if (clientType.equals(Clients.GREENHOUSE.getValue())) {
            addGreenhouseNode(clientId, socket);
        } else if (clientType.equals(Clients.CONTROL_PANEL.getValue())) {
            addControlPanel(clientId, socket);
        }
        else {
            Logger.error("Unknown client type: " + clientType);
            throw new UnknownClientException("Unknown client type: " + clientType);
        }
        Logger.info("Connected " + clientType + " with ID: " + clientId);
    }

    public synchronized void addGreenhouseNode(String nodeId, Socket socket) {
        greenhouseNodes.put(nodeId, socket);
        Logger.info("Greenhouse node added: " + nodeId);
    }

    public synchronized void addControlPanel(String panelId, Socket socket) {
        controlPanels.put(panelId, socket);
        Logger.info("Control panel added: " + panelId);
    }

    public synchronized void removeClient(String clientId, boolean isGreenhouse) {
        if (isGreenhouse) {
            greenhouseNodes.remove(clientId);
            Logger.info("Greenhouse node removed: " + clientId);
        } else {
            controlPanels.remove(clientId);
            Logger.info("Control panel removed: " + clientId);
        }
    }

    public Socket getGreenhouseNode(String nodeId) {
        return greenhouseNodes.get(nodeId);
    }

    public ArrayList<Socket> getGreenhouseNodes() {
        return new ArrayList<>(greenhouseNodes.values());
    }

    public ArrayList<String> getGreenhouseNodeIds() {
        return new ArrayList<>(greenhouseNodes.keySet());
    }

    public Socket getControlPanel(String panelId) {
        return controlPanels.get(panelId);
    }

    public ArrayList<Socket> getControlPanels() {
        return new ArrayList<>(controlPanels.values());
    }

    public Socket getClient(String clientType, String clientId) {
        Socket clientSocket = null;
        if (clientType.equals(Clients.GREENHOUSE.getValue())) {
            clientSocket = this.getGreenhouseNode(clientId);
        } else if (clientType.equals(Clients.CONTROL_PANEL.getValue())) {
            clientSocket = this.getControlPanel(clientId);
        }
        return clientSocket;
    }

    public ArrayList<Socket> getAllClients(String clientType) {
        ArrayList<Socket> clients = new ArrayList<>();
        if (clientType.equalsIgnoreCase(Clients.GREENHOUSE.getValue())) {
            clients = this.getGreenhouseNodes();
        } else if (clientType.equalsIgnoreCase(Clients.CONTROL_PANEL.getValue())) {
            clients = this.getControlPanels();
        }
        return clients;
    }

    private Socket acceptNextClientConnection() {
        Socket clientSocket = null;
        try {
          clientSocket = listeningSocket.accept();
        } catch (IOException e) {
          System.err.println("Could not accept client connection: " + e.getMessage());
        }
        return clientSocket;
    }

    private ServerSocket openListeningSocket(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Logger.info("Server listening on port " + port);
            return serverSocket;
        } catch (IOException e) {
            Logger.error("Could not open server socket on port " + port + ": " + e.getMessage());
        }
        return null;
    }

    @Override
    public void run() {
        startServer();
    }
}