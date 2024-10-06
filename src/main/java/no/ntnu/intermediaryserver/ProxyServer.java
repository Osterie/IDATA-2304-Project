package no.ntnu.intermediaryserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ProxyServer implements Runnable {
    public static final int PORT_NUMBER = 30425;
    
    // Maps to store connected Control Panels and Greenhouse nodes
    private Map<String, Socket> controlPanels = new HashMap<>();
    private Map<String, Socket> greenhouseNodes = new HashMap<>();

    public static void main(String[] args) throws IOException {
        new ProxyServer().startServer();
    }

    // Method to start the server and accept client connections
    public void startServer() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT_NUMBER)) {
            System.out.println("Proxy Server is running on port " + PORT_NUMBER);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                // Start a new ClientHandler for every connected client
                new ClientHandler(clientSocket, controlPanels, greenhouseNodes).start();
            }
        }
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
