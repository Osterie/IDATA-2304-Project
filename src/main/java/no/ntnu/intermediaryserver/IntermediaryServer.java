package no.ntnu.intermediaryserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
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
    private final ConcurrentHashMap<String, Socket> clients = new ConcurrentHashMap<>();
    private ServerSocket listeningSocket;

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
                ClientHandler clientHandler = acceptNextClientConnection();
                if (clientHandler != null) {
                    clientHandler.start();
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

        this.clients.put(clientType + clientId, socket);
        Logger.info("Connected " + clientType + " with ID: " + clientId);
    }

    /**
     * Removes a client from the collection based on client ID and type.
     *
     * @param clientId    the unique identifier for the client
     */
    public synchronized void removeClient(Clients clientType, String clientId) {
        if (this.clients.remove(clientType + clientId) == null) {
            Logger.error("Could not remove client, does not exist: " + clientType + clientId);
        }
        else{
            Logger.info("Disconnected " + clientType + " with ID: " + clientId);
        }
    }
    
    /**
     * Retrieves a specific client socket based on client type and ID.
     *
     * @param clientType the type of client (CONTROL_PANEL or GREENHOUSE)
     * @param clientId   the unique identifier for the client
     * @return the socket connection for the client, or null if not found
     */
    public Socket getClient(Clients clientType, String clientId) {
        return this.clients.get(clientType + clientId);
    }

    public ArrayList<Socket> getClients(Clients clientType){
        ArrayList<Socket> sockets = new ArrayList<>();
        
        for (String key : this.clients.keySet()){
            if (key.startsWith(clientType.getValue())){
                sockets.add(this.clients.get(key));
            }
        }

        return sockets;
    }   

    /**
     * Accepts the next client connection.
     *
     * @return the socket representing the client connection, or null if an error occurs
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