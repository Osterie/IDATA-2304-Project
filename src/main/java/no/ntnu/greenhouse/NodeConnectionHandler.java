package no.ntnu.greenhouse;

import no.ntnu.Clients;
import no.ntnu.SocketCommunicationChannel;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.commands.Command;
import no.ntnu.tools.Logger;

public class NodeConnectionHandler extends SocketCommunicationChannel implements Runnable {
    private final NodeLogic nodeLogic;

    public NodeConnectionHandler(SensorActuatorNode node, String host, int port) {
        super(host, port);
        this.nodeLogic = new NodeLogic(node);
        this.establishConnectionWithServer(Clients.GREENHOUSE, String.valueOf(node.getId()));
    }

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
        Command command = body.getCommand();
        
        Message response = command.execute(this.nodeLogic);

        Logger.info("Received request for node from server, sending response " + sender + ";" + senderID + ";" + nodeLogic.getId());
        socketWriter.println(response.toProtocolString());
    }
}
