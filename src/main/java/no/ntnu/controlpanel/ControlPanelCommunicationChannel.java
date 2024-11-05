package no.ntnu.controlpanel;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static no.ntnu.tools.Parser.parseDoubleOrError;
import static no.ntnu.tools.Parser.parseIntegerOrError;

import no.ntnu.Clients;
import no.ntnu.SocketCommunicationChannel;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.sensors.NumericSensor;
import no.ntnu.greenhouse.sensors.SensorReading;
import no.ntnu.tools.Logger;

import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.MessageTest;

/**
 * A communication channel for the control panel. This class is responsible for
 * sending commands to the server and receiving responses. It also listens for
 * incoming events from the server.
 */
public class ControlPanelCommunicationChannel extends SocketCommunicationChannel implements CommunicationChannel {
  private final ControlPanelLogic logic;

  public ControlPanelCommunicationChannel(ControlPanelLogic logic, String host, int port) throws IOException {
    super(host, port);
    this.logic = logic;
    // TODO should perhaps try to establsih connection with server. (try catch). And if it fails, try like 3 more times.
    this.listenForMessages();
    this.establishConnectionWithServer();
  }

  // TODO this should be done in another way, use a protocol with header and body instead and such?
  private void establishConnectionWithServer() {
    // Send initial identifier to server
    String identifierMessage = Clients.CONTROL_PANEL.getValue() + ";0"; // TODO generate unique identifier, or let
                                                                        // server do it?
    this.socketWriter.println(identifierMessage);
    Logger.info("connecting control panel 0 with identifier: " + identifierMessage);
  }

  @Override
  protected void handleMessage(String serverMessage) {

    // TODO handle invalid serverMessage.
    MessageTest message = MessageTest.fromProtocolString(serverMessage);

    MessageHeader header = message.getHeader();
    MessageBody body = message.getBody();

    Clients client = header.getReceiver();

    if (client == Clients.GREENHOUSE) {
      this.handleGreenhouseCommandResponse(body);
    }
    else {
      Logger.error("Unknown client: " + client);
    }
  }

  private void handleGreenhouseCommandResponse(MessageBody body) {
    String respondedToCommand = body.getCommand();
    String response = body.getData();

    Logger.info("Handling greenhouse command response: " + respondedToCommand);
    
    // TODO should someone else do this? @SebasoOlsen
    switch (respondedToCommand.trim()) {
      case "GET_NODE_ID":
        this.spawnNode(response, 5);
        break;
      case "GET_NODE":
        this.addNode(response);
        break;
      default:
        Logger.error("Unknown command: " + respondedToCommand);
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
    String nodeIdStr = Integer.toString(nodeId);
    MessageHeader header = new MessageHeader(Clients.GREENHOUSE, nodeIdStr);
    MessageBody body = new MessageBody("ACTUATOR_CHANGE;" + actuatorId + ";" + (isOn ? "ON" : "OFF"));
    MessageTest message = new MessageTest(header, body);
    this.sendCommandToServer(message);
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
    MessageHeader header = new MessageHeader(Clients.GREENHOUSE, "ALL");
    MessageBody body = new MessageBody("GET_NODE_ID");
    MessageTest message = new MessageTest(header, body);
    this.sendCommandToServer(message);
  }

  public void spawnNode(String nodeId, int START_DELAY) {
    MessageHeader header = new MessageHeader(Clients.GREENHOUSE, nodeId);
    MessageBody body = new MessageBody("GET_NODE");
    MessageTest message = new MessageTest(header, body);
    this.sendCommandToServer(message);
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
    String[] parts = actuatorSpecification.split(";");
    for (String part : parts) {
      this.parseActuatorInfo(part, info);
    }
  }

  private void parseActuatorInfo(String s, SensorActuatorNodeInfo info) {
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
    List<SensorReading> readings = new LinkedList<>();
    String[] readingInfo = sensorInfo.split(";");
    for (String reading : readingInfo) {
      readings.add(parseReading(reading));
    }
    return readings;
  }

  private SensorReading parseReading(String reading) {
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
