package no.ntnu.intermediaryserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

import no.ntnu.commands.Command;
import no.ntnu.commands.Message;
import no.ntnu.tools.Logger;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private BufferedReader socketReader;
    private PrintWriter socketWriter;



    
    // Reference to the maps for storing control panels and greenhouses
    // private Map<String, Socket> controlPanels;
    // private Map<String, Socket> greenhouseNodes;

    // Constructor to initialize client handler with socket and maps
    // public ClientHandler(Socket socket, Map<String, Socket> controlPanels, Map<String, Socket> greenhouseNodes) {
    //     this.clientSocket = socket;
    //     this.controlPanels = controlPanels;
    //     this.greenhouseNodes = greenhouseNodes;
    // }

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;

        if (clientSocket == null) {
            throw new IllegalArgumentException("Could not accept client connection");
        }

        Logger.info("ClientHandler created");

    // try {
    //   // Set a timeout to avoid a client keeping the line busy.
    //   clientSocket.setSoTimeout(5000); 
    // } catch (SocketException e) {
    //   System.err.println("Failed to set socket timeout: " + e.getMessage());
    // }

        this.startReaderAndWriter(clientSocket);



        // this.controlPanels = controlPanels;
        // this.greenhouseNodes = greenhouseNodes;
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
    


    @Override
    public void run() {
      this.handleClient();
    }

    /**
     * Handle the client connection: read and respond to client messages.
     */
    public void handleClient() {
        String response;
        // Message response;
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
    private void sendResponseToClient(String response) {
        // String serializedResponse = this.messageSerializer.toString(response);
        String serializedResponse = response;
        System.out.println(serializedResponse);
        this.socketWriter.println(serializedResponse);
    } 

    
    // // Handle communication with a greenhouse client
    // private void handleGreenhouse(String greenhouseId) throws IOException {
    //     String request;
    //     while ((request = socketReader.readLine()) != null) {
    //         // Greenhouse listens for commands or requests
    //         System.out.println("Received request/command for Greenhouse " + greenhouseId + ": " + request);
    //     }
    // }

    // // Handle communication with a control panel client
    // private void handleControlPanel(String controlPanelId) throws IOException {
    //     String request;
    //     while ((request = socketReader.readLine()) != null) {
    //         System.out.println("Control Panel " + controlPanelId + " is requesting data.");

    //         // Forward the request to the relevant greenhouse node
    //         Socket greenhouseSocket = greenhouseNodes.get("greenhouse1"); // Assuming a single greenhouse for now
    //         if (greenhouseSocket != null) {
    //             PrintWriter greenhouseWriter = new PrintWriter(greenhouseSocket.getOutputStream(), true);
    //             BufferedReader greenhouseReader = new BufferedReader(new InputStreamReader(greenhouseSocket.getInputStream()));

    //             // Request sensor data from the greenhouse
    //             greenhouseWriter.println("REQUEST_SENSOR_DATA");
    //             String sensorData = greenhouseReader.readLine(); // Get the sensor data from the greenhouse

    //             // Send the sensor data back to the control panel
    //             socketWriter.println(sensorData);
    //         } else {
    //             socketWriter.println("No greenhouse connected.");
    //         }
    //     }
    // }

}
