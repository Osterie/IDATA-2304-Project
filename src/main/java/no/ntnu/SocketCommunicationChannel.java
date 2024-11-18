package no.ntnu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import no.ntnu.messages.Message;
import no.ntnu.tools.Logger;


public abstract class SocketCommunicationChannel {
    protected Socket socket;
    protected BufferedReader socketReader;
    protected PrintWriter socketWriter;
    protected boolean isOn;

    protected SocketCommunicationChannel(String host, int port) {
        try{
            this.initializeStreams(host, port);
        }
        catch (IOException e) {
            Logger.error("Could not establish connection to the server: " + e.getMessage());
        }
    }

    private void initializeStreams(String host, int port) throws IOException {
        try {
            this.socket = new Socket(host, port);
            this.socket.setKeepAlive(true);
            this.socketReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.socketWriter = new PrintWriter(this.socket.getOutputStream(), true);
            this.isOn = true;
            Logger.info("Socket connection established with " + host + ":" + port);

            } catch (IOException e) {
            Logger.error("Could not establish connection to the server: " + e.getMessage());
        }
    }

    protected void listenForMessages(){
        Thread messageListener = new Thread(() -> {
            try {
            while (isOn) {
                if (socketReader.ready()) {
                  String serverMessage = socketReader.readLine();
                  if (serverMessage != null) {
                      Logger.info("Received from server: " + serverMessage);
                      this.handleMessage(serverMessage);
                  }
                  // TODO handle if null and such
                }
            }
            Logger.info("Server message listener stopped.");
            } catch (IOException e) {
              Logger.error("Connection lost: " + e.getMessage());
            } 
            finally {
              this.close();
            }
        });
        messageListener.start();
    }

    protected abstract void handleMessage(String message);

    protected void sendCommandToServer(Message message) {
      if (isOn && socketWriter != null) {
        Logger.info("Trying to send message...");
        socketWriter.println(message.toProtocolString());
        Logger.info("Sent message to server: " + message.toProtocolString());
      } else {
        Logger.error("Unable to send message, socket is not connected.");
      }
    }
    
    public boolean isOpen() {
      return isOn;
    }
  
    public boolean close() {
  
      boolean closed = false;
  
      try {
        if (socket != null)
          socket.close();
        if (socketReader != null)
          socketReader.close();
        if (socketWriter != null)
          socketWriter.close();
        isOn = false;
        Logger.info("Socket connection closed.");
        closed = true;
      } catch (IOException e) {
        Logger.error("Failed to close socket connection: " + e.getMessage());
      }
      return closed;
    }

  // TODO this should be done in another way, use a protocol with header and body instead and such?
  protected void establishConnectionWithServer(Clients client, String id) {

    // Send initial identifier to server
    // TODO server should send a response back with something to indicate the connection was successful.
    String identifierMessage = client.getValue() + ";" + id;
    this.socketWriter.println(identifierMessage);
  }
}
