package no.ntnu.intermediaryserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import no.ntnu.tools.Logger;

public class ProxyServer implements Runnable {
    public static final int PORT_NUMBER = 50500;
    private boolean isTcpServerRunning;

    // Using ConcurrentHashMap for thread-safe access
    private final ConcurrentHashMap<String, Socket> controlPanels = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Socket> greenhouseNodes = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        new ProxyServer().startServer();
    }

    // Start the server to listen for client connections
    public void startServer() throws IOException {
        ServerSocket listeningSocket = openListeningSocket(PORT_NUMBER);
        if (listeningSocket != null) {
            this.isTcpServerRunning = true;
            while (this.isTcpServerRunning) {
                Socket clientSocket = acceptNextClientConnection(listeningSocket);
                if (clientSocket != null) {
                    new Thread(new ClientHandler(clientSocket, this)).start();
                }
            }
        }
    }

    public void stopServer() {
        this.isTcpServerRunning = false;
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

    private Socket acceptNextClientConnection(ServerSocket listeningSocket) {
        try {
            return listeningSocket.accept();
        } catch (IOException e) {
            System.err.println("Could not accept client connection: " + e.getMessage());
        }
        return null;
    }

    private ServerSocket openListeningSocket(int port) {
        try {
            ServerSocket listeningSocket = new ServerSocket(port);
            System.out.println("Server listening on port " + port);
            return listeningSocket;
        } catch (IOException e) {
            System.err.println("Could not open server socket for port " + port + ": " + e.getMessage());
        }
        return null;
    }

    @Override
    public void run() {
        try {
            this.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
