package no.ntnu;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;

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

public abstract class SocketCommunicationChannel extends TcpConnection {
  protected ClientIdentification clientIdentification;

  private volatile boolean isReconnecting; // Flag to prevent simultaneous reconnects

  private static final int MAX_RETRIES = 5;
  private static final int RETRY_DELAY_MS = 1000; // Time between retries

  protected SocketCommunicationChannel(String host, int port) {
    super();
    try {
      this.initializeStreams(host, port);
    } catch (IOException e) {
      Logger.error("Could not establish connection to the server: " + e.getMessage());
      this.reconnect(host, port);
    }
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

  // TODO do differenlty.
  @Override
  public synchronized void reconnect(String host, int port) {

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
   * Creates a client identification message based on the provided client information.
   * 
   * @param clientIdentification The client identification information.
   * @return The identification message.
   */
  private Message createIdentificationMessage(ClientIdentification clientIdentification) {
    Transmission identificationCommand = new ClientIdentificationTransmission(clientIdentification);
    MessageBody body = new MessageBody(identificationCommand);
    MessageHeader header = new MessageHeader(Endpoints.SERVER, "none");
    return new Message(header, body);
  }
}
