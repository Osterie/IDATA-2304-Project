package no.ntnu.intermediaryserver.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import no.ntnu.constants.Endpoints;
import no.ntnu.intermediaryserver.clienthandler.ClientHandler;
import no.ntnu.tools.Logger;

/**
 * The IntermediaryServer class is responsible for managing the connections
 * between greenhouse nodes and control panels. It listens for incoming client
 * connections, then assigns each client to a handler thread for processing.
 */
public class IntermediaryServer implements Runnable {
    private boolean isTcpServerRunning;

    // Thread-safe collections for managing client sockets
    private final ConcurrentHashMap<String, ClientHandler> clientHandlers = new ConcurrentHashMap<>();
    private ServerSocket listeningSocket;

    /**
     * Starts the server and listens for incoming client connections on the
     * specified port.
     * Creates a new thread to handle each client connection.
     */
    public void startServer() {
        listeningSocket = this.openListeningSocket();
        if (listeningSocket != null) {
            this.isTcpServerRunning = true;
            Logger.info("Server started on port " + listeningSocket.getLocalPort());

            // TODO refactor.
            // Runs the whole time while application is up
            while (isTcpServerRunning) {
                try {

                    // Accepts the next client connection
                    ClientHandler clientHandler = acceptNextClientConnection();
                    if (clientHandler != null) {
                        new Thread(clientHandler).start();
                    }
                } catch (Exception e) {
                    Logger.error("Error in server loop: " + e.getMessage());
                }
                this.delay(10);
            }
        }
    }

    private void delay(int delay) {
        // Add a small delay to prevent excessive CPU usage
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Logger.error("Error sleeping server thread: " + e.getMessage());
            Thread.currentThread().interrupt();
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
     * @param clientType    the type of client (CONTROL_PANEL or GREENHOUSE)
     * @param clientId      the unique identifier for the client
     * @param clientHandler the client handler for the client
     * @throws UnknownClientException if the client type is not recognized
     */
    public synchronized void addClientHandler(Endpoints clientType, String clientId, ClientHandler clientHandler) {
        this.clientHandlers.put(clientType + clientId, clientHandler);
        Logger.info("Connected " + clientType + " with ID: " + clientId);
    }

    /**
     * Removes a client from the collection based on client ID and type.
     *
     * @param clientId the unique identifier for the client
     */
    public synchronized void removeClientHandler(Endpoints clientType, String clientId) {
        if (this.clientHandlers.remove(clientType + clientId) == null) {
            Logger.error("Could not remove client, does not exist: " + clientType + clientId);
        } else {
            Logger.info("Disconnected " + clientType + " with ID: " + clientId);
        }
    }

    /**
     * Retrieves a specific client socket based on client type and ID.
     *
     * @param clientType the type of client (CONTROL_PANEL or GREENHOUSE)
     * @param clientId   the unique identifier for the client
     * @return the client handler for the client, or null if not found
     */
    public ClientHandler getClientHandler(Endpoints clientType, String clientId) {
        return this.clientHandlers.get(clientType + clientId);
    }

    public List<ClientHandler> getClientHandlers(Endpoints clientType) {
        ArrayList<ClientHandler> sockets = new ArrayList<>();

        this.clientHandlers.forEach((key, value) -> {
            if (key.startsWith(clientType.getValue())) {
                sockets.add(value);
            }
        });

        return sockets;
    }

    /**
     * Accepts the next client connection.
     *
     * @return the socket representing the client connection, or null if an error
     *         occurs
     */
    private ClientHandler acceptNextClientConnection() {
        ClientHandler clientHandler = null;
        try {
            Socket clientSocket = listeningSocket.accept();
            Logger.info("New client connected from " + clientSocket.getRemoteSocketAddress());
            clientHandler = new ClientHandler(clientSocket, this);
        } catch (IOException e) {
            Logger.error("Could not accept client connection: " + e.getMessage());
        }
        return clientHandler;
    }

    /**
     * Opens a listening socket on an available port.
     *
     * @return the ServerSocket if successful, or null if an error occurs
     */
    private ServerSocket openListeningSocket() {
        return ServerSocketCreator.getAvailableServerSocket();
    }

    /**
     * The entry point for the server thread, calls startServer to initialize the
     * server.
     */
    @Override
    public void run() {
        startServer();
    }
}