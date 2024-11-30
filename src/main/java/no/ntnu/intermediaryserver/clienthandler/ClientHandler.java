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
 * This class runs in its own thread to manage the client's requests and
 * responses.
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

    /**
     * Handes a message received from the client.
     * handles the message by sending it to the client specified in the header of
     * the message.
     */
    @Override
    protected void handleMessage(Message message) {
        this.sendToClient(message);
    }

    /**
     * Sends a message from the server to the client, over the TCP socket.
     *
     * @param message The message from the client
     */
    private void sendToClient(Message message) {
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
        ArrayList<ClientHandler> clientHandlers = (ArrayList<ClientHandler>) this.logic
                .getAllClientHandlers(message.getHeader().getReceiver());
        message.setHeader(this.logic.generateSenderHeader());

        Logger.info("Sending message to all clients: " + message);
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.sendMessage(message);
        }
    }

    /**
     * Sends a message to a specific client.
     * 
     * @param message The message to send
     */
    private void sendMessageToClient(Message message) {
        ClientHandler receiver = this.getClientHandler(message);
        if (receiver == null) {
            return;
        }

        try {
            message.setHeader(this.logic.generateSenderHeader());
            receiver.sendMessage(message);
        } catch (Exception e) {
            Logger.error("Could not send message to client: " + e.getMessage());
        }
    }

    /**
     * Retrieves the client handler for the client specified in the message header.
     * 
     * @param message The message containing the client type and ID
     * @return The client handler for the client specified in the message header
     */
    private ClientHandler getClientHandler(Message message) {
        MessageHeader header = message.getHeader();
        Endpoints clientType = header.getReceiver();
        String clientId = header.getId();

        ClientHandler client = this.logic.getClientHandler(clientType, clientId);

        if (client == null) {
            Logger.error("Not found: " + clientType + " " + clientId);
        }

        return client;
    }

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
        Response response = this.processIdentification(identification);
        Message responseMessage = this.generateIdentificationResponseMessage(response);

        this.sendMessage(responseMessage);
        if (response == null || response instanceof FailureResponse) {
            Logger.error("Could not identify client type, sending failure response: " + responseMessage);
            this.identifyClientType(attempts + 1);
        } else if (response instanceof SuccessResponse) {
            this.logic.addSelfToServer();
        }
    }

    /**
     * Generates a response message for the client identification.
     *
     * @param response The response to the client identification
     * @return The response message
     */
    private Message generateIdentificationResponseMessage(Response response) {
        MessageHeader newHeader = new MessageHeader(Endpoints.SERVER, Endpoints.NONE.getValue());
        MessageBody newBody = new MessageBody(response);
        return new Message(newHeader, newBody);
    }

    /**
     * Processes the identification message from the client.
     *
     * @param identification The identification message
     * @return true if identification was successful, false otherwise
     */
    private Response processIdentification(String identification) {

        if (identification == null) {
            Logger.error("Invalid identification message: null");
            return new FailureResponse(new ClientIdentificationTransmission(), FailureReason.FAILED_TO_IDENTIFY_CLIENT);
        }

        Message message = Message.fromString(identification);
        Transmission command = message.getBody().getTransmission();

        Response response;
        if (command instanceof ClientIdentificationTransmission) {
            ClientIdentificationTransmission clientIdentificationCommand = (ClientIdentificationTransmission) command;
            response = this.handleClientIdentificationResponse(clientIdentificationCommand);
        } else {
            Logger.error("Invalid identification message: " + message);
            response = new FailureResponse(new ClientIdentificationTransmission(),
                    FailureReason.FAILED_TO_IDENTIFY_CLIENT);
        }

        return response;
    }

    /**
     * Handles the client identification response.
     *
     * @param command The client identification command
     * @return The response to the client identification
     */
    private Response handleClientIdentificationResponse(ClientIdentificationTransmission command) {
        Endpoints clientType = command.getClient();
        String clientId = command.getId();

        if (clientId.equals(Endpoints.NOT_PREDEFINED.getValue())) {
            clientId = this.getSocket().getRemoteSocketAddress().toString();
        }

        this.logic.setClientIdentification(new ClientIdentification(clientType, clientId));
        return new SuccessResponse(command, "Identification successful");
    }
}