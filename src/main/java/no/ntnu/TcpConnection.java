package no.ntnu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import no.ntnu.messages.Message;
import no.ntnu.messages.Transmission;
import no.ntnu.messages.commands.common.ClientIdentificationTransmission;
import no.ntnu.messages.responses.SuccessResponse;
import no.ntnu.tools.Logger;


/**
 * The TcpConnection class provides a base class for establishing and managing
 * TCP socket connections. This class provides methods for connecting to a
 * socket, sending and receiving messages, and handling connection errors.
 * Subclasses can extend this class to implement specific message handling
 * logic.
 */
public abstract class TcpConnection {

  private static final int MAX_RETRIES = 5;
  private static final int RETRY_DELAY_MS = 1000; // Time between retries
  private Socket socket;
  private BufferedReader socketReader;
  private PrintWriter socketWriter;
  private boolean isConnected;
  private final Queue<Message> messageQueue;
  private String host;
  private int port;
  private boolean autoReconnect = true;
  private boolean isReconnecting = false;

  /**
   * Creates a new TCP connection.
   */
  protected TcpConnection() {
    this.messageQueue = new LinkedList<>();
  }

  /**
   * Returns the socket.
   *
   * @return the socket.
   */
  protected Socket getSocket() {
    return socket;
  }

  /**
   * Sets whether the socket is reconnecting.
   *
   * @param isReconnecting true if the socket is reconnecting, false otherwise.
   */
  private void setIsReconnecting(boolean isReconnecting) {
    this.isReconnecting = isReconnecting;
  }

  /**
   * Returns true if the socket is reconnecting, false otherwise.
   *
   * @return true if the socket is reconnecting, false otherwise.
   */
  protected boolean isReconnecting() {
    return this.isReconnecting;
  }

  /**
   * Returns true if the socket is connected, false otherwise.
   *
   * @return true if the socket is connected, false otherwise.
   */
  public boolean isConnected() {
    return this.isConnected;
  }

  /**
   * Sets whether the socket is connected.
   *
   * @param isConnected true if the socket is connected, false otherwise.
   */
  protected void setConnected(boolean isConnected) {
    this.isConnected = isConnected;
  }

