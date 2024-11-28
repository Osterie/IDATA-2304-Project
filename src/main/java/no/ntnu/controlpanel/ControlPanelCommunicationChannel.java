package no.ntnu.controlpanel;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static no.ntnu.tools.Parser.parseDoubleOrError;
import static no.ntnu.tools.Parser.parseIntegerOrError;

import no.ntnu.SocketCommunicationChannel;
import no.ntnu.constants.Endpoints;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.sensors.*;
import no.ntnu.greenhouse.sensors.NoImageSensorReading;
import no.ntnu.intermediaryserver.clienthandler.ClientIdentification;
import no.ntnu.tools.Logger;
import no.ntnu.tools.stringification.Base64AudioEncoder;
import no.ntnu.tools.stringification.Base64ImageEncoder;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.Transmission;
import no.ntnu.messages.commands.common.ClientIdentificationTransmission;
import no.ntnu.messages.commands.greenhouse.ActuatorChangeCommand;
import no.ntnu.messages.commands.greenhouse.GetNodeCommand;
import no.ntnu.messages.commands.greenhouse.GetSensorDataCommand;
import no.ntnu.messages.commands.greenhouse.GreenhouseCommand;
import no.ntnu.messages.responses.FailureReason;
import no.ntnu.messages.responses.FailureResponse;
import no.ntnu.messages.responses.Response;
import no.ntnu.messages.responses.SuccessResponse;
import no.ntnu.messages.Delimiters;
import no.ntnu.messages.Message;

// TODO refactor this class. it does sooooo much

/**
 * A communication channel for the control panel. This class is responsible for
 * sending commands to the server and receiving responses. It also listens for
 * incoming events from the server.
 */
public class ControlPanelCommunicationChannel extends SocketCommunicationChannel implements CommunicationChannel {
  private final ControlPanelLogic logic;
  private String targetId = Endpoints.BROADCAST.getValue(); // Used to target a greenhouse node for sensor data requests
  

  /**
   * Create a communication channel for the control panel.
   * Initializes the communication channel with the specified logic, host, and port.
   *
   * @param logic The control panel logic
   * @param host  The server host
   * @param port  The server port
   */
  public ControlPanelCommunicationChannel(ControlPanelLogic logic, String host, int port) {
    super(host, port);
    this.logic = logic;

    // TODO should perhaps try to establish connection with server. (try catch). And if it fails, try like 3 more times.
    // Don't use chatgpt or copilot and preferably, remember design patterns, cohesion, coupling and such.

    // TODO the classes that extend the sockec communication channel should be able to just call establisconnection or something without parameters.
    // Or WHATEVER. refactor
    ClientIdentification clientIdentification = new ClientIdentification(Endpoints.CONTROL_PANEL, Endpoints.NOT_PREDEFINED.getValue());
    this.establishConnectionWithServer(clientIdentification);
  }

  /**
   * Handle a message received from the server.
   * Parses the server message and processes it based on the client type.
   *
   * @param serverMessage The message received from the server
   */
  @Override
  protected void handleMessage(Message message) {
    
    Logger.info("Received message from server: " + message);

    // Extract header and body
    MessageHeader header = message.getHeader();
    MessageBody body = message.getBody();
    Endpoints client = header.getReceiver();

    // Handle based on client type
    if (client == Endpoints.GREENHOUSE) {
      this.handleGreenhouseResponse(body);
    } else if (client == Endpoints.SERVER){
      this.handleServerResponse(body);
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
    if (!(transmission instanceof SuccessResponse || transmission instanceof FailureResponse)) {
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
        this.addNode(responseData);
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
        this.establishConnectionWithServer(clientIdentification);
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
   * Add a node based on the response.
   * Creates a new node and schedules its addition to the control panel logic.
   *
   * @param response The response containing node information
   */
  private void addNode(String response) {
    SensorActuatorNodeInfo nodeInfo = this.createSensorNodeInfoFrom(response);

    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        Logger.info("Spawning node " + response);
        logic.onNodeAdded(nodeInfo);
      }
    }, 5 * 1000L);
  }

