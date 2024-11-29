package no.ntnu;

import java.io.IOException;

import no.ntnu.constants.Endpoints;
import no.ntnu.intermediaryserver.clienthandler.ClientIdentification;
import no.ntnu.messages.Transmission;
import no.ntnu.messages.commands.common.ClientIdentificationTransmission;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.tools.Logger;

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

  // TODO this class should have a method which decrypts the received message, and tursn it from string into message, and then calls handleMessage. Perhaps handleMessage should be renamed and such.
  // TODO: Decrypt message before handling using decryptStringMessage?
  protected abstract void handleMessage(Message message);

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
