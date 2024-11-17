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
import no.ntnu.messages.commands.ActuatorChangeCommand;
import no.ntnu.messages.commands.GetNodeCommand;
import no.ntnu.messages.commands.GetNodeIdCommand;
import no.ntnu.messages.commands.GetSensorDataCommand;
import no.ntnu.messages.Delimiters;
import no.ntnu.messages.Message;

/**
 * A communication channel for the control panel. This class is responsible for
 * sending commands to the server and receiving responses. It also listens for
 * incoming events from the server.
 */
public class ControlPanelCommunicationChannel extends SocketCommunicationChannel implements CommunicationChannel {
  private final ControlPanelLogic logic;

  public ControlPanelCommunicationChannel(ControlPanelLogic logic, String host, int port) {
    super(host, port);
    this.logic = logic;
    // TODO should perhaps try to establsih connection with server. (try catch). And if it fails, try like 3 more times.
    this.listenForMessages();
    this.establishConnectionWithServer(Clients.CONTROL_PANEL, "0");
    this.askForSensorDataPeriodically(1, 5);
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
    Clients client = header.getReceiver();

    // Handle based on client type
    if (client == Clients.GREENHOUSE) {
      this.handleGreenhouseCommandResponse(body);
    } else {
      Logger.error("Unknown client: " + client);
    }
  }

  private void handleGreenhouseCommandResponse(MessageBody body) {
    // TODO CHANGE!
    String respondedToCommand = body.getCommand().toProtocolString();
    String response = body.getData();

    Logger.info("Handling greenhouse command response: " + respondedToCommand);
    
    // TODO should someone else do this? another class?
    switch (respondedToCommand.trim()) {
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
    MessageBody body = new MessageBody(new ActuatorChangeCommand(actuatorId, isOn));
    Message message = new Message(header, body);
    this.sendCommandToServer(message);
  }

  public void askForSensorDataPeriodically(int nodeId, int period) {
    Thread thread = new Thread(() -> {
      Timer timer = new Timer();
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          MessageHeader header = new MessageHeader(Clients.GREENHOUSE, Integer.toString(nodeId));
          MessageBody body = new MessageBody(new GetSensorDataCommand());
          Message message = new Message(header, body);
          sendCommandToServer(message);
        }
      }, 0, period * 1000L);
    });
    thread.start();
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
    MessageBody body = new MessageBody(new GetNodeCommand());
    Message message = new Message(header, body);
    this.sendCommandToServer(message);
  }

  public void spawnNode(String nodeId, int START_DELAY) {
    MessageHeader header = new MessageHeader(Clients.GREENHOUSE, nodeId);
    MessageBody body = new MessageBody(new GetNodeCommand());
    Message message = new Message(header, body);
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
    String[] readingInfo = sensorInfo.split(",");
    for (String reading : readingInfo) {
      readings.add(parseReading(reading));
    }
    return readings;
  }

  // TODO improve...
  private SensorReading parseReading(String reading) {
    Logger.info("Reading: " + reading);
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
