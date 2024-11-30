package no.ntnu.controlpanel;

import static no.ntnu.tools.parsing.Parser.parseBooleanOrError;
import static no.ntnu.tools.parsing.Parser.parseIntegerOrError;

import java.util.List;

import no.ntnu.constants.Endpoints;
import no.ntnu.greenhouse.sensor.SensorReading;
import no.ntnu.intermediaryserver.clienthandler.ClientIdentification;
import no.ntnu.messages.Delimiters;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.Transmission;
import no.ntnu.messages.commands.greenhouse.ActuatorChangeCommand;
import no.ntnu.messages.commands.greenhouse.GetNodeCommand;
import no.ntnu.messages.commands.greenhouse.GetNodeIdCommand;
import no.ntnu.messages.commands.greenhouse.GetSensorDataCommand;
import no.ntnu.messages.commands.greenhouse.GreenhouseCommand;
import no.ntnu.messages.responses.FailureReason;
import no.ntnu.messages.responses.FailureResponse;
import no.ntnu.messages.responses.Response;
import no.ntnu.messages.responses.SuccessResponse;
import no.ntnu.tools.Logger;
import no.ntnu.tools.parsing.SensorActuatorNodeInfoParser;
import no.ntnu.tools.parsing.SensorReadingsParser;

public class ControlPanelResponseHandler {

  private ControlPanelCommunicationChannel communicationChannel;
  private ControlPanelLogic logic;

  public ControlPanelResponseHandler(ControlPanelCommunicationChannel communicationChannel, ControlPanelLogic logic) {
    this.communicationChannel = communicationChannel;
    this.logic = logic;
  }

  /**
   * Handle a response from a client.
   * Processes the response and performs actions based on the client type.
   *
   * @param client   The client that sent the response
   * @param body The response message body
   */
  public void handleResponse(Endpoints client, MessageBody body) {

    Response response = this.extractResponse(body);
    if (response == null) {
      return;
    }

    // Handle based on client type
    if (client == Endpoints.GREENHOUSE) {
      this.handleGreenhouseResponse(response);
    } else if (client == Endpoints.SERVER) {
      this.handleServerResponse(response);
    } else {
      Logger.error("Unknown client: " + client);
    }
  }

  /**
   * Handle a response to a greenhouse command.
   * Processes the command response and performs actions based on the command
   * type.
   *
   * @param body The message body containing the command response
   */
  private void handleGreenhouseResponse(Response response) {

    if (!(response instanceof SuccessResponse)) {
      Logger.info("Received non-success response, no action taken: " + response);
      return;
    }

    GreenhouseCommand command = this.extractCommand(response);
    if (command == null) {
      return;
    }

    String responseData = response.getResponseData();
    if (responseData == null) {
      Logger.error("Response data is null: " + response);
      return;
    }

    if (command instanceof GetNodeIdCommand) {
      this.handleGetNodeIdResponse(responseData);
    } else if (command instanceof GetNodeCommand) {
      this.handleGetNodeCommand(responseData);
    } else if (command instanceof GetSensorDataCommand) {
      this.handleGetSensorDataResponse(responseData);
    } else if (command instanceof ActuatorChangeCommand) {
      this.handleActuatorChangeResponse(responseData);
    } else {
      Logger.error("Unknown command: " + command);
    }
  }

  /**
   * Handles the response to an actuator change command.
   * 
   * @param responseData the response data.
   */
  private void handleActuatorChangeResponse(String responseData) {

    String[] parts = responseData.split(Delimiters.BODY_FIELD.getValue());
    if (parts.length != 3) {
      Logger.error("Invalid actuator change response: " + responseData);
      return;
    }

    int nodeId = parseIntegerOrError(parts[0], "Invalid node ID:" + parts[0]);
    int actuatorId = parseIntegerOrError(parts[1], "Invalid actuator ID:" + parts[1]);
    boolean actuatorState = parseBooleanOrError(parts[2], "Invalid actuator state:" + parts[2]);

    this.logic.advertiseActuatorState(nodeId, actuatorId, actuatorState, 1);
  }

  /**
   * Handles the response to a get sensor data command.
   * 
   * @param responseData the response data.
   */
  private void handleGetSensorDataResponse(String responseData) {

    String[] parts = responseData.split(Delimiters.BODY_FIELD.getValue());
    if (parts.length != 2) {
      throw new IllegalArgumentException("Incorrect specification format: " + responseData);
    }

    int nodeId = parseIntegerOrError(parts[0], "Invalid node ID:" + parts[0]);
    List<SensorReading> sensors = SensorReadingsParser.parseSensors(parts[1]);

    this.logic.advertiseSensorData(sensors, nodeId, 1);
  }

  /**
   * Handles the response to a get node command.
   * 
   * @param responseData the response data.
   */
  private void handleGetNodeCommand(String responseData) {
    SensorActuatorNodeInfo nodeInfo = SensorActuatorNodeInfoParser.createSensorNodeInfoFrom(responseData, this.logic);
    this.logic.addNode(nodeInfo);
  }

  /**
   * Handles the response to a get node ID command.
   * Spawns a new sensor/actuator node based on the response data.
   * 
   * @param nodeId the node id response data.
   */
  private void handleGetNodeIdResponse(String nodeId) {
    this.communicationChannel.askForNodeInfo(nodeId);
  }

  /**
   * Handles a response from the server.
   * 
   * @param response the message response.
   */
  private void handleServerResponse(Response response) {
    if (response instanceof SuccessResponse) {
      SuccessResponse successResponse = (SuccessResponse) response;
      this.handleServerSuccessResponse(successResponse);
    } else if (response instanceof FailureResponse) {
      FailureResponse failureResponse = (FailureResponse) response;
      this.handleServerFailureResponse(failureResponse);
    }
  }

  /**
   * Handles a success response from the server.
   * 
   * @param response the success response.
   */
  private void handleServerSuccessResponse(SuccessResponse response) {
    Logger.info("Success response: " + response);
  }

  /**
   * Handles a failure response from the server.
   * Different failure reasons may be hanlded differently.
   * 
   * @param response the failure response.
   */
  private void handleServerFailureResponse(FailureResponse response) {

    Logger.error("Failed to execute command, sending again: " + response);

    FailureReason reason = response.getFailureReason();

    if (reason == FailureReason.FAILED_TO_IDENTIFY_CLIENT) {
        ClientIdentification clientIdentification = new ClientIdentification(Endpoints.CONTROL_PANEL,
            Endpoints.NOT_PREDEFINED.getValue());
        this.communicationChannel.establishConnectionWithServer(clientIdentification);
    } else {
        Logger.error("Unknown command: " + response);
    }
  }

  /**
   * Extracts the response from a message body.
   * If the body does not contain a response, logs an error and returns null.
   * 
   * @param body the message body.
   * @return the success response, or null if the body does not contain a response.
   */
  private Response extractResponse(MessageBody body) {

    Transmission transmission = body.getTransmission();

    if (!(transmission instanceof Response)) {
      Logger.error("Invalid command type: " + transmission.getClass().getName());
      return null;
    }
    return (Response) transmission;
  }

  /**
   * Extracts the greenhouse command from a success response.
   * If the command is not a greenhouse command, logs an error and returns null.
   * 
   * @param response the response.
   * @return the greenhouse command, or null if the command is not a greenhouse command.
   */
  private GreenhouseCommand extractCommand(Response response) {
    Transmission command = response.getTransmission();

    if (!(command instanceof GreenhouseCommand)) {
      Logger.error("Invalid command type: " + command.getClass().getName());
      return null;
    }

    return (GreenhouseCommand) command;
  }
}
