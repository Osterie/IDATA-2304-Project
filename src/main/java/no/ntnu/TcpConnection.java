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
import no.ntnu.tools.encryption.HashEncryptor;

public abstract class TcpConnection {
  // TODO make stuff private
  protected Socket socket;
  protected BufferedReader socketReader;
  protected PrintWriter socketWriter;
  protected boolean isOn; // TODO change name?
  protected Queue<Message> messageQueue;

  private boolean isReconnecting = false;

  protected String host;
  protected int port;

  private boolean autoReconnect = true;

  private static final int MAX_RETRIES = 5;
  private static final int RETRY_DELAY_MS = 1000; // Time between retries

  public TcpConnection() {
    this.messageQueue = new LinkedList<>();
  }

  /**
   * Returns true if the socket is connected, false otherwise.
   * 
   * @return true if the socket is connected, false otherwise.
   */
  public boolean isConnected() {
    return isOn;
  }

  /**
   * Sets whether the connection should automatically reconnect if lost.
   * 
   * @param autoReconnect true to enable auto-reconnect, false to disable it.
   */
  public void setAutoReconnect(boolean autoReconnect) {
    this.autoReconnect = autoReconnect;
  }

  /**
   * Connects to the given socket.
   * 
   * @param socket the socket to connect to.
   */
  public void connect(Socket socket) {
    try {
      this.socket = socket;
      this.socket.setKeepAlive(true);
      this.socketReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
      this.socketWriter = new PrintWriter(this.socket.getOutputStream(), true);
      this.isOn = true;
      this.host = socket.getInetAddress().getHostAddress();
      this.port = socket.getPort();
      Logger.info("Socket connection established with " + this.host + ":" + this.port);
      // listenForMessages();
    } catch (IOException e) {
      Logger.error("Could not establish connection to the server: " + e.getMessage());
      reconnect(this.host, this.port);
    }
  }

  /**
   * Connects to the given host and port.
   * 
   * @param host the host to connect to.
   * @param port the port to connect to.
   */
  public void connect(String host, int port) {
    try {
      Logger.info("Trying to establish connection to " + host + ":" + port);
      this.socket = new Socket(host, port);
      this.socket.setKeepAlive(true);
      this.socketReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
      this.socketWriter = new PrintWriter(this.socket.getOutputStream(), true);
      this.isOn = true;
      this.host = host;
      this.port = port;
      Logger.info("Socket connection established with " + host + ":" + port);
      // listenForMessages();
    } catch (IOException e) {
      Logger.error("Could not establish connection to the server: " + e.getMessage());
      reconnect(host, port);
    }
  }

  // TODO make private
  /**
   * Reconnects to the given host and port if connection lost.
   * 
   * @param host the host to connect to.
   * @param port the port to connect to.
   */
  public synchronized void reconnect(String host, int port) {

    if (!autoReconnect) {
      Logger.info("Auto-reconnect is disabled. Skipping reconnection attempt.");
      return;
    }

    if (this.isReconnecting) {
      Logger.info("Reconnection already in progress. Skipping this attempt.");
      return;
    }

    this.isReconnecting = true;

    int attempts = 0;
    while (!this.isOn && attempts < MAX_RETRIES) {
      try {
        Thread.sleep(RETRY_DELAY_MS * (int) Math.pow(2, attempts)); // Exponential backoff
        Logger.info("Reconnecting attempt " + (attempts + 1));
        this.close(); // Ensure previous resources are cleaned up
        this.initializeStreams(host, port);
        // this.establishConnectionWithServer(this.clientIdentification);
        this.isOn = true;
        this.flushBufferedMessages(); // Optional: flush buffered messages
        Logger.info("Reconnection successful.");
        // TODO don't have break?
        break;
      } catch (IOException | InterruptedException e) {
        attempts++;
        Logger.error("Reconnection attempt " + attempts + " failed: " + e.getMessage());
      }
    }

    if (!isOn) {
      Logger.error("Failed to reconnect after " + attempts + " attempts.");
    }

    isReconnecting = false;
  }

  /**
   * Initializes the input and output streams for the socket connection.
   * And starts the listening thread
   * 
   * @param host
   * @param port
   * @throws IOException
   */
  protected void initializeStreams(String host, int port) throws IOException {
    Logger.info("Trying to establish connection to " + host + ":" + port);
    this.close(); // Ensure any existing connection is closed
    this.socket = new Socket(host, port);
    this.socket.setKeepAlive(true);
    this.socketReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    this.socketWriter = new PrintWriter(this.socket.getOutputStream(), true);
    this.isOn = true;
    this.host = host;
    this.port = port;
    Logger.info("Socket connection established with " + host + ":" + port);
    this.startListenerThread();
  }

  /**
   * Starts the listener thread for the socket connection.
   */
  protected void startListenerThread() {
    Thread messageListener = new Thread(() -> {
      listenForMessages();
    });
    messageListener.setDaemon(true); // Ensure the thread doesn't block app shutdown
    messageListener.start();
  }

  /**
   * Continually listens for messages from the connected socket.
   */
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
      this.reconnect(this.host, this.port);
    }
  }

  /**
   * Reads a line from the connected socket.
   * 
   * @return the line read from the socket.
   */
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

  /**
   * Reads and handles a message from the connected socket.
   * 
   * @throws IOException if an I/O error occurs when reading the message.
   */
  protected void readMessage() throws IOException {
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

  /**
   * Parses the server message into a Message object.
   * 
   * @param messageToParse the message to parse.
   * @return the parsed message as a Message object.
   */
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

  // TODO: Should this class encrypt a message if handleMessage decrypts? If so,
  // should it have its own method before sending?
  /**
   * Sends a message to the connected socket.
   * 
   * @param message the message to send.
   */
  public synchronized void sendMessage(Message message) {

    // Adds hashed body content to header and encrypts the message.
    // Message encryptedMessage =
    // encryptMessage(addHashedContentToMessage(message));

    if (isOn && socketWriter != null) {
      socketWriter.println(message);
      Logger.info("Sent message to server: " + message);
    } else {
      Logger.error("Unable to send message, socket is not connected.");
      messageQueue.offer(message); // Buffer the message
      reconnect(this.host, this.port);
    }
  }

  // TODO do differenlty? use a send method perhaps
  // TODO should it be synchronized?
  /**
   * Flushes the buffered messages to the connected socket.
   */
  protected synchronized void flushBufferedMessages() {
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
        break; // TODO is break needed?
      }
    }
  }

  // TODO make private?
  /**
   * Closes the socket connection.
   */
  public synchronized void close() {
    // TODO refactor, if the close fails for any part, the next part wont be closed.

    try {
      if (socket != null)
        socket.close();
      if (socketReader != null)
        socketReader.close();
      if (socketWriter != null)
        socketWriter.close();
      isOn = false;
      Logger.info("Socket connection closed.");
    } catch (IOException e) {
      Logger.error("Failed to close socket connection: " + e.getMessage());
    }
  }

  // TODO make private?
  /**
   * Returns the socket.
   * 
   * @return the socket.
   */
  public Socket getSocket() {
    return socket;
  }

  // TODO use this method, should be private.
  /**
   * Returns true if the socket is reconnecting, false otherwise.
   * 
   * @return true if the socket is reconnecting, false otherwise.
   */
  public boolean isReconnecting() {
    return isReconnecting;
  }

  /**
   * Handles the received message.
   * 
   * @param message the message to handle.
   */
  protected abstract void handleMessage(Message message);
}
