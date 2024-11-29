package no.ntnu.controlpanel;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static no.ntnu.tools.Parser.parseIntegerOrError;
import static no.ntnu.tools.Parser.parseBooleanOrError;

import no.ntnu.SensorActuatorNodeInfoParser;
import no.ntnu.SensorReadingsParser;
import no.ntnu.constants.Endpoints;
import no.ntnu.greenhouse.sensors.NoSensorReading;
import no.ntnu.greenhouse.sensors.SensorReading;
import no.ntnu.intermediaryserver.clienthandler.ClientIdentification;
import no.ntnu.messages.Delimiters;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.Transmission;
import no.ntnu.messages.commands.greenhouse.GetNodeCommand;
import no.ntnu.messages.commands.greenhouse.GreenhouseCommand;
import no.ntnu.messages.responses.FailureReason;
import no.ntnu.messages.responses.FailureResponse;
import no.ntnu.messages.responses.Response;
import no.ntnu.messages.responses.SuccessResponse;
import no.ntnu.tools.Logger;

public class ControlPanelResponseHandler {

    private ControlPanelCommunicationChannel communicationChannel;
    private ControlPanelLogic logic;
    
    public ControlPanelResponseHandler(ControlPanelCommunicationChannel communicationChannel, ControlPanelLogic logic){
        this.communicationChannel = communicationChannel;
        this.logic = logic;
    }

    public void handleResponse(Endpoints client, MessageBody response){

        // Handle based on client type
        if (client == Endpoints.GREENHOUSE) {
            this.handleGreenhouseResponse(response);
        } else if (client == Endpoints.SERVER){
            this.handleServerResponse(response);
        }
        else{
            Logger.error("Unknown client: " + client);
        }
    }

    
  /**
   * Handle a response to a greenhouse command.
   * Processes the command response and performs actions based on the command type.
   *
   * @param body The message body containing the command response
   */
    // TODO refactor.
  private void handleGreenhouseResponse(MessageBody body) {
    // TODO CHANGE!

    Transmission transmission = body.getTransmission();
    if (!(transmission instanceof Response)) {
      Logger.error("Invalid command type: " + transmission.getClass().getName());
      return;
    }

    Response response = (Response) transmission;
    SuccessResponse successResponse;
    if (response instanceof FailureResponse){
      Logger.error("Failed to execute command: " + response);
      return;
      // TODO try to execute command again? If this is done, in the future perhaps an attempts field should be added, 
      // which shows how many times the transmission has been tried sent.
    }
    else if (response instanceof SuccessResponse) {
      successResponse = (SuccessResponse) response;
    }
    else {
      Logger.error("Invalid response type: " + response.getClass().getName());
      return;
    }

    Logger.info("Handling greenhouse command response: " + response);

    Transmission command = successResponse.getTransmission();
    
    if (!(command instanceof GreenhouseCommand)) {
      Logger.error("Invalid command type: " + command.getClass().getName());
      return;
    }

    // TODO should someone else do this? another class?

    String responseData = successResponse.getResponseData();

    if (responseData == null || responseData.isEmpty()) {
      throw new IllegalArgumentException("Sensor specification can't be empty");
    }

    String[] parts = responseData.split(";");

    switch (command.getTransmissionString()) {
      case "GET_NODE_ID":
        // Response data is the node id.
        this.communicationChannel.spawnNode(responseData, 5);
        break;
      case "GET_NODE":

        SensorActuatorNodeInfo nodeInfo = SensorActuatorNodeInfoParser.createSensorNodeInfoFrom(responseData, this.logic);
        this.logic.addNode(nodeInfo);
      
        break;
      case "GET_SENSOR_DATA":


        if (parts.length != 2) {
          throw new IllegalArgumentException("Incorrect specification format: " + responseData);
        }
        int nodeId = parseIntegerOrError(parts[0], "Invalid node ID:" + parts[0]);
        List<SensorReading> sensors = SensorReadingsParser.parseSensors(parts[1]);
        // sensors.removeIf(sensor -> sensor instanceof NoSensorReading);

        this.logic.advertiseSensorData(sensors, nodeId, 1);

        break;
      case "ACTUATOR_CHANGE":
        Logger.info("Received actuator change response: " + responseData);

        if (parts.length != 3) {
          Logger.error("Invalid actuator change response: " + responseData);
          return;
        }

        int nodeId2 = parseIntegerOrError(parts[0], "Invalid node ID:" + parts[0]);
        int actuatorId = parseIntegerOrError(parts[1], "Invalid actuator ID:" + parts[1]);
        boolean actuatorState = parseBooleanOrError(parts[2], "Invalid actuator state:" + parts[2]);
        this.logic.advertiseActuatorState(nodeId2, actuatorId, actuatorState, 1);
        
        break;
      default:
        Logger.error("Unknown command: " + command);
    }
  }

  // TODO refactor
  private void handleServerResponse(MessageBody body){
    // TODO CHANGE!

    Transmission transmission = body.getTransmission();
    if (!(transmission instanceof SuccessResponse || transmission instanceof FailureResponse)) {
      Logger.error("Invalid response type: " + transmission.getClass().getName());
      return;
    }

    Response response = (Response) transmission;
    SuccessResponse successResponse;
    if (response instanceof FailureResponse){

      FailureResponse failureResponse = (FailureResponse) response;

      Logger.error("Failed to execute command, sending again: " + response);

      if (failureResponse.getFailureReason() == FailureReason.FAILED_TO_IDENTIFY_CLIENT){
        ClientIdentification clientIdentification = new ClientIdentification(Endpoints.CONTROL_PANEL, Endpoints.NOT_PREDEFINED.getValue());
        this.communicationChannel.establishConnectionWithServer(clientIdentification);
      }
      else{
        Logger.error("Unknown command: " + response);
      }
      // MessageBody bodyToSend = new MessageBody(response.getTransmission());

      // Message message = new Message(header, bodyToSend);

      // TODO try to execute command again? If this is done, in the future perhaps an attempts field should be added, 
      // which shows how many times the transmission has been tried sent.
    }
    else if (response instanceof SuccessResponse) {
      successResponse = (SuccessResponse) response;
      Logger.info("Success response: " + successResponse);
    }
  }
}
