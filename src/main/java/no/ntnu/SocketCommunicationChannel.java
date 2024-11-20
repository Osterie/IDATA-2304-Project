package no.ntnu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.commands.ClientIdentificationCommand;
import no.ntnu.tools.Logger;


public abstract class SocketCommunicationChannel {
    protected Socket socket;
    protected BufferedReader socketReader;
    protected PrintWriter socketWriter;
    protected boolean isOn;

    protected SocketCommunicationChannel(String host, int port) {
      this.initializeStreams(host, port);
    }

    private void initializeStreams(String host, int port) {
      try {
          Logger.info("Trying to establish connection to " + host + ":" + port);
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

  protected void establishConnectionWithServer(Endpoints client, String id)  {
    if (client == null || id == null) {
      Logger.error("Client type or ID is null, cannot establish connection.");
      return;
    }

    // TODO server should send a response back with something to indicate the connection was successful.
    // Send initial identifier to server
    Message identificationMessage = this.createIdentificationMessage(client, id);
    this.sendCommandToServer(identificationMessage);
  }

  private Message createIdentificationMessage(Endpoints client, String id) {
    ClientIdentificationCommand identificationCommand = new ClientIdentificationCommand(client, id);
    MessageBody body = new MessageBody(identificationCommand);
    MessageHeader header = new MessageHeader(Endpoints.SERVER, "none");
    return new Message(header, body);
  }
}
