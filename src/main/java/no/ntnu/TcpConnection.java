package no.ntnu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.*;
import java.util.LinkedList;
import java.util.Queue;

import no.ntnu.constants.Endpoints;
import no.ntnu.gui.common.PopUpWindows.ErrorWindow;
import no.ntnu.messages.*;
import no.ntnu.messages.commands.greenhouse.GetNodeCommand;
import no.ntnu.tools.Logger;
import no.ntnu.tools.encryption.KeyGenerator;
import no.ntnu.tools.encryption.MessageEncryptor;
import no.ntnu.tools.encryption.MessageHasher;

public abstract class TcpConnection {

  private Socket socket;
  private BufferedReader socketReader;
  private PrintWriter socketWriter;
  private boolean isConnected;
  private Queue<Message> messageQueue;

  private String host;
  private int port;

  private boolean autoReconnect = true;
  private boolean isReconnecting = false;
  private static final int MAX_RETRIES = 5;
  private static final int RETRY_DELAY_MS = 1000; // Time between retries

  // Generate key pair
  private KeyPair recipientKeyPair = KeyGenerator.generateRSAKeyPair();
  private PublicKey recipientPublicKey = recipientKeyPair.getPublic();
  private PrivateKey recipientPrivateKey = recipientKeyPair.getPrivate();

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
   * Sets whether the socket is connected.
   * 
   * @param isConnected true if the socket is connected, false otherwise.
   */
  protected void setConnected(boolean isConnected) {
    this.isConnected = isConnected;
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
   * Sets whether the connection should automatically reconnect if lost.
   * 
   * @param autoReconnect true to enable auto-reconnect, false to disable it.
   */
  public void setAutoReconnect(boolean autoReconnect) {
    this.autoReconnect = autoReconnect;
  }

  /**
   * Returns true if the connection should automatically reconnect if lost, false
   * otherwise.
   * 
   * @return true if the connection should automatically reconnect if lost, false
   *         otherwise.
   */
  private boolean isAutoReconnect() {
    return this.autoReconnect;
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

    if (!isConnected()) {
      Logger.error("Failed to reconnect after " + attempts + " attempts.");
    }

    this.setIsReconnecting(false);
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
   * @param host
   * @param port
   * @throws IOException
   */
  protected void initializeStreams(String host, int port) throws IOException {
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

  // TODO @SebasoOlsen when done, refactor
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

      // Decryption
      try {
        // TODO: Encryption in send message hinders control panel to run.
        Logger.info("BEFORE DECRYPTION:" + message.getBody().getTransmission().toString());
        // Decrypts protocol
        // message = MessageEncryptor.decryptStringMessage(message,
        // recipientPrivateKey);
        Logger.info("AFTER DECRYPTION:" + message.getBody().getTransmission().toString());
      } catch (Exception e) {
        System.err.println("Could not decrypt message: " + e.getMessage());
      }

      // TODO: Delete when done using sout.
      Logger.info("HASHING TEST, HASH AFTER BEING SENT OVER:" + message.getHeader().getHashedContent());
      MessageHasher.addHashedContentToMessage(message);
      Logger.info("HASHING TEST, CHECKING IF EQUAL:" + message.getHeader().getHashedContent());

      // Extract hash from header
      String hashedContentFromHeader = message.getHeader().getHashedContent();
      // Hash received message
      MessageHasher.addHashedContentToMessage(message);
      String receivedMessageHash = message.getHeader().getHashedContent();

      // Match the two hashes
      if (hashedContentFromHeader.equals(receivedMessageHash)) {
        // No integiry loss.
      }
      else{
        // Integrity loss
        // Send message again. 
        this.handleIntegrityError(message);
        return;
      }

      this.handleMessage(message);
    } else {
      Logger.warn("Server message is null, closing connection");
      // TODO do differently?
      this.close();
    }
    // TODO handle if null and such
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
      Logger.error("Invalid server message format: " + messageToParse + ". Error: " + e.getMessage());
    }

    // Check for null message, header, or body
    if (message == null || message.getHeader() == null || message.getBody() == null) {
      Logger.error("Message, header, or body is missing in server message: " + message);
    }

    return message;
  }

  // TODO: Delete when done using.
  private void testIfEncryptionWorks() {
    Message message = new Message(new MessageHeader(Endpoints.GREENHOUSE, "7", ""),
        new MessageBody(new GetNodeCommand()));

    ErrorWindow.showError("tit", "It works");
    // TODO: This test shows it works.
    try {
      Logger.info("ENCRYPTION TEST: " + message.getBody().getTransmission().toString());
      message = MessageEncryptor.encryptMessage(message, recipientPublicKey);
      Logger.info("ENCRYPTION TEST: " + message.getBody().getTransmission().toString());
      message = MessageEncryptor.decryptStringMessage(message, recipientPrivateKey);
      Logger.info("ENCRYPTION TEST: " + message.getBody().getTransmission().toString());
    } catch (Exception e) {
      System.err.println("Could not decrypt message: " + e.getMessage());
    }
  }

  /**
   * Sends a message to the connected socket.
   * 
   * @param message the message to send.
   */
  public synchronized void sendMessage(Message message) {

    // Adds hashed version of body content to header,
    Message originalMessage = MessageHasher.addHashedContentToMessage(message);

    // TODO: This hinders the control panel to run.
    // Encrypt original body content
    // Message encryptedMessage = MessageEncryptor.encryptMessage(originalMessage,
    // recipientPublicKey);
    Message encryptedMessage = message;
    // TODO: Delete when done using
    testIfEncryptionWorks();

    // TODO: Delete when done using sout.
    Logger.info("HASHING TEST, HASH BEFORE SENDING:" + encryptedMessage.getHeader().getHashedContent());

    if (isConnected && socketWriter != null) {
      socketWriter.println(encryptedMessage);
      Logger.info("Sent message to server: " + encryptedMessage);
    } else {
      Logger.error("Unable to send message, socket is not connected.");
      messageQueue.offer(encryptedMessage); // Buffer the message
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
      if (socket != null)
        socket.close();
      if (socketReader != null)
        socketReader.close();
      if (socketWriter != null)
        socketWriter.close();
      isConnected = false;
      Logger.info("Socket connection closed.");
    } catch (IOException e) {
      Logger.error("Failed to close socket connection: " + e.getMessage());
    }
  }

  /**
   * Handles the received message.
   * 
   * @param message the message to handle.
   */
  protected abstract void handleMessage(Message message);
}