  /**
   * Returns true if the connection should automatically reconnect if lost, false
   * otherwise.
   *
   * @return true if the connection should automatically reconnect if lost, false otherwise.
   */
  private boolean isAutoReconnect() {
    return this.autoReconnect;
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
      this.initializeSocket(socket);
    } catch (IOException e) {
      this.handleConnectionError(e, this.host, this.port);
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
      Socket socket = new Socket(host, port);
      this.initializeSocket(socket);
    } catch (IOException e) {
      this.handleConnectionError(e, host, port);
    }
  }

  /**
   * Initializes the socket and sets up the reader and writer streams.
   *
   * @param socket the socket to initialize.
   * @throws IOException if an I/O error occurs during initialization.
   */
  private void initializeSocket(Socket socket) throws IOException {
    this.socket = socket;
    this.socket.setKeepAlive(true);
    this.socketReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    this.socketWriter = new PrintWriter(this.socket.getOutputStream(), true);
    this.setConnected(true);
    this.host = socket.getInetAddress().getHostAddress();
    this.port = socket.getPort();
    Logger.info("Socket connection established with " + this.host + ":" + this.port);
  }

  /**
   * Handles connection errors by logging and attempting to reconnect.
   *
   * @param e    the exception encountered.
   * @param host the host to reconnect to.
   * @param port the port to reconnect to.
   */
  private void handleConnectionError(IOException e, String host, int port) {
    Logger.error("Could not establish connection to the server: " + e.getMessage());
    this.reconnect(host, port);
  }

  /**
   * Reconnects to the given host and port if connection lost.
   *
   * @param host the host to connect to.
   * @param port the port to connect to.
   */
  protected synchronized void reconnect(String host, int port) {

    if (!this.shouldReconnect()) {
      return;
    }

    this.setIsReconnecting(true);

    int attempts = 0;
    while (!this.isConnected() && attempts < MAX_RETRIES) {
      try {
        this.sleepForReconnection(attempts);
        this.doReconnectionActions(host, port);
        Logger.info("Reconnection successful.");
      } catch (IOException e) {
        attempts++;
        Logger.error("Reconnection attempt " + attempts + " failed: " + e.getMessage());
        this.setConnected(false);
      }
    }

    this.setIsReconnecting(false);

    if (!isConnected()) {
      Logger.error("Failed to reconnect after " + attempts + " attempts.");
      this.setIsReconnecting(false);
      this.close();
    }
  }

  /**
   * Returns true if the connection should attempt to reconnect, false otherwise.
   * If auto-reconnect is disabled or a reconnection is already in progress, it
   * should not attempt to reconnect.
   *
   * @return true if the connection should attempt to reconnect, false otherwise.
   */
  private boolean shouldReconnect() {

    boolean shouldReconnect = true;

    if (!this.isAutoReconnect()) {
      Logger.info("Auto-reconnect is disabled. Skipping reconnection attempt.");
      shouldReconnect = false;
    } else if (this.isReconnecting()) {
      Logger.info("Reconnection already in progress. Skipping this attempt.");
      shouldReconnect = false;
    }

    return shouldReconnect;
  }

  /**
   * Performs the actions needed to reconnet.
   *
   * @param host the host to reconnect to.
   * @param port the port to reconnect to.
   * @throws IOException if an I/O error occurs during reconnection.
   */
  private void doReconnectionActions(String host, int port) throws IOException {
    this.initializeStreams(host, port);
  }

  /**
   * Performs the actions needed after a successful reconnection.
   * subclasses can override this method to perform additional actions if needed.
   */
  protected void doReconnectedActions() {
    this.flushBufferedMessages();
  }

  /**
   * Sleeps for an increasing amount of time before attempting to reconnect.
   *
   * @param attempts the number of reconnection attempts made so far.
   */
  private void sleepForReconnection(int attempts) {
    try {
      Logger.info("Reconnecting attempt " + (attempts + 1));
      int delay = RETRY_DELAY_MS * (int) Math.pow(2, attempts);
      Thread.sleep(delay); // Exponential backoff
    } catch (InterruptedException e) {
      Logger.error("Failed to sleep for reconnection: " + e.getMessage());
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Initializes the input and output streams for the socket connection.
   * And starts the listening thread
   *
   * @param host the host to connect to.
   * @param port the port to connect to.
   * @throws IOException if an I/O error occurs during initialization.
   */
  public void initializeStreams(String host, int port) throws IOException {
    Logger.info("Trying to establish connection to " + host + ":" + port);
    this.socket = new Socket(host, port);
    this.socket.setKeepAlive(true);
    this.socketReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    this.socketWriter = new PrintWriter(this.socket.getOutputStream(), true);
    this.setConnected(true);
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
      while (this.isConnected()) {
        this.readMessage();
      }
      Logger.info("Server message listener stopped.");
    } catch (IOException e) {
      Logger.error("Connection lost: " + e.getMessage());
      this.setConnected(false);
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
      Message message = this.parseMessage(serverMessage);

      // Extract hash from created header
      String hashedContentFromHeader = message.getHeader().getHashedContent();

      // Extract hash from received header
      String receivedMessageHash = message.getHeader().getHashedContent();

      // Match the two hashes
      if (hashedContentFromHeader.equals(receivedMessageHash)) {
        // No integrity loss.
      } else {
        // Integrity loss
        // Send message again.
        this.handleIntegrityError(message);
        return;
      }

      this.handleMessage(message);
    } else {
      Logger.warn("Server message is null, closing connection");
      this.close();
    }
  }

  /**
   * Handles integrity error by resending the message.
   *
   * @param message the message to resend.
   */
  private void handleIntegrityError(Message message) {
    Logger.error("Integrity error detected");
    // Resend the message
    this.sendMessage(message);

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
      Logger.error("Invalid server message format: " + messageToParse + ". Error: "
              + e.getMessage());
    }

    // Check for null message, header, or body
    if (message == null || message.getHeader() == null || message.getBody() == null) {
      Logger.error("Message, header, or body is missing in server message: " + message);
    }

    return message;
  }

  /**
   * Sends a message to the connected socket.
   *
   * @param message the message to send.
   */
  public synchronized void sendMessage(Message message) {

    if (isConnected && socketWriter != null) {
      socketWriter.println(message);

    } else {
      Logger.error("Unable to send message, socket is not connected.");
      messageQueue.offer(message); // Buffer the message
      reconnect(this.host, this.port);
    }
  }

  /**
   * Flushes the buffered messages to the connected socket.
   */
  protected synchronized void flushBufferedMessages() {

    boolean tryingToSend = true;

    while (!messageQueue.isEmpty() && tryingToSend) {
      Message message = messageQueue.poll();
      try {
        // Check if the socket is still open
        if (socket != null && !socket.isClosed() && socket.isConnected() && socketWriter != null) {
          this.sendMessage(message);
          Logger.info("Resent buffered message: " + message);
        } else {
          throw new IOException("Socket is not open or not connected.");
        }
      } catch (IOException e) {
        Logger.error("Failed to resend buffered message: " + e.getMessage());
        messageQueue.offer(message); // Put it back in the queue for retry later
        tryingToSend = false; // Stop trying to send
      }
    }
  }

  /**
   * Closes the socket connection.
   */
  public synchronized void close() {

    try {
      if (socket != null) {
        socket.close();
      }
      if (socketReader != null) {
        socketReader.close();
      }
      if (socketWriter != null) {
        socketWriter.close();
      }
      isConnected = false;
      Logger.info("Socket connection closed.");
    } catch (IOException e) {
      Logger.error("Failed to close socket connection: " + e.getMessage());
    }
  }

  /**
   * Handles a message received from the client. This method is final to
   * ensure consistent handling logic across all subclasses.
   * Subclasses can override `handleSpecificMessage` to provide custom logic.
   */
  protected final void handleMessage(Message message) {
    // Common handling logic
    if (message.getBody().getTransmission() instanceof SuccessResponse response) {

      Transmission transmission = response.getTransmission();

      if (transmission
              instanceof ClientIdentificationTransmission clientIdentificationTransmission) {

        // Common logic for handling client identification
        handleClientIdentification(clientIdentificationTransmission);
      }
    }

    // Delegate to subclass for specific logic
    handleSpecificMessage(message);
  }

  /**
   * Processes the client identification transmission. This is shared logic
   * that applies to all subclasses.
   */
  private void handleClientIdentification(ClientIdentificationTransmission transmission) {
    // Perform common handling logic for client identification
  }

  /**
   * Allows subclasses to implement their specific handling logic.
   *
   * @param message The message to handle
   */
  protected abstract void handleSpecificMessage(Message message);
}
