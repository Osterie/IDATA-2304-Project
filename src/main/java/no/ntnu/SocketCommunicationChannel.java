package no.ntnu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.LinkedList;
import java.util.Queue;

import no.ntnu.constants.Endpoints;
import no.ntnu.intermediaryserver.clienthandler.ClientIdentification;
import no.ntnu.messages.Transmission;
import no.ntnu.messages.commands.common.ClientIdentificationTransmission;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.tools.Logger;
import no.ntnu.tools.encryption.HashEncryptor;
import no.ntnu.tools.encryption.asymmetric.HybridRSAEncryptor;

import javax.crypto.SecretKey;

public abstract class SocketCommunicationChannel {
  protected ClientIdentification clientIdentification;
  protected Socket socket;
  protected BufferedReader socketReader;
  protected PrintWriter socketWriter;
  protected boolean isOn;
  private Queue<Message> messageQueue;

  private volatile boolean isReconnecting; // Flag to prevent simultaneous reconnects

  private static final int MAX_RETRIES = 5;
  private static final int RETRY_DELAY_MS = 1000; // Time between retries

  protected SocketCommunicationChannel(String host, int port) {
    this.messageQueue = new LinkedList<>();
    try {
      this.initializeStreams(host, port);
    } catch (IOException e) {
      Logger.error("Could not establish connection to the server: " + e.getMessage());
      this.reconnect(host, port);
    }
  }

  private synchronized void initializeStreams(String host, int port) throws IOException {
    Logger.info("Trying to establish connection to " + host + ":" + port);
    this.close(); // Ensure any existing connection is closed
    this.socket = new Socket(host, port);
    this.socket.setKeepAlive(true);
    this.socketReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    this.socketWriter = new PrintWriter(this.socket.getOutputStream(), true);
    this.isOn = true;
    Logger.info("Socket connection established with " + host + ":" + port);
    this.startListenerThread();
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

  protected void readMessage() throws IOException{
    String serverMessage = this.socketReader.readLine();
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

  // TODO: handleMessage is abstract, so where should decryption happen?
  protected String decryptStringMessage(String encryptedMessage, String encryptedAESKey, PrivateKey privateKey) throws Exception {
    // Decrypt the AES key using the private key
    SecretKey decryptedAESKey = HybridRSAEncryptor.decryptAESKeyWithRSA(encryptedAESKey, privateKey);
    System.out.println("Decrypted AES Key.");

    // Decrypt the message using the AES key
    String decryptedMessage = HybridRSAEncryptor.decryptWithAES(encryptedMessage, decryptedAESKey);
    System.out.println("Decrypted Message: " + decryptedMessage);

    return decryptedMessage;
  };

  // TODO this class should have a method which decrypts the received message, and tursn it from string into message, and then calls handleMessage. Perhaps handleMessage should be renamed and such.
  // TODO: Decrypt message before handling using decryptStringMessage?
  // TODO: Message to ADRIAN - You said it should be Message not String, are you gonna fix it?
  protected abstract void handleMessage(Message message);

  /**
   * Takes message in and returns the encrypted version back.
   *
   * @param message the message to be encrypted.
   * @return message that is encrypted.
   */
  protected Message encryptMessage(Message message) {
    // TODO: This class should encrypt a message before sending it in sendMessage method.
    // TODO: Encrypting this was meaning we also need to send the key. But where? You can see how the class works under.
    try {
      // Generate RSA key pair for recipient
      KeyPair recipientKeyPair = HybridRSAEncryptor.generateRSAKeyPair();
      System.out.println("Generated recipient's RSA key pair.");

      // Generate AES key
      SecretKey aesKey = HybridRSAEncryptor.generateAESKey();
      System.out.println("Generated AES key.");

      // Encrypt the message
      String originalMessage = "Hello, this is a confidential message!";
      String encryptedMessage = HybridRSAEncryptor.encryptWithAES(originalMessage, aesKey);
      System.out.println("Encrypted Message: " + encryptedMessage);

      // Encrypt the AES key with the recipient's public key
      String encryptedAESKey = HybridRSAEncryptor.encryptAESKeyWithRSA(aesKey, recipientKeyPair.getPublic());
      System.out.println("Encrypted AES Key: " + encryptedAESKey);

    } catch (Exception e) {
      System.err.println("Error occurred during encryption or decryption: " + e.getMessage());
      e.printStackTrace();
    }

    return message;
  }

  /**
   * Takes a message in and returns the same message, but with the hashed
   * transmission stored in the header.
   *
   * @param message message that will be added hash to.
   * @return hashedMessage the new message with hashed element.
   */
  protected Message addHashedContentToMessage(Message message) {
    Message hashedMessage = message;

    String transmissionString = message.getBody().getTransmission().toString();
    String hashedTransmissionString = HashEncryptor.encryptString(transmissionString);
    hashedMessage.getHeader().setHashedContent(hashedTransmissionString);

    return hashedMessage;
  }

  // TODO: Should this class encrypt a message if handleMessage decrypts? If so, should it have its own method before sending?
  protected synchronized void sendMessage(Message message) {

    // Adds hashed body content to header and encrypts the message.
    Message encryptedMessage = encryptMessage(addHashedContentToMessage(message));

    if (isOn && socketWriter != null) {
      socketWriter.println(message);
      Logger.info("Sent message to server: " + message);
    } else {
      Logger.error("Unable to send message, socket is not connected.");
      messageQueue.offer(message); // Buffer the message
      reconnect(socket.getInetAddress().getHostAddress(), socket.getPort());
    }
  }

  protected void establishConnectionWithServer(ClientIdentification clientIdentification) {
    if (clientIdentification == null) {
      Logger.error("Client type or ID is null, cannot establish connection.");
      return;
    }

    this.clientIdentification = clientIdentification;

    // TODO server should send a response back with something to indicate the
    // connection was successful.
    // Send initial identifier to server
    Message identificationMessage = this.createIdentificationMessage(clientIdentification);
    this.sendMessage(identificationMessage);
  }

  private synchronized void reconnect(String host, int port) {

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
        this.establishConnectionWithServer(this.clientIdentification);
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

  // TODO do differenlty? use a send method perhaps
  private synchronized  void flushBufferedMessages() {
    while (!messageQueue.isEmpty() && this.isOn) {
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
        break; //TODO is break needed?
      }
    }
  }

  private Message createIdentificationMessage(ClientIdentification clientIdentification) {
    Transmission identificationCommand = new ClientIdentificationTransmission(clientIdentification);
    MessageBody body = new MessageBody(identificationCommand);
    MessageHeader header = new MessageHeader(Endpoints.SERVER, "none");
    return new Message(header, body);
  }

  /**
   * Returns true if the socket is reconnecting, false otherwise.
   * 
   * @return true if the socket is reconnecting, false otherwise.
   */
  public boolean isReconnecting() {
    return isReconnecting;
  }

  public boolean isOpen() {
    return isOn;
  }

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
}
