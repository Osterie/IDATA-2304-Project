package no.ntnu.intermediaryserver.clienthandler;

import java.util.List;

import no.ntnu.constants.Endpoints;
import no.ntnu.intermediaryserver.server.IntermediaryServer;
import no.ntnu.messages.MessageHeader;
import no.ntnu.tools.Logger;

public class ClientHandlerLogic {

    private final ClientHandler clientHandler;
    private final IntermediaryServer server;
    private ClientIdentification clientIdentification;

    /**
     * Creates a new instance of the client handler logic.
     * 
     * @param clientHandler The client handler
     * @param server        The server
     */
    public ClientHandlerLogic(ClientHandler clientHandler, IntermediaryServer server) {
        this.clientHandler = clientHandler;
        this.server = server;
    }

    /**
     * Generates a message header for the sender of a message.
     * When we send a message received from a client, we have to put in the header
     * what client sent it.
     * This is so that the sent message can be sent back to the original sender.
     * 
     * @return The message header for the sender
     */
    public MessageHeader generateSenderHeader() {
        if (this.getClientType() == null || this.getClientId() == null) {
            Logger.error("Client type or id is null");
            return null;
        }
        return new MessageHeader(this.getClientType(), this.getClientId());
    }

    /**
     * Gets the client handler for a given client type and ID from the server.
     * 
     * @param clientType The client type
     * @param clientId   The client ID
     * @return The client handler for the client type and ID
     */
    public ClientHandler getClientHandler(Endpoints clientType, String clientId) {
        return server.getClientHandler(clientType, clientId);
    }

    /**
     * Gets all client handlers for a given client type.
     * 
     * @param header The message header containing the client type
     * @return A list of client handlers for the client type specified in the header
     */
    public List<ClientHandler> getAllClientHandlers(Endpoints clientType) {
        return server.getClientHandlers(clientType);
    }

    /**
     * Sets client identification.
     * 
     * @param clientIdentification The client identification to set.
     */
    public void setClientIdentification(ClientIdentification clientIdentification) {
        this.clientIdentification = clientIdentification;
    }

    /**
     * Gets the client type of the client being handled.
     * 
     * @return The client type of the client being handled
     */
    private Endpoints getClientType() {
        return this.clientIdentification.getClientType();
    }

    /**
     * Gets the client ID of the client being handled.
     * 
     * @return The client ID of the client being handled
     */
    private String getClientId() {
        return this.clientIdentification.getClientId();
    }

    /**
     * Adds the client to the server's list of clients.
     */
    public void addSelfToServer() {
        try {
            server.addClientHandler(this.getClientType(), this.getClientId(), this.clientHandler);
        } catch (UnknownClientException e) {
            Logger.error("Unknown client type: " + this.getClientType());
        }
    }
}
