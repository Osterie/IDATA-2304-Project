package no.ntnu.intermediaryserver.clienthandler;

import java.net.Socket;
import java.util.ArrayList;

import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.Transmission;
import no.ntnu.messages.commands.common.ClientIdentificationTransmission;
import no.ntnu.messages.responses.FailureReason;
import no.ntnu.messages.responses.FailureResponse;
import no.ntnu.messages.responses.Response;
import no.ntnu.messages.responses.SuccessResponse;
import no.ntnu.TcpConnection;
import no.ntnu.constants.Endpoints;
import no.ntnu.intermediaryserver.server.IntermediaryServer;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
import no.ntnu.tools.Logger;

/**
 * Handles communication with a client connected to the IntermediaryServer.
 * This class runs in its own thread to manage the client's requests and responses.
 */
public class ClientHandler extends TcpConnection implements Runnable {
    
    private final ClientHandlerLogic logic;
    
    /**
     * Constructs a ClientHandler for a given client socket and server.
     *
     * @param socket The client socket
     * @param server The server managing the connections
     */
    public ClientHandler(Socket socket, IntermediaryServer server) {
        super();
        this.setAutoReconnect(false);
        this.connect(socket);
        this.logic = new ClientHandlerLogic(this, server);
    }

    /**
     * Runs the client handler thread, identifying the client type,
     * and listening for and handling client requests.
     */
    @Override
    public void run() {
        try {
            this.identifyClientType(0);
            this.listenForMessages();
        } catch (Exception e) {
            Logger.error("Error in client handler: " + e.getMessage());
            e.printStackTrace();
        } finally {
            this.close();
        }
    }

    @Override
    protected void handleMessage(Message message){
        this.sendToClient(message);
    }

    /**
     * Sends a message from the server to the client, over the TCP socket.
     *
     * @param message The message from the client
     */
    private void sendToClient(Message message) {
        Logger.info("message: " + message);
        String targetId = message.getHeader().getId();

        if (targetId.equalsIgnoreCase(Endpoints.BROADCAST.getValue())) {
            this.broadcastMessage(message);
        } else {
            this.sendMessageToClient(message);
        }
    }

    /**
     * Broadcasts a message to all clients of a given type.
     * The client type is found in the message header.
     * 
     * @param message The message to broadcast
     */
    private void broadcastMessage(Message message) {
        // Gets the client handlers for the client type specified in the message header.
        ArrayList<ClientHandler> clientHandlers = (ArrayList<ClientHandler>) this.logic.getAllClientHandlers(message.getHeader().getReceiver());
        message.setHeader(this.logic.generateSenderHeader());
        
        Logger.info("Sending message to all clients: " + message);
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.sendMessage(message);
        }
    }

    private void sendMessageToClient(Message message) {
        MessageHeader header = message.getHeader();
        Endpoints receiverClientType = header.getReceiver();
        String receiverId = header.getId();

        ClientHandler receiver = this.logic.getClientHandler(receiverClientType, receiverId);
        try {
            if (receiver == null) {
                Logger.error("Not found: " + receiverClientType + " " + receiverId);
            }
            
            Logger.info("Sending message to " + receiverClientType + " " + receiverId + ": " + message);
            message.setHeader(this.logic.generateSenderHeader());
            receiver.sendMessage(message);
        } catch (Exception e) {
            Logger.error("Could not send message to client: " + e.getMessage());
        }
    }

    // TODO identification should be handle in another way. Another class?
    /**
     * Identifies the type of the client (control panel or greenhouse).
     */
    private void identifyClientType(int attempts) {

        if (attempts > 3) {
            Logger.error("Failed to identify client type after 3 attempts, closing connection");
            this.close();
            return;
        }

        String identification = this.readLine();
        if (identification == null) {
            Logger.error("Error identifying client type, listening for identification message...");
            this.identifyClientType(attempts+1); // TODO check that this works
            return;
        }

        // TODO: Check hashed body content here?
        // TODO rename to incommingMessage
        Message message = Message.fromString(identification);
        Response response = this.processIdentification(message);

        // MessageHeader header = message.getHeader();
        MessageHeader header = new MessageHeader(Endpoints.SERVER, Endpoints.NONE.getValue());
        MessageBody body = message.getBody();

        MessageBody newBody = new MessageBody(response);
        Message responseMessage = new Message(header, newBody);

        if (response == null) {

            Logger.error("Could not identify client type, sending failure response: " + responseMessage);

            this.sendMessage(responseMessage);
            this.identifyClientType(attempts+1);
            return;
        }
        else if (response instanceof FailureResponse) {
            Logger.error("Could not identify client type, sending failure response: " + responseMessage);

            this.sendMessage(responseMessage);
            this.identifyClientType(attempts+1);
            return;
        }
        else if (response instanceof SuccessResponse) {
            message.setBody(new MessageBody(response));
            this.sendMessage(message);
            this.logic.addSelfToServer();
        }
        else {
            Logger.error("Could not identify client type, listening for identification message...");
            this.identifyClientType(attempts+1);
            return;
        }
    }

    // TODO refactor
    /**
     * Processes the identification message from the client.
     *
     * @param identification The identification message
     * @return true if identification was successful, false otherwise
     */
    private Response processIdentification(Message identification) {

        if (identification == null) {
            Logger.error("Invalid identification message: null");
            return new FailureResponse(new ClientIdentificationTransmission(), FailureReason.FAILED_TO_IDENTIFY_CLIENT);
        }

        Response response;
        MessageBody body = identification.getBody();
        Transmission command = body.getTransmission();

        if (command instanceof ClientIdentificationTransmission) {
            ClientIdentificationTransmission clientIdentificationCommand = (ClientIdentificationTransmission) command;
            Endpoints clientType = clientIdentificationCommand.getClient();
            String clientId = clientIdentificationCommand.getId();

            if (clientId.equals(Endpoints.NOT_PREDEFINED.getValue())) {
                clientId = this.socket.getRemoteSocketAddress().toString();
            }
            
            this.logic.setClientIdentification(new ClientIdentification(clientType, clientId));
            response = new SuccessResponse(command, "Identification successful");
        }
        else{
            Logger.error("Invalid identification message: " + identification);
            response = new FailureResponse(new ClientIdentificationTransmission(), FailureReason.FAILED_TO_IDENTIFY_CLIENT);
        }

        return response;
    }
}