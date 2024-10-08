package no.ntnu.intermediaryserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import no.ntnu.tools.Logger;

public class ProxyServer implements Runnable {
    public static final int PORT_NUMBER = 50500;
    private boolean isTcpServerRunning;
    
    // Maps to store connected Control Panels and Greenhouse nodes
    private Map<String, Socket> controlPanels = new HashMap<>();
    private Map<String, Socket> greenhouseNodes = new HashMap<>();

    public static void main(String[] args) throws IOException {
        new ProxyServer().startServer();
    }

    // Method to start the server and accept client connections
    public void startServer() throws IOException {


        ServerSocket listeningSocket = openListeningSocket(PORT_NUMBER);
        if (listeningSocket != null) {
            this.isTcpServerRunning = true;
            while (this.isTcpServerRunning) {
                Socket clientSocket = acceptNextClientConnection(listeningSocket);
                if (clientSocket != null) {
                  new Thread (new ClientHandler(clientSocket)).start();
      
                }
            }
        }



        // try (ServerSocket serverSocket = new ServerSocket(PORT_NUMBER)) {
        //     System.out.println("Proxy Server is running on port " + PORT_NUMBER);
        //     while (true) {
        //         Socket clientSocket = serverSocket.accept();
        //         Logger.info("Client connected: " + clientSocket.getInetAddress().getHostAddress());
        //         // Start a new ClientHandler for every connected client
        //         new ClientHandler(clientSocket, controlPanels, greenhouseNodes).start();
        //     }
        // }
    }

    public void stopServer(){
        this.isTcpServerRunning = false;
    }

    /**
     * Accept the next client connection on the given server socket.
     *
     * @param listeningSocket The server socket to accept client connections on
     * @return The new client socket, or null on error
     */
    private Socket acceptNextClientConnection(ServerSocket listeningSocket) {
        Socket clientSocket = null;
        try {
        clientSocket = listeningSocket.accept();
        } catch (IOException e) {
        System.err.println("Could not accept client connection: " + e.getMessage());
        }
        return clientSocket;
    }

    /**
   * Open a server socket that listens for incoming client connections.
   */
    private ServerSocket openListeningSocket(int port) {
        ServerSocket listeningSocket = null;
        try {
        listeningSocket = new ServerSocket(port);
        System.out.println("Server listening on port " + port);
        } catch (IOException e) {
        System.err.println("Could not open server socket for port " + port + ": " + e.getMessage());
        }
        return listeningSocket;
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
