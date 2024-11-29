package no.ntnu.controlpanel;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static no.ntnu.tools.Parser.parseIntegerOrError;

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

    switch (command.getTransmissionString()) {
      case "GET_NODE_ID":
        this.spawnNode(responseData, 5);
        break;
      case "GET_NODE":

      SensorActuatorNodeInfo nodeInfo = SensorActuatorNodeInfoParser.createSensorNodeInfoFrom(responseData, this.logic);
        this.logic.addNode(nodeInfo);
      
        break;
      case "GET_SENSOR_DATA":
        Logger.info("Received sensor data: " + responseData);
        this.advertiseSensorData(responseData, 1);
        break;
      case "ACTUATOR_CHANGE":
        Logger.info("Received actuator change response: " + responseData);
        String[] parts = responseData.split(Delimiters.BODY_FIELD_PARAMETERS.getValue());
        if (parts.length != 3) {
          Logger.error("Invalid actuator change response: " + responseData);
          return;
        }
        String nodeId = parts[0];
        String actuatorId = parts[1];
        String actuatorState = parts[2];

        if (actuatorState.equals("1") || actuatorState.equals("0")) {
          boolean isOn = actuatorState.equals("1");
          this.advertiseActuatorState(Integer.parseInt(nodeId), Integer.parseInt(actuatorId), isOn, 1);
        } 
        else {
          Logger.error("Invalid actuator state: " + actuatorState);
        }
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

  /**
   * Spawn a new sensor/actuator node information after a given delay.
   * Sends a command to the server to spawn a new node after a specified delay.
   *
   * @param nodeId The ID of the node to spawn
   * @param START_DELAY The delay in seconds before spawning the node
   */
  public void spawnNode(String nodeId, int START_DELAY) {
    try {
      MessageHeader header = new MessageHeader(Endpoints.GREENHOUSE, nodeId);
      MessageBody body = new MessageBody(new GetNodeCommand());
      Message message = new Message(header, body);
    //   TODO do differently
      this.communicationChannel.sendMessage(message);
    } catch (Exception e) {
      Logger.error("Failed to spawn node: " + e.getMessage());
    }
  }

  /**
   * Advertise new sensor readings.
   * Notifies the control panel logic of new sensor readings after a specified delay.
   * 
   * @param specification Specification of the readings in the following format:
   *                      [nodeID]
   *                      semicolon
   *                      [sensor_type_1] equals [sensor_value_1] space [unit_1]
   *                      comma
   *                      ...
   *                      comma
   *                      [sensor_type_N] equals [sensor_value_N] space [unit_N]
   * @param delay         Delay in seconds
   */
  public void advertiseSensorData(String specification, int delay) {
    if (specification == null || specification.isEmpty()) {
      throw new IllegalArgumentException("Sensor specification can't be empty");
    }
    String[] parts = specification.split(";");
    if (parts.length != 2) {
      throw new IllegalArgumentException("Incorrect specification format: " + specification);
    }
    int nodeId = parseIntegerOrError(parts[0], "Invalid node ID:" + parts[0]);
    List<SensorReading> sensors = SensorReadingsParser.parseSensors(parts[1]);
    sensors.removeIf(sensor -> sensor instanceof NoSensorReading);
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        logic.onSensorData(nodeId, sensors);
      }
    }, delay * 1000L);
  }

  /**
   * Advertise that a node is removed.
   * Notifies the control panel logic that a node has been removed after a specified delay.
   *
   * @param nodeId ID of the removed node
   * @param delay  Delay in seconds
   */
  public void advertiseRemovedNode(int nodeId, int delay) {
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        logic.onNodeRemoved(nodeId);
      }
    }, delay * 1000L);
  }


  /**
   * Advertise that an actuator has changed its state.
   * Notifies the control panel logic of an actuator state change after a specified delay.
   *
   * @param nodeId     ID of the node to which the actuator is attached
   * @param actuatorId ID of the actuator
   * @param on         When true, actuator is on; off when false
   * @param delay      The delay in seconds after which the advertisement will be generated
   */
  public void advertiseActuatorState(int nodeId, int actuatorId, boolean on, int delay) {
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        logic.onActuatorStateChanged(nodeId, actuatorId, on);
      }
    }, delay * 1000L);
    }
}
