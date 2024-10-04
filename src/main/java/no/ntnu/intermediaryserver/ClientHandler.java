package no.ntnu.intermediaryserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

// import no.ntnu.message.CommandTranslator;
// import no.ntnu.message.ErrorMessage;
// import no.ntnu.message.Message;
// import no.ntnu.message.ResponseMessage;
// import no.ntnu.message.command.Command;


/**
 * A simple TCP server that listens for incoming client connections and handles client requests.
 */
public class ClientHandler implements Runnable{

  
  private BufferedReader socketReader;
  private PrintWriter socketWriter;
  // private CommandTranslator messageSerializer;
  private String clientType;


  /**
   * Create a new TCP server.
   *
   * @param numberOfChannels The total number of channels the TV has
   */
  public ClientHandler(Socket clientSocket) {
    this.setClientSocket(clientSocket);
    // this.messageSerializer = new CommandTranslator();
    this.startReaderAndWriter(clientSocket);
    this.clientType = identifyClientType(); // Add this method to determine client type
  }

  public void setClientSocket(Socket clientSocket){
    if (clientSocket == null) {
      throw new IllegalArgumentException("Could not accept client connection");
    }

    try {
      // Set a timeout to avoid a client keeping the line busy.
      clientSocket.setSoTimeout(5000); 
    } catch (SocketException e) {
      System.err.println("Failed to set socket timeout: " + e.getMessage());
    }
  }

  @Override
  public void run() {
    handleClient();
  }

  public void startReaderAndWriter(Socket clientSocket){
    try{
        this.socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.socketWriter = new PrintWriter(clientSocket.getOutputStream(), true);
        System.out.println("New client connected from " + clientSocket.getRemoteSocketAddress());
    }
    catch (IOException e) {
      System.err.println("Could not open reader or writer: " + e.getMessage());
    }
  }

  /**
   * Handle the client connection: read and respond to client messages.
   */
  public void handleClient() {
    // Message response; 
    String response;
    do {
      String clientRequest = readClientRequest();
      System.out.println("Received from client: " + clientRequest);
      response = this.handleClientRequest(clientRequest);
      if (response != null) {
        this.sendResponseToClient(response);
      }
      else{
        System.out.println("Invalid request from client: " + clientRequest);
      }
    } while (response != null);
    System.out.println("Client disconnected");

  }

  /**
   * Read one message from the TCP socket - from the client.
   *
   * @return The received client message, or null on error
   */
  private String readClientRequest() {
    String clientRequest = null;
    try {
      clientRequest = this.socketReader.readLine();
    } catch (IOException e) {
      System.err.println("Could not receive client request: " + e.getMessage());
    }
    return clientRequest;
  }


  /**
   * Handle a request from the client.
   *
   * @param clientRequest The request from the client
   * @return The response to send back to the client
   */
  private String handleClientRequest(String clientRequest) {
    if (clientRequest == null) {
      return null;
    }

    return clientRequest;

    // Message request = this.messageSerializer.toMessage(clientRequest);

    // if (request instanceof Command) {
    //   System.out.println("Handling command: " + clientRequest);
    //   return this.smartTv.handleClientRequest((Command) request);
    // }
    // else{
    //   System.out.println("Invalid request from client, is not command: " + clientRequest);
    //   return null;
    // }
  }

  /**
   * Send a response from the server to the client, over the TCP socket.
   *
   * @param response The response to send to the client, NOT including the newline
   */
  public void sendResponseToClient(String response) {
    this.socketWriter.println(response);
    // String serializedResponse = this.messageSerializer.toString(response);
    // System.out.println(serializedResponse);
    // this.socketWriter.println(serializedResponse);
  }


    /**
   * Identify the client type (greenhouse or control panel).
   * @return The client type
   */
  private String identifyClientType() {
    return "GREENHOUSE";
    // try {
    //   String clientType = this.socketReader.readLine();
    //   System.out.println("Client identified as: " + clientType);
    //   return clientType;  // Expect "GREENHOUSE" or "CONTROL_PANEL"
    // } catch (IOException e) {
    //   System.err.println("Failed to identify client type: " + e.getMessage());
    // }
    // return null;
  }

}
