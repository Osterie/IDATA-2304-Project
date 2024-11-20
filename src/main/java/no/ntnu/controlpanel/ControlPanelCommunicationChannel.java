package no.ntnu.controlpanel;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static no.ntnu.tools.Parser.parseDoubleOrError;
import static no.ntnu.tools.Parser.parseIntegerOrError;

import no.ntnu.Endpoints;
import no.ntnu.SocketCommunicationChannel;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.sensors.NumericSensor;
import no.ntnu.greenhouse.sensors.SensorReading;
import no.ntnu.tools.Logger;

import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.commands.Command;
import no.ntnu.messages.greenhousecommands.ActuatorChangeCommand;
import no.ntnu.messages.greenhousecommands.GetNodeCommand;
import no.ntnu.messages.greenhousecommands.GetSensorDataCommand;
import no.ntnu.messages.greenhousecommands.GreenhouseCommand;
import no.ntnu.messages.Delimiters;
import no.ntnu.messages.Message;

/**
 * A communication channel for the control panel. This class is responsible for
 * sending commands to the server and receiving responses. It also listens for
 * incoming events from the server.
 */
public class ControlPanelCommunicationChannel extends SocketCommunicationChannel implements CommunicationChannel {
  private final ControlPanelLogic logic;
  private String targetId = "1";

  public ControlPanelCommunicationChannel(ControlPanelLogic logic, String host, int port) {
    super(host, port);
    this.logic = logic;

    // TODO should perhaps try to establsih connection with server. (try catch). And if it fails, try like 3 more times.
    // Don't use chatgpt or copilot and preferably, remember design patterns, cohesion, coupling and such.

    this.listenForMessages();
    this.establishConnectionWithServer(Endpoints.CONTROL_PANEL, "0");
    this.askForSensorDataPeriodically(5); //TODO change id to be the id of the current panel. changes when changing panel.
  }

  @Override
  protected void handleMessage(String serverMessage) {
    // Attempt to parse the server message
    Message message;
    try {
      message = Message.fromProtocolString(serverMessage);
    } catch (IllegalArgumentException | NullPointerException e) {
      Logger.error("Invalid server message format: " + serverMessage + ". Error: " + e.getMessage());
      return;
    }

    // Check for null message, header, or body
    if (message == null || message.getHeader() == null || message.getBody() == null) {
      Logger.error("Message, header, or body is missing in server message: " + serverMessage);
      return;
    }

    // Extract header and body
    MessageHeader header = message.getHeader();
    MessageBody body = message.getBody();
    Endpoints client = header.getReceiver();

    // Handle based on client type
    if (client == Endpoints.GREENHOUSE) {
      this.handleGreenhouseCommandResponse(body);
    } else {
      Logger.error("Unknown client: " + client);
    }
  }

