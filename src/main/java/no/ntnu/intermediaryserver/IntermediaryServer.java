package no.ntnu.intermediaryserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import no.ntnu.Clients;
import no.ntnu.tools.Logger;

/**
 * The IntermediaryServer class is responsible for managing the connections
 * between greenhouse nodes and control panels. It listens for incoming client
 * connections, then assigns each client to a handler thread for processing.
 */
public class IntermediaryServer implements Runnable {
    public static final int PORT_NUMBER = 50500;
    private boolean isTcpServerRunning;

    // Thread-safe collections for managing client sockets
    private final ConcurrentHashMap<String, Socket> controlPanels = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Socket> greenhouseNodes = new ConcurrentHashMap<>();
    private ServerSocket listeningSocket;

    /**
     * Main method to start the Intermediary Server.
     *
     * @throws IOException if there is an error starting the server
     */
    public static void main(String[] args) throws IOException {
        Logger.info("Starting Intermediary Server...");
        new IntermediaryServer().startServer();
    }

    /**
     * Starts the server and listens for incoming client connections on the specified port.
     * Creates a new thread to handle each client connection.
     */
    public void startServer() {
        listeningSocket = this.openListeningSocket(PORT_NUMBER);
        if (listeningSocket != null) {
            this.isTcpServerRunning = true;
            Logger.info("Server started on port " + PORT_NUMBER);

            // Runs the whole time while application is up
            while (isTcpServerRunning) {
                Socket clientSocket = acceptNextClientConnection();
                if (clientSocket != null) {
                    new Thread(new ClientHandler(clientSocket, this)).start();
                }
            }
        }
    }

    /**
     * Stops the server and closes the listening socket.
     */
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

    /**
     * Adds a client to the appropriate collection, based on client type.
     *
     * @param clientType the type of client (CONTROL_PANEL or GREENHOUSE)
     * @param clientId   the unique identifier for the client
     * @param socket     the socket connection for the client
     * @throws UnknownClientException if the client type is not recognized
     */
    public synchronized void addClient(Clients clientType, String clientId, Socket socket) {
        if (clientType == Clients.GREENHOUSE) {
            addGreenhouseNode(clientId, socket);
        } else if (clientType == Clients.CONTROL_PANEL) {
            addControlPanel(clientId, socket);
        } else {
            Logger.error("Unknown client type: " + clientType);
            throw new UnknownClientException("Unknown client type: " + clientType);
        }
        Logger.info("Connected " + clientType + " with ID: " + clientId);
    }

    /**
     * Adds a greenhouse node to the collection of connected greenhouse nodes.
     *
     * @param nodeId the unique identifier for the greenhouse node
     * @param socket the socket connection for the greenhouse node
     */
    public synchronized void addGreenhouseNode(String nodeId, Socket socket) {
        greenhouseNodes.put(nodeId, socket);
        Logger.info("Greenhouse node added: " + nodeId);
    }

    /**
     * Adds a control panel to the collection of connected control panels.
     *
     * @param panelId the unique identifier for the control panel
     * @param socket  the socket connection for the control panel
     */
    public synchronized void addControlPanel(String panelId, Socket socket) {
        controlPanels.put(panelId, socket);
        Logger.info("Control panel added: " + panelId);
    }

    /**
     * Removes a client from the collection based on client ID and type.
     *
     * @param clientId    the unique identifier for the client
     * @param isGreenhouse true if the client is a greenhouse node; false if a control panel
     */
    public synchronized void removeClient(String clientId, boolean isGreenhouse) {
        if (isGreenhouse) {
            greenhouseNodes.remove(clientId);
            Logger.info("Greenhouse node removed: " + clientId);
        } else {
            controlPanels.remove(clientId);
            Logger.info("Control panel removed: " + clientId);
        }
    }

    /**
     * Retrieves a specific greenhouse node by ID.
     *
     * @param nodeId the unique identifier for the greenhouse node
     * @return the socket connection for the greenhouse node, or null if not found
     */
    public Socket getGreenhouseNode(String nodeId) {
        return greenhouseNodes.get(nodeId);
    }

    /**
     * Retrieves a list of all connected greenhouse nodes.
     *
     * @return an ArrayList of sockets representing the greenhouse nodes
     */
    public ArrayList<Socket> getGreenhouseNodes() {
        return new ArrayList<>(greenhouseNodes.values());
    }

    /**
     * Retrieves a list of all greenhouse node IDs.
     *
     * @return an ArrayList of strings representing greenhouse node IDs
     */
    public ArrayList<String> getGreenhouseNodeIds() {
        return new ArrayList<>(greenhouseNodes.keySet());
    }

    /**
     * Retrieves a specific control panel by ID.
     *
     * @param panelId the unique identifier for the control panel
     * @return the socket connection for the control panel, or null if not found
     */
    public Socket getControlPanel(String panelId) {
        return controlPanels.get(panelId);
    }

    /**
     * Retrieves a list of all connected control panels.
     *
     * @return an ArrayList of sockets representing the control panels
     */
    public ArrayList<Socket> getControlPanels() {
        return new ArrayList<>(controlPanels.values());
    }

    /**
     * Retrieves a specific client socket based on client type and ID.
     *
     * @param clientType the type of client (CONTROL_PANEL or GREENHOUSE)
     * @param clientId   the unique identifier for the client
     * @return the socket connection for the client, or null if not found
     */
    public Socket getClient(Clients clientType, String clientId) {
        Socket clientSocket = null;

        if (clientType == Clients.GREENHOUSE) {
            clientSocket = getGreenhouseNode(clientId);
        } else if (clientType == Clients.CONTROL_PANEL) {
            clientSocket = getControlPanel(clientId);
        }

        return clientSocket;
    }

    /**
     * Retrieves all connected clients of a specific type.
     *
     * @param clientType the type of clients to retrieve (CONTROL_PANEL or GREENHOUSE)
     * @return an ArrayList of sockets for the specified client type
     */
    public ArrayList<Socket> getAllClients(Clients clientType) {
        ArrayList<Socket> clients = new ArrayList<>();

        if (clientType == Clients.GREENHOUSE) {
            clients = this.getGreenhouseNodes();
        } else if (clientType == Clients.CONTROL_PANEL) {
            clients = this.getControlPanels();
        }
        return clients;
    }

    /**
     * Accepts the next client connection.
     *
     * @return the socket representing the client connection, or null if an error occurs
     */
    private Socket acceptNextClientConnection() {
        Socket clientSocket = null;
        try {
            clientSocket = listeningSocket.accept();
        } catch (IOException e) {
            System.err.println("Could not accept client connection: " + e.getMessage());
        }
        return clientSocket;
    }

    /**
     * Opens a listening socket on the specified port.
     *
     * @param port the port number for the server to listen on
     * @return the ServerSocket if successful, or null if an error occurs
     */
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

    /**
     * The entry point for the server thread, calls startServer to initialize the server.
     */
    @Override
    public void run() {
        startServer();
    }
}