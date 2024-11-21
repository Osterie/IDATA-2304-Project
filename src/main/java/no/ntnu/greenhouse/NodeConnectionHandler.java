package no.ntnu.greenhouse;

import no.ntnu.SocketCommunicationChannel;
import no.ntnu.constants.Endpoints;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.Transmission;
import no.ntnu.messages.commands.Command;
import no.ntnu.messages.greenhousecommands.GreenhouseCommand;
import no.ntnu.tools.Logger;

/**
 * Handles the connection to the server for a node.
 */
public class NodeConnectionHandler extends SocketCommunicationChannel implements Runnable {
    private final NodeLogic nodeLogic;

    /**
     * Create a new connection handler for a node.
     *
     * @param node The node to handle the connection for.
     * @param host The host to connect to.
     * @param port The port to connect to.
     */
    public NodeConnectionHandler(SensorActuatorNode node, String host, int port) {
        super(host, port);
        this.nodeLogic = new NodeLogic(node);
        this.establishConnectionWithServer(Endpoints.GREENHOUSE, String.valueOf(node.getId()));
    }

    /**
     * Start listening for messages from the server.
     */
    @Override
    public void run() {
        this.listenForMessages();
    }

    // TODO fix, improve, refactor.
    @Override
    protected void handleMessage(String message) {

        Logger.info("Received message for node! " + this.nodeLogic.getId() + ": " + message);

        Message messageObject = Message.fromProtocolString(message);
        MessageHeader header = messageObject.getHeader();
        MessageBody body = messageObject.getBody();
        
        String sender = header.getReceiver().toString();
        String senderID = header.getId();
        Transmission command = body.getTransmission();

        if (command instanceof GreenhouseCommand) {
            GreenhouseCommand greenhouseCommand = (GreenhouseCommand) command;
            Message response = greenhouseCommand.execute(this.nodeLogic);
            Logger.info("Received command for node, sending response " + sender + ";" + senderID + ";" + response.getBody().getTransmission().toString());
            socketWriter.println(response.toProtocolString());
        }
        else{
            Logger.error("Received invalid command for node: " + command.toString());
        }        
    }
}