  /**
   * Send an actuator change command to the server.
   * Constructs and sends a message to change the state of an actuator.
   *
   * @param nodeId     The ID of the node
   * @param actuatorId The ID of the actuator
   * @param isOn       The desired state of the actuator
   */
  @Override
  public void sendActuatorChange(int nodeId, int actuatorId, boolean isOn) {
    // TODO do not just catch exception. No clue what exception is caught.
    // Does creating the message cause an exception?
    // Does sending the message cause an exception?
    // By having both in try catch, seems like both can fail, but in reality probably only sending can fail.
    // Have custom exceptions.
    // don't use chatgpt or copilot preferably...
    try {
      String nodeIdStr = Integer.toString(nodeId);
      MessageHeader header = new MessageHeader(Endpoints.GREENHOUSE, nodeIdStr);
      MessageBody body = new MessageBody(new ActuatorChangeCommand(actuatorId, isOn));
      Message message = new Message(header, body);
      this.sendMessage(message);
    } catch (Exception e) {
      Logger.error("Failed to send actuator change: " + e.getMessage());
    }
  }

  /**
   * Set the target sensor node ID.
   *
   * @param targetId The target sensor node ID
   */
  public void setSensorNodeTarget(String targetId){
    this.targetId = targetId;
  }

  /**
   * Get the target sensor node ID.
   *
   * @return The target sensor node ID
   */
  public String getSensorNoderTarget(){
    return this.targetId;
  }

