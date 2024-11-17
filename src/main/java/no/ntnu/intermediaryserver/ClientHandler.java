package no.ntnu.intermediaryserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import no.ntnu.Clients;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.Message;
import no.ntnu.tools.Logger;

/**
 * Handles communication with a client connected to the IntermediaryServer.
 * This class runs in its own thread to manage the client's requests and responses.
 */
public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private final IntermediaryServer server;
    private BufferedReader socketReader;
    private PrintWriter socketWriter;

    private ClientIdentifier clientIdentifier;
    
    private Clients clientType;  // Clients.CONTROL_PANEL or Clients.GREENHOUSE
    private String clientId;    // Unique ID for the greenhouse node or control panel

    /**
     * Constructs a ClientHandler for a given client socket and server.
     *
     * @param socket The client socket
     * @param server The server managing the connections
     */
    public ClientHandler(Socket socket, IntermediaryServer server) {
        this.clientSocket = socket;
        this.server = server;
        this.clientIdentifier = new ClientIdentifier();
        this.initializeStreams();
    }

    /**
     * Initializes the input and output streams for the client socket.
     */
    private void initializeStreams() {
        try {
            this.clientSocket.setKeepAlive(true); // Enable keep-alive on the socket
            this.socketReader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            this.socketWriter = new PrintWriter(this.clientSocket.getOutputStream(), true);
            Logger.info("New client connected from " + this.clientSocket.getRemoteSocketAddress());
        } catch (IOException e) {
            Logger.error("Could not open reader or writer: " + e.getMessage());
        }
    }

    /**
     * Runs the client handler thread, identifying the client type and handling client requests.
     */
    @Override
    public void run() {
        this.identifyClientType();
        this.handleClient();
    }

    /**
     * Handles client requests in a loop until the client disconnects.
     */
    private void handleClient() {
        String clientRequest = null; 
        do {
            clientRequest = this.readClientRequest();
            if (clientRequest != null) {
                Logger.info("Received from client: " + clientRequest);
                this.sendToClient(clientRequest);
            } else {
                Logger.info("Invalid request from client: " + clientRequest);
            }
        } while (clientRequest != null);
        this.closeStreams();
        Logger.info("Client disconnected");
    }

    /**
     * Reads one message from the TCP socket - from the client.
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

    /**
     * Sends a message from the server to the client, over the TCP socket.
     *
     * @param request The request from the client
     */
    private void sendToClient(String request) {
        Logger.info("request: " + request);
        Message message = Message.fromProtocolString(request);
        String targetId = message.getHeader().getId();

        if (targetId.equalsIgnoreCase("ALL")) {
            this.sendMessageToAllClients(message);
        }
        else{
            this.sendMessageToClient(message);
        }
    }

    private void sendMessageToAllClients(Message message) {
        ArrayList<Socket> clients = this.getAllClients(message.getHeader());
        message.setHeader(this.generateNewHeader());
        
        
        // PrintWriter receiverWriter;
        Logger.info("Sending message to all clients: " + message.toProtocolString());
        for (Socket client : clients) {
            this.sendMessage(message, client);
        }
    }

    private void sendMessageToClient(Message message) {
        Socket receiver = this.getClient(message.getHeader());
        message.setHeader(this.generateNewHeader());


        if (receiver == null) {
            Logger.error("Not found: " + message.getHeader().getReceiver() + " " + message.getHeader().getId());
            return;
        }

        // Logger.info("Sending message to " + message.getHeader().getReceiver() + " " + message.getHeader().getId() + ": " + message.toProtocolString());
        this.sendMessage(message, receiver);
    }

    private void sendMessage(Message message, Socket receiver) {
        if (message == null) {
            Logger.error("Message is null");
            return;
        }
        try {
            PrintWriter receiverWriter = new PrintWriter(receiver.getOutputStream(), true);
            // Logger.info("Sending response to " + targetClientType + " " + receiver.getRemoteSocketAddress() + ": " + message.toProtocolString());

            receiverWriter.println(message.toProtocolString());
        } catch (IOException e) {
            // Logger.error("Failed to send response to " + targetClientType + ": " + e.getMessage());
        }
    }

    private MessageHeader generateNewHeader(){
        return new MessageHeader(this.clientType, this.clientId);
    }

    private Socket getClient(MessageHeader header){
        return server.getClient(header.getReceiver(), header.getId());
    }

    private ArrayList<Socket> getAllClients(MessageHeader header){
        return server.getAllClients(header.getReceiver());
    }

    /**
     * Identifies the type of the client (control panel or greenhouse).
     */
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

    /**
     * Processes the identification message from the client.
     *
     * @param identification The identification message
     * @return true if identification was successful, false otherwise
     */
    private boolean processIdentification(String identification) {
        boolean identificationSuccess = false;
        try {
            this.clientIdentifier.identifyClientType(identification);
            identificationSuccess = true;    
        } catch (IllegalArgumentException e) {
            Logger.error("Invalid identification message: " + identification);
            this.closeStreams();
        }
        
        this.clientType = this.clientIdentifier.getClientType();
        this.clientId = this.clientIdentifier.getClientId();

        return identificationSuccess;
    }

    /**
     * Adds the client to the server's list of clients.
     */
    private void addClient() {
        try {
            server.addClient(this.clientType, this.clientId, this.clientSocket);
        } catch (UnknownClientException e) {
            Logger.error("Unknown client type: " + this.clientType);
            this.closeStreams();
        }
    }

    /**
     * Closes the input and output streams and the client socket.
     */
    private void closeStreams() {
        try {
            Logger.info("Closing reader, writer and socket");
            this.socketReader.close();
            this.socketWriter.close();
            this.clientSocket.close();
        } catch (IOException e) {
            Logger.error("Could not close reader, writer or socket: " + e.getMessage());
        }
    }
}