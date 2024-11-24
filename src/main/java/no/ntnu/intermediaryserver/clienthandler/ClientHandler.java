package no.ntnu.intermediaryserver.clienthandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.Transmission;
import no.ntnu.messages.commands.common.ClientIdentificationTransmission;
import no.ntnu.constants.Endpoints;
import no.ntnu.intermediaryserver.server.IntermediaryServer;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
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

    private ClientIdentification clientIdentification;
    
    /**
     * Constructs a ClientHandler for a given client socket and server.
     *
     * @param socket The client socket
     * @param server The server managing the connections
     */
    public ClientHandler(Socket socket, IntermediaryServer server) {
        this.clientSocket = socket;
        this.server = server;
        this.initializeStreams();
    }

    /**
     * Initializes the input and output streams for the client socket.
     */
    private void initializeStreams() {
        try {
            if (this.clientSocket != null) {
                this.clientSocket.setKeepAlive(true); // Enable keep-alive on the socket
                this.socketReader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
                this.socketWriter = new PrintWriter(this.clientSocket.getOutputStream(), true);
                Logger.info("New client connected from " + this.clientSocket.getRemoteSocketAddress());
            } else {
                Logger.error("Client socket is null");
            }
        } catch (IOException e) {
            Logger.error("Could not open reader or writer: " + e.getMessage());
            this.closeStreams();
        }
    }

    /**
     * Runs the client handler thread, identifying the client type and handling client requests.
     */
    @Override
    public void run() {
        try {
            this.identifyClientType(0);
            this.handleClient();
        } catch (Exception e) {
            Logger.error("Error in client handler: " + e.getMessage());
            e.printStackTrace();
        } finally {
            this.closeStreams();
        }
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
            if (this.socketReader != null) {
                clientRequest = this.socketReader.readLine();
            } else {
                Logger.error("Socket reader is null");
            }
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

        // TODO change this
        if (targetId.equalsIgnoreCase("BROADCAST")) {
            this.broadcastMessage(message);
        } else {
            boolean success = this.sendMessageToClient(message);
            //try {

            //    if (success) {
            //        SuccessCommand successCommand = new SuccessCommand("Operation completed successfully");
            //        this.sendMessage(new Message(successCommand.toProtocolString()), this.clientSocket);
            //    } else {
            //    FailureCommand failureCommand = new FailureCommand("Operation failed");
            //        this.sendMessage(new Message(failureCommand.toProtocolString()), this.clientSocket);
            //   }
            //} catch (Exception e) {
            //    FailureCommand failureCommand = new FailureCommand("Operation failed: " + e.getMessage());
            //    this.sendMessage(new Message(failureCommand.toProtocolString()), this.clientSocket);
            //}
        }
    }

    private void broadcastMessage(Message message) {
        // Gets the client handlers for the client type specified in the message header.
        ArrayList<ClientHandler> clientHandlers = this.getAllClientHandlers(message.getHeader());
        message.setHeader(this.generateNewHeader());
        
        Logger.info("Sending message to all clients: " + message.toProtocolString());
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.sendMessage(message);
        }
    }

    private boolean sendMessageToClient(Message message) {
        ClientHandler receiver = this.getClientHandler(message.getHeader());
        message.setHeader(this.generateNewHeader());
        try {
            if (receiver == null) {
                Logger.error("Not found: " + message.getHeader().getReceiver() + " " + message.getHeader().getId());
                return false;
            }

            Logger.info("Sending message to " + message.getHeader().getReceiver() + " " + message.getHeader().getId() + ": " + message.toProtocolString());
            receiver.sendMessage(message);
            return true;
        } catch (Exception e) {
            Logger.error("Could not send message to client: " + e.getMessage());
            return false;
        }
    }

    private void sendMessage(Message message) {
        if (message == null) {
            Logger.error("Message is null");
            return;
        } 
        this.socketWriter.println(message.toProtocolString());
    }

    private MessageHeader generateNewHeader(){
        return new MessageHeader(this.getClientType(), this.getClientId());
    }

    private ClientHandler getClientHandler(MessageHeader header){
        return server.getClient(header.getReceiver(), header.getId());
    }

    private ArrayList<ClientHandler> getAllClientHandlers(MessageHeader header){
        return server.getClients(header.getReceiver());
    }

    private Endpoints getClientType(){
        return this.clientIdentification.getClientType();
    }

    private String getClientId(){
        return this.clientIdentification.getClientId();
    }

    /**
     * Identifies the type of the client (control panel or greenhouse).
     */
    private void identifyClientType(int attempts) {

        if (attempts > 3) {
            Logger.error("Failed to identify client type after 3 attempts, closing connection");
            this.closeStreams();
            return;
        }

        String identification = this.readClientRequest();
        if (identification == null) {
            Logger.error("Error identifying client type, listening for identification message...");
            this.identifyClientType(attempts+1); // TODO check that this works
            return;
        }

        if (!this.processIdentification(identification)) {
            Logger.error("Could not identify client type, listening for identification message...");
            this.identifyClientType(attempts+1);
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

        boolean success = false;

        Message message = Message.fromProtocolString(identification);
        if (message == null) {
            Logger.error("Invalid identification message: " + identification);
            this.closeStreams();
            return false;
        }

        MessageBody body = message.getBody();
        Transmission command = body.getTransmission();

        if (command instanceof ClientIdentificationTransmission) {
            ClientIdentificationTransmission clientIdentificationCommand = (ClientIdentificationTransmission) command;
            Endpoints clientType = clientIdentificationCommand.getClient();
            String clientId = clientIdentificationCommand.getId();
            if (clientId.equals("?")) {
                clientId = this.clientSocket.getRemoteSocketAddress().toString();
            }
            this.clientIdentification = new ClientIdentification(clientType, clientId);
            success = true;
        }
        else{
            Logger.error("Invalid identification message: " + identification);
            this.closeStreams();
        }

        return success;


        // boolean identificationSuccess = false;
        // try {
        //     this.clientIdentifier.identifyClientType(identification);
        //     identificationSuccess = true;    
        // } catch (IllegalArgumentException e) {
        //     Logger.error("Invalid identification message: " + identification);
        //     this.closeStreams();
        // }
        
        // this.clientType = this.clientIdentifier.getClientType();
        // this.clientId = this.clientIdentifier.getClientId();

        // return identificationSuccess;
    }

    /**
     * Adds the client to the server's list of clients.
     */
    private void addClient() {
        try {
            server.addClient(this.getClientType(), this.getClientId(), this);
        } catch (UnknownClientException e) {
            Logger.error("Unknown client type: " + this.getClientType());
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