package no.ntnu.intermediaryserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import no.ntnu.Clients;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.MessageTest;
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
        initializeStreams();
    }

    /**
     * Initializes the input and output streams for the client socket.
     */
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
        MessageTest message = MessageTest.fromProtocolString(request);

        MessageHeader header = message.getHeader();
        MessageBody body = message.getBody();

        Clients target = header.getReceiver();
        String targetId = header.getId();
        String bodyString = body.toProtocolString();

        if (targetId.equalsIgnoreCase("ALL")) {
            this.sendToAll(target, bodyString);
            return;
        }

        this.sendCommandToClient(target, targetId, bodyString);
    }

    /**
     * Sends a command to a specific client.
     *
     * @param targetClientType The type of the target client
     * @param targetClientId The ID of the target client
     * @param body The body of the message to send
     */
    private void sendCommandToClient(Clients targetClientType, String targetClientId, String body) {
        Socket receiver = server.getClient(targetClientType, targetClientId);
        if (receiver == null) {
            Logger.error(targetClientType + " not found: " + targetClientId);
            return;
        }

        String header = this.clientType + ";" + this.clientId;
        String message = header + "-" + body;

        try {
            PrintWriter receiverWriter = new PrintWriter(receiver.getOutputStream(), true);
            Logger.info("Sending response to " + targetClientType + " " + receiver.getRemoteSocketAddress() + ": " + body);
            receiverWriter.println(message);
        } catch (IOException e) {
            Logger.error("Failed to send response to " + targetClientType + ": " + e.getMessage());
        }
    }

    /**
     * Sends a message to all clients of a specific type.
     *
     * @param targetClientType The type of the target clients
     * @param body The body of the message to send
     */
    private void sendToAll(Clients targetClientType, String body) {
        ArrayList<Socket> clients = server.getAllClients(targetClientType);
        PrintWriter receiverWriter;

        MessageHeader header = new MessageHeader(this.clientType, this.clientId);
        MessageBody messageBody = new MessageBody(body);
        MessageTest message = new MessageTest(header, messageBody);
        
        for (Socket client : clients) {
            try {
                receiverWriter = new PrintWriter(client.getOutputStream(), true);
                Logger.info("Sending response to " + targetClientType + " " + client.getRemoteSocketAddress() + ": " + body);
                receiverWriter.println(message.toProtocolString());
            } catch (IOException e) {
                Logger.error("Failed to send response to " + targetClientType + ": " + e.getMessage());
            }
        }
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
            this.socketReader.close();
            this.socketWriter.close();
            this.clientSocket.close();
        } catch (IOException e) {
            Logger.error("Could not close reader, writer or socket: " + e.getMessage());
        }
    }
}