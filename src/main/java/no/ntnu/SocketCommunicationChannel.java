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

  protected SocketCommunicationChannel(String host, int port) {
    super();
    try {
      this.initializeStreams(host, port);
    } catch (IOException e) {
      Logger.error("Could not establish connection to the server: " + e.getMessage());
      this.reconnect(host, port);
    }
  }

  public void establishConnectionWithServer(ClientIdentification clientIdentification) {
    if (clientIdentification == null) {
      Logger.error("Client type or ID is null, cannot establish connection.");
      return;
    }

    this.clientIdentification = clientIdentification;

    Message identificationMessage = this.createIdentificationMessage(clientIdentification);
    this.sendMessage(identificationMessage);
  }

  @Override
  protected void doReconnectedActions(){
    this.establishConnectionWithServer(this.clientIdentification);
    super.doReconnectedActions();
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
