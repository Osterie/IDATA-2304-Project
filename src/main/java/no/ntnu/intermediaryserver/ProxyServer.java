package no.ntnu.intermediaryserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple TCP server that listens for incoming client connections and handles client requests.
 */
public class ProxyServer implements Runnable {

  // String should be a unique identifier for each client
  private Map<String, ClientHandler> connectedClients = new HashMap<>();
  
  public static final int PORT_NUMBER = 30425;
  public static final String DEFAULT_HOST = "localhost";


  private int currentPortNumber = PORT_NUMBER;

  private boolean isTcpServerRunning;

  /**
   * Create a new TCP server.
   *
   * @param numberOfChannels The total number of channels the TV has
   */
  public ProxyServer(int port) {
    this.currentPortNumber = port;
  }

  public static void main(String[] args) {
    // PortAsker portAsker = new PortAsker();
    // int port = portAsker.getPort();


    ProxyServer server = new ProxyServer(PORT_NUMBER);
    server.startServer();
  }

  public int getPortNumber(){
    return this.currentPortNumber;
  }


  /**
   * Start TCP server for this TV.
   */
  public void startServer() {

    ServerSocket listeningSocket = openListeningSocket(this.currentPortNumber);
    if (listeningSocket != null) {
      this.isTcpServerRunning = true;
      while (this.isTcpServerRunning) {

        Socket clientSocket = acceptNextClientConnection(listeningSocket);
        if (clientSocket != null) {
          // ClientHandler clientHandler = new ClientHandler(clientSocket, identifier);
          // Thread clientThread = new Thread(clientHandler);
          // clientThread.start();
          
          // connectedClients.put(clientHandler.getClientType(), clientHandler);
        }
      }
    }
  }

  // Forward messages between clients
  public void forwardMessage(String message, String targetClientType) {
    ClientHandler targetClient = connectedClients.get(targetClientType);
    if (targetClient != null) {
      targetClient.sendResponseToClient(message);
    }
  }

  public void stopServer() {
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
    this.startServer();
  }
}

