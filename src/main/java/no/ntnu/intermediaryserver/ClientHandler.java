package no.ntnu.intermediaryserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import no.ntnu.tools.Logger;

public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private final IntermediaryServer server;
    private BufferedReader socketReader;
    private PrintWriter socketWriter;
    
    private String clientType;  // "CONTROL_PANEL" or "GREENHOUSE"
    private String clientId;    // Unique ID for the greenhouse node or control panel

    public ClientHandler(Socket socket, IntermediaryServer server) {
        this.clientSocket = socket;
        this.server = server;
        initializeStreams();
    }

    private void initializeStreams() {
        try {
            clientSocket.setKeepAlive(true); // Enable keep-alive on the socket
            this.socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.socketWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            Logger.info("New client connected from " + clientSocket.getRemoteSocketAddress());
        } catch (IOException e) {
            Logger.error("Could not open reader or writer: " + e.getMessage());
        }
    }
    
    @Override
    public void run() {
        identifyClientType();
        handleClient();
    }

    private void identifyClientType() {
        try {
            String identification = this.socketReader.readLine();
            String[] parts = identification.split(";");
            this.clientType = parts[0];
            clientId = parts.length > 1 ? parts[1] : null;

            if ("CONTROL_PANEL".equalsIgnoreCase(this.clientType) && clientId != null) {
                server.addControlPanel(clientId, clientSocket);
                Logger.info("Connected Control Panel with ID: " + clientId);
            } else if ("GREENHOUSE".equalsIgnoreCase(this.clientType) && clientId != null) {
                server.addGreenhouseNode(clientId, clientSocket);
                Logger.info("Connected Greenhouse Node with ID: " + clientId);
            } else {
                Logger.info("Unknown client type. Closing connection. Received: " + identification + " Client type: " + this.clientType + " Client ID: " + clientId);
                clientSocket.close();
            }
        } catch (IOException e) {
            Logger.error("Error identifying client type: " + e.getMessage());
        }
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

    String[] parts = clientRequest.split(";");
    String sender = parts[0];
    String senderID = parts[1];
    String command;
    if (parts.length == 3) {
      command = parts[2];
      return sender + ";" + senderID + ";" + command;
    }

    return "Handled request: " + clientRequest;
  }

    /**
   * Send a response from the server to the client, over the TCP socket.
   *
   * @param response The response to send to the client, NOT including the newline
   */
    private void sendResponseToClient(String response) {
        Logger.info(">>> " + response);
        String[] responseParts = response.split(";");
        if (responseParts.length == 2){
            Logger.info("Received identifier message...");
            return;
        } 

        String target = responseParts[0];
        String targetId = responseParts[1];
        String command = responseParts[2];
        PrintWriter receiverWriter;


        if (targetId.equalsIgnoreCase("ALL")){
            ArrayList<String> nodeIds = server.getGreenhouseNodeIds();
            String nodeIdsString = String.join(";", nodeIds);
            this.socketWriter.println(nodeIdsString);
            // for (Socket nodeSocket : server.getGreenhouseNodes()) {
            //     try {
            //         receiverWriter = new PrintWriter(nodeSocket.getOutputStream(), true);
            //         Logger.info("Sending response to greenhouse node " + nodeSocket.getRemoteSocketAddress() + ": " + command);
            //         receiverWriter.println(this.clientType + ";" + this.clientId + ";" + command);
            //     } catch (IOException e) {
            //         Logger.error("Failed to send response to greenhouse node: " + e.getMessage());
            //     }
            // }
            return;
        }
        
        if ("CONTROL_PANEL".equalsIgnoreCase(target)) {
            Socket controlPanelSocket = server.getControlPanel(targetId);
            if (controlPanelSocket != null) {
                try {
                    receiverWriter = new PrintWriter(controlPanelSocket.getOutputStream(), true);
                    Logger.info("Sending response to control panel " + targetId + ": " + command);
                    receiverWriter.println(this.clientType + ";" + this.clientId + ";" + command);
                } catch (IOException e) {
                    Logger.error("Failed to send response to control panel: " + e.getMessage());
                }
            } else {
                Logger.error("Control panel not found: " + targetId);
            }
        } else if ("GREENHOUSE".equalsIgnoreCase(target)) {
            Socket greenhouseNodeSocket = server.getGreenhouseNode(targetId);
            if (greenhouseNodeSocket != null) {
                try {
                    receiverWriter = new PrintWriter(greenhouseNodeSocket.getOutputStream(), true);
                    Logger.info("Sending response to greenhouse node " + targetId + ": " + command);
                    receiverWriter.println(this.clientType + ";" + this.clientId + ";" + command);
                } catch (IOException e) {
                    Logger.error("Failed to send response to greenhouse node: " + e.getMessage());
                }
            } else {
                Logger.error("Greenhouse node not found: " + targetId);
            }
        }
        // this.socketWriter.println(response);
    }

    private void handleClient() {
        String response; 
        do {
        String clientRequest = readClientRequest();
        Logger.info("Received from client: " + clientRequest);
        response = this.handleClientRequest(clientRequest);
        if (response != null) {
            this.sendResponseToClient(response);
        }
        else{
            Logger.info("Invalid request from client: " + clientRequest);
        }
        } while (response != null);
        Logger.info("Client disconnected");
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
        Logger.error("Could not receive client request: " + e.getMessage());
        }
        return clientRequest;
    }
}