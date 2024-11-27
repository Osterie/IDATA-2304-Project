package no.ntnu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import no.ntnu.messages.Message;
import no.ntnu.tools.Logger;

public abstract class TcpConnection {
    // TODO make stuff private
    protected Socket socket;
    protected BufferedReader socketReader;
    private PrintWriter socketWriter;
    protected boolean isOn;
    private Queue<Message> messageQueue;

    private static final int MAX_RETRIES = 5;
    private static final int RETRY_DELAY_MS = 1000; // Time between retries

    public TcpConnection() {
        this.messageQueue = new LinkedList<>();
    }

    public boolean isConnected() {
        return isOn;
    }

    public void connect(Socket socket){
        try {
            this.socket = socket;
            this.socket.setKeepAlive(true);
            this.socketReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.socketWriter = new PrintWriter(this.socket.getOutputStream(), true);
            this.isOn = true;
            Logger.info("Socket connection established with " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
            // listenForMessages();
        } catch (IOException e) {
            Logger.error("Could not establish connection to the server: " + e.getMessage());
            reconnect(socket.getInetAddress().getHostAddress(), socket.getPort());
        }
    }

    public void connect(String host, int port) {
        try {
            Logger.info("Trying to establish connection to " + host + ":" + port);
            this.socket = new Socket(host, port);
            this.socket.setKeepAlive(true);
            this.socketReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.socketWriter = new PrintWriter(this.socket.getOutputStream(), true);
            this.isOn = true;
            Logger.info("Socket connection established with " + host + ":" + port);
            // listenForMessages();
        } catch (IOException e) {
            Logger.error("Could not establish connection to the server: " + e.getMessage());
            reconnect(host, port);
        }
    }

    // TODO make private
    public void reconnect(String host, int port) {
        int attempts = 0;
        while (!isOn && attempts < MAX_RETRIES) {
            try {
                Thread.sleep(RETRY_DELAY_MS * (int) Math.pow(2, attempts)); // Exponential backoff
                Logger.info("Reconnecting attempt " + (attempts + 1));
                socket = new Socket(host, port);
                socket.setKeepAlive(true);
                socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                socketWriter = new PrintWriter(socket.getOutputStream(), true);
                isOn = true;
                Logger.info("Reconnected successfully to " + host + ":" + port);
                flushBufferedMessages();
                break;
            } catch (IOException | InterruptedException e) {
                attempts++;
                Logger.error("Reconnection attempt " + attempts + " failed: " + e.getMessage());
            }
        }
    }

    protected void startListenerThread() {
        Thread messageListener = new Thread(() -> {
          listenForMessages();
        });
        messageListener.setDaemon(true); // Ensure the thread doesn't block app shutdown
    
        messageListener.start();
      }
    
      protected void listenForMessages() {
        try {
          while (this.isOn) {
            this.readMessage();
          }
          Logger.info("Server message listener stopped.");
        } catch (IOException e) {
          this.close();
          Logger.error("Connection lost: " + e.getMessage());
          this.isOn = false;
          this.reconnect(this.socket.getInetAddress().getHostAddress(), this.socket.getPort());
        }
      }

    protected String readLine() {
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
    
  protected void readMessage() throws IOException{
    String serverMessage = this.readLine();
    if (serverMessage != null) {
      Logger.info("Received from server: " + serverMessage);
      // TODO: It needs decryption.
        // TODO: HandleMessage should take Message not String.
        Message message = this.parseMessage(serverMessage);
        this.handleMessage(message);
    } else {
      Logger.warn("Server message is null, closing connection");
      // TODO do differently?
      this.close();
    }
    // TODO handle if null and such
  }

  private Message parseMessage(String messageToParse) {
    Message message = null;

    // Attempt to parse the server message
    try {
      message = Message.fromString(messageToParse);
    } catch (IllegalArgumentException | NullPointerException e) {
      Logger.error("Invalid server message format: " + messageToParse + ". Error: " + e.getMessage());
    }

    // Check for null message, header, or body
    if (message == null || message.getHeader() == null || message.getBody() == null) {
      Logger.error("Message, header, or body is missing in server message: " + message);
    }

    return message;
    }

    public void sendMessage(Message message) {
        if (isOn && socketWriter != null) {
            socketWriter.println(message);
            Logger.info("Sent message to server: " + message);
        } else {
            Logger.error("Unable to send message, socket is not connected.");
            messageQueue.offer(message); // Buffer the message
            reconnect(socket.getInetAddress().getHostAddress(), socket.getPort());
        }
    }

    private void flushBufferedMessages() {
        while (!messageQueue.isEmpty()) {
            Message message = messageQueue.poll();
            try {
                // Check if the socket is still open
                if (socket != null && !socket.isClosed() && socket.isConnected() && socketWriter != null) {
                    socketWriter.println(message);
                    socketWriter.flush(); // Ensure the message is sent immediately
                    Logger.info("Resent buffered message: " + message);
                } else {
                    throw new IOException("Socket is not open or not connected.");
                }
            } catch (IOException e) {
                Logger.error("Failed to resend buffered message: " + e.getMessage());
                messageQueue.offer(message); // Put it back in the queue for retry later
                break;
            }
        }
    }
    

    public void close() {
        try {
            if (socket != null) socket.close();
            if (socketReader != null) socketReader.close();
            if (socketWriter != null) socketWriter.close();
            isOn = false;
            Logger.info("Socket connection closed.");
        } catch (IOException e) {
            Logger.error("Failed to close socket connection: " + e.getMessage());
        }
    }

    // TODO make private?
    public Socket getSocket() {
        return socket;
    }

    protected abstract void handleMessage(Message message);
}