  /**
   * Request sensor data periodically.
   * Schedules periodic requests for sensor data from the server.
   *
   * @param period The period in seconds between requests
   */
  public void askForSensorDataPeriodically(int period) {
    ControlPanelCommunicationChannel self = this;

    // TODO: Hashing here?
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        MessageHeader header = new MessageHeader(Endpoints.GREENHOUSE, self.getSensorNoderTarget());
        MessageBody body = new MessageBody(new GetSensorDataCommand());
        Message message = new Message(header, body);
        if (self.isOn && !self.isReconnecting()) {
          self.sendMessage(message);
        }
        else{
          Logger.info("Unable to send message...");
        }
      }
    }, 5000, period * 1000L);
  }

  /**
   * Spawn a new sensor/actuator node information after a given delay.
   *
   * @param specification A (temporary) manual configuration of the node in the
   *                      following format
   *                      [nodeId] semicolon
   *                      [actuator_count_1] underscore [actuator_type_1] space
   *                      ... space
   *                      [actuator_count_M] underscore [actuator_type_M]
   */
  public void askForNodes() {
    // TODO do not just catch exception. No clue what exception is caught.
    // Does creating the message cause an exception?
    // Does sending the message cause an exception?
    // By having both in try catch, seems like both can fail, but in reality probably only sending can fail.
    // Have custom exceptions.
    // don't use chatgpt or copilot preferably...
    try {
      MessageHeader header = new MessageHeader(Endpoints.GREENHOUSE, Endpoints.BROADCAST.getValue());
      MessageBody body = new MessageBody(new GetNodeCommand());
      Message message = new Message(header, body);
      this.sendMessage(message);
    } catch (Exception e) {
      Logger.error("Failed to ask for nodes: " + e.getMessage());
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
      this.sendMessage(message);
    } catch (Exception e) {
      Logger.error("Failed to spawn node: " + e.getMessage());
    }
  }

  /**
   * Create a SensorActuatorNodeInfo object from a specification string.
   * Parses the specification string to create a SensorActuatorNodeInfo object.
   *
   * @param specification The specification string
   * @return The created SensorActuatorNodeInfo object
   */
  // TODO someone else should do this.
  private SensorActuatorNodeInfo createSensorNodeInfoFrom(String specification) {
    Logger.info("specification: " + specification);
    if (specification == null || specification.isEmpty()) {
      throw new IllegalArgumentException("Node specification can't be empty");
    }
    String[] parts = specification.split(";", 2);
    int nodeId = parseIntegerOrError(parts[0], "Invalid node ID:" + parts[0]);
    SensorActuatorNodeInfo info = new SensorActuatorNodeInfo(nodeId);

    if (parts.length == 2) {
      this.parseActuators(parts[1], info);
    }
    return info;
  }

  /**
   * Parse actuators from a specification string and add them to the node info.
   * Extracts actuator information from the specification string and adds it to the node info.
   *
   * @param actuatorSpecification The actuator specification string
   * @param info The SensorActuatorNodeInfo object to add actuators to
   */
  private void parseActuators(String actuatorSpecification, SensorActuatorNodeInfo info) {
    if (actuatorSpecification == null || actuatorSpecification.isEmpty()) {
      throw new IllegalArgumentException("Actuator specification can't be empty");
    }
    String[] parts = actuatorSpecification.split(";");
    for (String part : parts) {
      this.parseActuatorInfo(part, info);
    }
  }

  /**
   * Parse actuator information from a string and add it to the node info.
   * Extracts individual actuator details from the string and adds them to the node info.
   *
   * @param s The actuator information string
   * @param info The SensorActuatorNodeInfo object to add the actuator to
   */
  private void parseActuatorInfo(String s, SensorActuatorNodeInfo info) {
    if (s == null || s.isEmpty()) {
      throw new IllegalArgumentException("Actuator info can't be empty");
    }
    String[] actuatorInfo = s.split("_");
    if (actuatorInfo.length != 2) {
      throw new IllegalArgumentException("Invalid actuator info format: " + s);
    }

    String actuatorType = actuatorInfo[0];
    int  actuatorId = parseIntegerOrError(actuatorInfo[1],
        "Invalid actuator count: " + actuatorInfo[0]);

    Actuator actuator = new Actuator(actuatorId, actuatorType, info.getId());
    actuator.setListener(logic);
    info.addActuator(actuator);
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
    List<SensorReading> sensors = parseSensors(parts[1]);
    sensors.removeIf(sensor -> sensor instanceof NoImageSensorReading);
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
   * Parse sensor readings from a string.
   * Extracts sensor readings from the provided string and returns them as a list.
   *
   * @param sensorInfo The sensor information string
   * @return A list of parsed sensor readings
   */
  private List<SensorReading> parseSensors(String sensorInfo) {
    if (sensorInfo == null || sensorInfo.isEmpty()) {
      throw new IllegalArgumentException("Sensor info can't be empty");
    }
    List<SensorReading> readings = new LinkedList<>();
    String[] readingInfo = sensorInfo.split(",");
    for (String reading : readingInfo) {
      try{
        readings.add(parseReading(reading));
      }
      catch (IllegalArgumentException e){
        Logger.error("Failed to parse sensor reading: " + e.getMessage());
      }
    }
    return readings;
  }

  
  /**
   * Parses a sensor reading from a string and returns a SensorReading object.
   *
   * @param reading the sensor reading string in the format "type=value unit" or "image=base64String fileExtension"
   * @return a SensorReading object representing the parsed sensor reading
   * @throws IllegalArgumentException if the reading is null, empty, or not in the expected format
   */
  private SensorReading parseReading(String reading) {
    Logger.info("Reading: " + reading);
    if (reading == null || reading.isEmpty()) {
      throw new IllegalArgumentException("Sensor reading can't be empty");
    }
    String[] formatParts = reading.split(":");
    if (formatParts.length != 2) {
      throw new IllegalArgumentException("Invalid sensor format/data: " + reading);
    }
    String[] assignmentParts = formatParts[1].split("=");
    if (assignmentParts.length != 2) {
      throw new IllegalArgumentException("Invalid sensor reading specified: " + reading);
    }
    String[] valueParts = assignmentParts[1].split(" ");
    if (valueParts.length != 3) {
      throw new IllegalArgumentException("Invalid sensor value/unit: " + reading);
    }
    if (formatParts[0].equals("IMG")) {
      if ("NoImage".equals(valueParts[2])) {
        return new NoImageSensorReading();
      }
      String type = assignmentParts[0];
      String base64String = valueParts[1];
      String fileExtension = valueParts[2];

      BufferedImage image;
      try {
        image = Base64ImageEncoder.stringToImage(base64String);
      } catch (IOException e) {
        throw new IllegalArgumentException("Failed to decode image: " + e.getMessage(), e);
      }
      ImageSensorReading imageReading = new ImageSensorReading(type, image);
      imageReading.setFileExtension(fileExtension);
      
      return imageReading;
    }
    else if (formatParts[0].equals("NUM")) {
      String type = assignmentParts[0];
      double value = parseDoubleOrError(valueParts[1], "Invalid sensor value: " + valueParts[1]);
      String unit = valueParts[2];
      return new NumericSensorReading(type, value, unit);
    }
    else if (formatParts[0].equals("AUD")) {
      String type = assignmentParts[0];
      String base64String = valueParts[1];
      String fileExtension = valueParts[2];

      File audioFile;
      try {
        audioFile = Base64AudioEncoder.stringToAudio(base64String);
      } catch (IOException e) {
        throw new IllegalArgumentException("Failed to decode audio: " + e.getMessage(), e);
      }
      return new AudioSensorReading(type, audioFile);
    }
    else {
      throw new IllegalArgumentException("Unknown sensor format: " + formatParts[0]);
    }
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