  // TODO refactor.
  private void handleGreenhouseCommandResponse(MessageBody body) {
    // TODO CHANGE!
    Command command = body.getCommand();
    String response = body.getData();

    Logger.info("Handling greenhouse command response: " + command.toProtocolString());
    
    if (!(command instanceof GreenhouseCommand)) {
      Logger.error("Invalid command type: " + command.getClass().getName());
      return;
    }

    // TODO should someone else do this? another class?

    switch (command.getCommandString()) {
      case "GET_NODE_ID":
        this.spawnNode(response, 5);
        break;
      case "GET_NODE":
        this.addNode(response);
        break;
      case "GET_SENSOR_DATA":
        Logger.info("Received sensor data: " + response);
        this.advertiseSensorData(response, 1);
        break;
      case "ACTUATOR_CHANGE":
        Logger.info("Received actuator change response: " + response);
        String[] parts = response.split(Delimiters.BODY_PARAMETERS_DELIMITER.getValue());
        if (parts.length != 3) {
          Logger.error("Invalid actuator change response: " + response);
          return;
        }
        String nodeId = parts[0];
        String actuatorId = parts[1];
        String actuatorState = parts[2];

        if (actuatorState.equals("ON") || actuatorState.equals("OFF")) {
          boolean isOn = actuatorState.equals("ON");
          this.advertiseActuatorState(Integer.parseInt(nodeId), Integer.parseInt(actuatorId), isOn, 1);
        } 
        else {
          Logger.error("Invalid actuator state: " + actuatorState);
        }
        break;
      default:
        Logger.error("Unknown command: " + command.toProtocolString());
    }
  }

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
      this.sendCommandToServer(message);
    } catch (Exception e) {
      Logger.error("Failed to send actuator change: " + e.getMessage());
    }
  }

  public void setSensorNodeTarget(String targetId){
    this.targetId = targetId;
  }

  public String getSensorNoderTarget(){
    return this.targetId;
  }

  public void askForSensorDataPeriodically(int period) {

    ControlPanelCommunicationChannel self = this;

    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        MessageHeader header = new MessageHeader(Endpoints.GREENHOUSE, self.getSensorNoderTarget());
        MessageBody body = new MessageBody(new GetSensorDataCommand());
        Message message = new Message(header, body);
        sendCommandToServer(message);
      }
    }, 3000, period * 1000L);
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
    MessageHeader header = new MessageHeader(Endpoints.GREENHOUSE, "ALL");
    MessageBody body = new MessageBody(new GetNodeCommand());
    Message message = new Message(header, body);
    this.sendCommandToServer(message);
    } catch (Exception e) {
      Logger.error("Failed to ask for nodes: " + e.getMessage());
    }
  }

  public void spawnNode(String nodeId, int START_DELAY) {
    try {
      MessageHeader header = new MessageHeader(Endpoints.GREENHOUSE, nodeId);
      MessageBody body = new MessageBody(new GetNodeCommand());
      Message message = new Message(header, body);
      this.sendCommandToServer(message);
    } catch (Exception e) {
      Logger.error("Failed to spawn node: " + e.getMessage());
    }
  }

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

  private void parseActuators(String actuatorSpecification, SensorActuatorNodeInfo info) {
    if (actuatorSpecification == null || actuatorSpecification.isEmpty()) {
      throw new IllegalArgumentException("Actuator specification can't be empty");
    }
    String[] parts = actuatorSpecification.split(";");
    for (String part : parts) {
      this.parseActuatorInfo(part, info);
    }
  }

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

  private List<SensorReading> parseSensors(String sensorInfo) {
    if (sensorInfo == null || sensorInfo.isEmpty()) {
      throw new IllegalArgumentException("Sensor info can't be empty");
    }
    List<SensorReading> readings = new LinkedList<>();
    String[] readingInfo = sensorInfo.split(",");
    for (String reading : readingInfo) {
      readings.add(parseReading(reading));
    }
    return readings;
  }

  // TODO improve...
  private SensorReading parseReading(String reading) {
    Logger.info("Reading: " + reading);
    if (reading == null || reading.isEmpty()) {
      throw new IllegalArgumentException("Sensor reading can't be empty");
    }
    String[] assignmentParts = reading.split("=");
    if (assignmentParts.length != 2) {
      throw new IllegalArgumentException("Invalid sensor reading specified: " + reading);
    }
    String[] valueParts = assignmentParts[1].split(" ");
    if (valueParts.length != 2) {
      throw new IllegalArgumentException("Invalid sensor value/unit: " + reading);
    }
    String sensorType = assignmentParts[0];
    double value = parseDoubleOrError(valueParts[0], "Invalid sensor value: " + valueParts[0]);
    String unit = valueParts[1];
    NumericSensor sensor = new NumericSensor(sensorType, value, value, value, unit);
    return sensor.getReading();
  }

  /**
   * Advertise that an actuator has changed it's state.
   *
   * @param nodeId     ID of the node to which the actuator is attached
   * @param actuatorId ID of the actuator.
   * @param on         When true, actuator is on; off when false.
   * @param delay      The delay in seconds after which the advertisement will be
   *                   generated
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
