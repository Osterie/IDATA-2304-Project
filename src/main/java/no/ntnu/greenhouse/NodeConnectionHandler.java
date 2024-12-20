package no.ntnu.greenhouse;

import no.ntnu.SocketCommunicationChannel;
import no.ntnu.constants.Endpoints;
import no.ntnu.greenhouse.actuator.Actuator;
import no.ntnu.intermediaryserver.clienthandler.ClientIdentification;
import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.Transmission;
import no.ntnu.messages.commands.greenhouse.ActuatorChangeCommand;
import no.ntnu.messages.commands.greenhouse.GreenhouseCommand;
import no.ntnu.messages.responses.FailureResponse;
import no.ntnu.messages.responses.SuccessResponse;
import no.ntnu.tools.Logger;

/**
 * Handles the connection to the server for a node.
 */
public class NodeConnectionHandler extends SocketCommunicationChannel
    implements Runnable, ActuatorListener {
  private final NodeLogic nodeLogic;

  /**
   * Create a new connection handler for a node.
   *
   * @param node The node to handle the connection for.
   * @param host The host to connect to.
   * @param port The port to connect to.
   */
  public NodeConnectionHandler(SensorActuatorNode node, String host, int port) {
    super(host, port);
    this.nodeLogic = new NodeLogic(node);
    this.nodeLogic.addActuatorListener(this);
  }

  /**
   * Establish a connection with the server.
   */
  @Override
  public void run() {
    ClientIdentification clientIdentification =
        new ClientIdentification(Endpoints.GREENHOUSE, String.valueOf(this.nodeLogic.getId()));
    this.establishConnectionWithServer(clientIdentification);
  }

  /**
   * Send an actuator change command to the server.
   * Constructs and sends a message to change the state of an actuator.
   *
   * @param actuatorId The ID of the actuator
   * @param isOn       The desired state of the actuator
   */
  public void sendActuatorChange(int actuatorId, boolean isOn) {
    MessageHeader header =
        new MessageHeader(Endpoints.CONTROL_PANEL, Endpoints.BROADCAST.getValue());

    ActuatorChangeCommand command = new ActuatorChangeCommand(actuatorId, isOn);
    String responseData = command.createResponseData(this.nodeLogic);
    SuccessResponse response = new SuccessResponse(command, responseData);

    MessageBody body = new MessageBody(response);
    Message message = new Message(header, body);
    this.sendMessage(message);
  }

  /**
   * Handle a message received from the server.
   *
   * @param message The message received.
   */
  @Override
  protected void handleSpecificMessage(Message message) {
    Logger.info("Received message for node! " + this.nodeLogic.getId() + ": " + message);

    MessageHeader header = message.getHeader();
    MessageBody body = message.getBody();
    Transmission command = body.getTransmission();

    this.handleTransmission(command, header);
  }

  /**
   * Handles a transmission.
   *
   * @param transmission the received transmission.
   * @param header       the message header.
   */
  private void handleTransmission(Transmission transmission, MessageHeader header) {
    if (transmission instanceof GreenhouseCommand) {
      GreenhouseCommand greenhouseCommand = (GreenhouseCommand) transmission;
      this.handleGreenhouseCommand(greenhouseCommand, header);
    } else if (transmission instanceof SuccessResponse) {
      SuccessResponse successResponse = (SuccessResponse) transmission;
      this.handleSuccessResponse(successResponse);
    } else if (transmission instanceof FailureResponse) {
      FailureResponse failureResponse = (FailureResponse) transmission;
      this.handleFailureResponse(failureResponse);
    } else {
      Logger.error("Received invalid command for node: " + transmission);
    }
  }

  /**
   * Handles a greenhouse command.
   *
   * @param command the received greenhouse command.
   * @param header  the message header.
   */
  private void handleGreenhouseCommand(GreenhouseCommand command, MessageHeader header) {
    Message response = command.execute(this.nodeLogic, header);
    // Logger.info("Received command for node, sending response: " + response);
    this.sendMessage(response);
  }

  /**
   * Handles a success response.
   *
   * @param response the received success response.
   */
  private void handleSuccessResponse(SuccessResponse response) {
    Logger.info("Received success response for node: " + response);
  }

  /**
   * Handles a failure response.
   *
   * @param response the received failure response.
   */
  private void handleFailureResponse(FailureResponse response) {
    Logger.error("Received failure response for node: " + response);
  }

  /**
   * Notify the server that an actuator has changed state.
   *
   * @param nodeId   The ID of the node.
   * @param actuator The actuator that has changed state.
   */
  @Override
  public void actuatorUpdated(int nodeId, Actuator actuator) {
    sendActuatorChange(actuator.getId(), actuator.isOn());
  }
}
