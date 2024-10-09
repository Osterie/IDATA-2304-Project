package no.ntnu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import no.ntnu.controlpanel.CommunicationChannel;
import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.tools.Logger;

public class SocketCommunicationChannel implements CommunicationChannel {
    private final ControlPanelLogic logic;
    private Socket socket;
    private BufferedReader socketReader;
    private PrintWriter socketWriter;
    private boolean isConnected;

    public SocketCommunicationChannel(ControlPanelLogic logic, String host, int port) throws IOException {
        this.logic = logic;
        connect(host, port);
    }

    private void connect(String host, int port) throws IOException {
        try {
            this.socket = new Socket(host, port);
            this.socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.socketWriter = new PrintWriter(socket.getOutputStream(), true);
            this.isConnected = true;
            Logger.info("Socket connection established with " + host + ":" + port);
        } catch (IOException e) {
            Logger.error("Failed to connect to the server: " + e.getMessage());
            throw e;
        }
    }

    public void sendCommandToServer(String command) {
        if (isConnected && socketWriter != null) {
            socketWriter.println(command);
            Logger.info("Sent command to server: " + command);
            try {
                String serverResponse = socketReader.readLine();
                Logger.info("Received response from server: " + serverResponse);
            } catch (IOException e) {
                Logger.error("Error reading server response: " + e.getMessage());
            }
        } else {
            Logger.error("Unable to send command, socket is not connected.");
        }
    }

    @Override
    public void sendActuatorChange(int nodeId, int actuatorId, boolean isOn) {
        String command = "ACTUATOR_CHANGE:" + nodeId + "," + actuatorId + "," + (isOn ? "ON" : "OFF");
        sendCommandToServer(command);
    }

    @Override
    public boolean open() {
        return isConnected;
    }

    @Override
    public boolean close() {

        boolean closed = false;
        
        try {
            if (socket != null) socket.close();
            if (socketReader != null) socketReader.close();
            if (socketWriter != null) socketWriter.close();
            isConnected = false;
            Logger.info("Socket connection closed.");
            closed = true;
        } catch (IOException e) {
            Logger.error("Failed to close socket connection: " + e.getMessage());
        }
        return closed;
    }
}
