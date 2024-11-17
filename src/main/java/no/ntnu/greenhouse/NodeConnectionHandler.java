package no.ntnu.greenhouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import no.ntnu.Clients;
import no.ntnu.SocketCommunicationChannel;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.commands.ActuatorChangeCommand;
import no.ntnu.messages.commands.Command;
import no.ntnu.tools.Logger;

public class NodeConnectionHandler implements Runnable {
    private final NodeLogic nodeLogic;
    private final Socket socket;
    private final PrintWriter socketWriter;
    private final BufferedReader socketReader;

    public NodeConnectionHandler(SensorActuatorNode node, String host, int port) throws IOException {
        this.nodeLogic = new NodeLogic(node);
        this.socket = new Socket(host, port);
        this.socket.setKeepAlive(true);
        this.socketWriter = new PrintWriter(socket.getOutputStream(), true);
        this.socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Send initial identifier to server
        String identifierMessage = "GREENHOUSE;" + node.getId();
        socketWriter.println(identifierMessage);
        Logger.info("connecting node " + node.getId() + " with identifier: " + identifierMessage);
    }

    @Override
    public void run() {
        try {
            while (!socket.isClosed()) {
                String serverMessage = socketReader.readLine();
                if (serverMessage != null) {
                    Logger.info("Received for node " + this.nodeLogic.getId() + ": " + serverMessage);
                    handleServerCommand(serverMessage);
                }
                else{
                    Logger.info("Invalid request from server: " + serverMessage);
                }
            }

        } catch (IOException e) {
            Logger.error("Connection lost for node " + this.nodeLogic.getId() + ": " + e.getMessage());
        } catch (Exception e) {
            Logger.error("Unexpected error for node " + this.nodeLogic.getId() + ": " + e.getMessage());
        } finally {
            this.close();
        }
    }

    // TODO fix, improve, refactor. Use command classes and message classes
    private void handleServerCommand(String message) {
        try {
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
        } catch (Exception e) {
            Logger.error("Error handling server command for node " + this.nodeLogic.getId() + ": " + e.getMessage());
        }
    }

    public void close() {
        try {
            socket.close();
            socketWriter.close();
            socketReader.close();
            Logger.info("Connection closed for node " + nodeLogic.getId());
        } catch (IOException e) {
            Logger.error("Error closing connection for node " + nodeLogic.getId());
        }
    }
}
