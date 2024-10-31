package no.ntnu.controlpanel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static no.ntnu.tools.Parser.parseDoubleOrError;
import static no.ntnu.tools.Parser.parseIntegerOrError;

import no.ntnu.Clients;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.NumericSensor;
import no.ntnu.greenhouse.sensorreading.SensorReading;
import no.ntnu.messages.Message;
import no.ntnu.tools.Logger;

import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.MessageTest;

/**
 * A communication channel for the control panel. This class is responsible for
 * sending commands to the server and receiving responses. It also listens for
 * incoming events from the server.
 */
public class ControlPanelCommunicationChannel implements CommunicationChannel {
  private final ControlPanelLogic logic;
  private Socket socket;
  private BufferedReader socketReader;
  private PrintWriter socketWriter;
  private boolean isOn;

  public ControlPanelCommunicationChannel(ControlPanelLogic logic, String host, int port) throws IOException {
    this.logic = logic;
    this.start(host, port);
    this.establishConnectionWithServer();
    this.listenForServerMessages();
  }

  private void listenForServerMessages(){
    Thread messageListener = new Thread(() -> {
      try {
        while (isOn) {
          if (socketReader.ready()) {
            String serverMessage = socketReader.readLine();
            if (serverMessage != null) {
              Logger.info("Received from server: " + serverMessage);
              this.handleServerCommand(serverMessage);
            }
          }
        }
        Logger.info("Server message listener stopped.");
      } catch (IOException e) {
        Logger.error("Connection lost: " + e.getMessage());
      } 
      finally {
        close();
      }
    });
    messageListener.start();
  }

  private void handleServerCommand(String serverMessage) {

    // TODO what if invalid serverMessage?
    MessageTest message = MessageTest.fromProtocolString(serverMessage);

    MessageHeader header = message.getHeader();
    MessageBody body = message.getBody();

    Clients client = header.getReceiver();
    String nodeIdRaw = header.getId();
    
    String command = body.getCommand();
    String response = body.getData();

    Integer nodeId = parseIntegerOrError(nodeIdRaw, "Invalid node ID: " + nodeIdRaw);

    if (client == Clients.GREENHOUSE) {
      Logger.info("Handling greenhouse command: " + command);
      this.handleGreenhouseCommandResponse(nodeId, command, response);
    }
    else {
      Logger.error("Unknown client: " + client);
    }


    // switch (client) {
    //   case GREENHOUSE:
    //     Logger.info("Handling greenhouse command: " + command);
    //     this.handleGreenhouseCommandResponse(nodeId, command, response); 
    //     break;
    //   default:
    //     Logger.error("Unknown client: " + client);
    // }
  }

  private void handleGreenhouseCommandResponse(int nodeId, String command, String response) {
    
    switch (command.trim()) {
      case "GET_NODE_ID":
        this.spawnNode(response, 5);
        break;
      case "GET_NODE":
        this.addNode(response);
        break;
      default:
        Logger.error("Unknown command: " + command);
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

  private void start(String host, int port) throws IOException {
    try {
      this.socket = new Socket(host, port);
      this.socket.setKeepAlive(true);
      this.socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      this.socketWriter = new PrintWriter(socket.getOutputStream(), true);
      this.isOn = true;
      Logger.info("Socket connection established with " + host + ":" + port);

    } catch (IOException e) {
      Logger.error("Could not establish connection to the server: " + e.getMessage());
    }
  }

  // TODO this should be done in another way, use a protocol with header and body instead and such?
  private void establishConnectionWithServer() {
    // Send initial identifier to server
    String identifierMessage = Clients.CONTROL_PANEL.getValue() + ";0"; // TODO generate unique identifier, or let
                                                                        // server do it?
    socketWriter.println(identifierMessage);
    Logger.info("connecting control panel 0 with identifier: " + identifierMessage);
  }

  public void sendCommandToServerNoResponse(MessageTest message) {
    if (isOn && socketWriter != null) {
      Logger.info("Trying to send message...");
      socketWriter.println(message.toProtocolString());
      Logger.info("Sent message to server: " + message);
    } else {
      Logger.error("Unable to send message, socket is not connected.");
    }
  }

  @Override
  public void sendActuatorChange(int nodeId, int actuatorId, boolean isOn) {
    String nodeIdStr = Integer.toString(nodeId);
    MessageHeader header = new MessageHeader(Clients.GREENHOUSE, nodeIdStr);
    MessageBody body = new MessageBody("ACTUATOR_CHANGE;" + actuatorId + ";" + (isOn ? "ON" : "OFF"));
    MessageTest message = new MessageTest(header, body);
    this.sendCommandToServerNoResponse(message);
    // String command = Clients.GREENHOUSE + ";" + nodeId + "-ACTUATOR_CHANGE;" + actuatorId + ";" + (isOn ? "ON" : "OFF");
    // sendCommandToServerNoResponse(command);
  }

  @Override
  public boolean open() {
    return isOn;
  }

  @Override
  public boolean close() {

    boolean closed = false;

    try {
      if (socket != null)
        socket.close();
      if (socketReader != null)
        socketReader.close();
      if (socketWriter != null)
        socketWriter.close();
      isOn = false;
      Logger.info("Socket connection closed.");
      closed = true;
    } catch (IOException e) {
      Logger.error("Failed to close socket connection: " + e.getMessage());
    }
    return closed;
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
    this.sendCommandToServerNoResponse(message);
  }

  public void spawnNode(String nodeId, int START_DELAY) {
    MessageHeader header = new MessageHeader(Clients.GREENHOUSE, nodeId);
    MessageBody body = new MessageBody("GET_NODE");
    MessageTest message = new MessageTest(header, body);
    this.sendCommandToServerNoResponse(message);
  }

  private SensorActuatorNodeInfo createSensorNodeInfoFrom(String specification) {
    Logger.info("specification: " + specification);
    if (specification == null || specification.isEmpty()) {
      throw new IllegalArgumentException("Node specification can't be empty");
    }
    String[] parts = specification.split(";", 2);
    // if (parts.length > 3) {
    //   throw new IllegalArgumentException("Incorrect specification format");
    // }
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

    // int actuatorCount = parseIntegerOrError(actuatorInfo[0],
    //     "Invalid actuator count: " + actuatorInfo[0]);
    // String actuatorType = actuatorInfo[1];
    // for (int i = 0; i < actuatorCount; ++i) {
    //   Actuator actuator = new Actuator(actuatorType, info.getId());
    //   actuator.setListener(logic);
    //   info.addActuator(actuator);
    // }
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
