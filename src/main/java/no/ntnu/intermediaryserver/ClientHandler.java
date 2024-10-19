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

    private ClientIdentifier clientIdentifier;
    
    private String clientType;  // "CONTROL_PANEL" or "GREENHOUSE"
    private String clientId;    // Unique ID for the greenhouse node or control panel

    public ClientHandler(Socket socket, IntermediaryServer server) {
        this.clientSocket = socket;
        this.server = server;
        this.clientIdentifier = new ClientIdentifier();
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
        this.identifyClientType();
        this.handleClient();
    }

    private void handleClient() {
        String clientRequest = null; 
        do {
            clientRequest = this.readClientRequest();
            if (clientRequest != null) {
                Logger.info("Received from client: " + clientRequest);
                this.sendToClient(clientRequest);
            }
            else{
                Logger.info("Invalid request from client: " + clientRequest);
            }
        } while (clientRequest != null);
        this.closeStreams();
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

//    /**
//    * Handle a request from the client.
//    *
//    * @param clientRequest The request from the client
//    * @return The response to send back to the client
//    */
//   private String handleClientRequest(String clientRequest) {
//     if (clientRequest == null) {
//       return null;
//     }

//     // TODO use a class to parse the client request
//     // TODO Seperate between header and body

//     String[] parts = clientRequest.split(";");
//     String sender = parts[0];
//     String senderID = parts[1];
//     String command;
//     if (parts.length == 3) {
//       command = parts[2];
//       return sender + ";" + senderID + ";" + command;
//     }

//     return "Handled request: " + clientRequest;
//   }

   /**
   * Send a response from the server to the client, over the TCP socket.
   *
   * @param response The response to send to the client, NOT including the newline
   */
    private void sendToClient(String response) {
        String[] responseParts = response.split(";");
        String target = responseParts[0];
        String targetId = responseParts[1];
        String command = responseParts[2];

        if (targetId.equalsIgnoreCase("ALL")){
            this.sendToAll(target, command);
            return;
        }

        this.sendCommandToClient(target, targetId, command);
    }

    private void sendCommandToClient(String targetClientType, String targetClientId, String command) {
        Socket receiver = server.getClient(targetClientType, targetClientId);
        if (receiver == null) {
            Logger.error(targetClientType + " not found: " + targetClientId);
            return;
        }

        try {
            PrintWriter receiverWriter = new PrintWriter(receiver.getOutputStream(), true);
            Logger.info("Sending response to " + targetClientType + " " + receiver.getRemoteSocketAddress() + ": " + command);
            receiverWriter.println(this.clientType + ";" + this.clientId + ";" + command);
        } catch (IOException e) {
            Logger.error("Failed to send response to " + targetClientType + ": " + e.getMessage());
        }
    }

    private void sendToAll(String targetClientType, String command) {
        ArrayList<Socket> clients = server.getAllClients(targetClientType);
        PrintWriter receiverWriter;

        for (Socket client : clients) {
            try {
                receiverWriter = new PrintWriter(client.getOutputStream(), true);
                Logger.info("Sending response to " + targetClientType + " " + client.getRemoteSocketAddress() + ": " + command);
                receiverWriter.println(this.clientType + ";" + this.clientId + ";" + command);
            } catch (IOException e) {
                Logger.error("Failed to send response to " + targetClientType + ": " + e.getMessage());
            }
        }
    }

    private void identifyClientType() {
        String identification = this.readClientRequest();
        if (identification == null) {
            Logger.error("Error identifying client type");
            return;
        }

        if (!this.processIdentification(identification)) {
            return;
        }
    
        this.addClient();
    }

    private boolean processIdentification(String identification) {
        boolean identificationSuccess = false;
        try{
            this.clientIdentifier.identifyClientType(identification);
            identificationSuccess = true;    
        }
        catch (IllegalArgumentException e){
            Logger.error("Invalid identification message: " + identification);
            this.closeStreams();
        }
        
        this.clientType = this.clientIdentifier.getClientType();
        this.clientId = this.clientIdentifier.getClientId();

        return identificationSuccess;
    }

    private void addClient() {
        try {
            server.addClient(this.clientType, this.clientId, this.clientSocket);
        } catch (UnknownClientException e) {
            Logger.error("Unknown client type: " + this.clientType);
            this.closeStreams();
        }
    }

    private void closeStreams() {
        try {
            this.socketReader.close();
            this.socketWriter.close();
            this.clientSocket.close();
        } catch (IOException e) {
            Logger.error("Could not close reader, writer or socket: " + e.getMessage());
        }
    }
}