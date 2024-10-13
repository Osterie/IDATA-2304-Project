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

import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.SensorReading;
import no.ntnu.tools.Logger;

public class SocketCommunicationChannel implements CommunicationChannel {
    private final ControlPanelLogic logic;
    private Socket socket;
    private BufferedReader socketReader;
    private PrintWriter socketWriter;
    private boolean isConnected;

    public SocketCommunicationChannel(ControlPanelLogic logic, String host, int port) throws IOException {
        this.logic = logic;
        connect(host, port);
    }

    private void connect(String host, int port) throws IOException {
        try {
            this.socket = new Socket(host, port);
            this.socket.setKeepAlive(true);
            this.socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.socketWriter = new PrintWriter(socket.getOutputStream(), true);
            this.socketWriter.println("CONTROL_PANEL;0"); //TODO Use a unique identifier
            this.isConnected = true;
            Logger.info("Socket connection established with " + host + ":" + port);
        } catch (IOException e) {
            Logger.error("Failed to connect to the server: " + e.getMessage());
            throw e;
        }
    }

    public String sendCommandToServer(String command) {
        String serverResponse = "No response";
        if (isConnected && socketWriter != null) {
            socketWriter.println(command);
            Logger.info("Sent command to server: " + command);
            try {
                serverResponse = socketReader.readLine();
                Logger.info("Received response from server: " + serverResponse);
            } catch (IOException e) {
                Logger.error("Error reading server response: " + e.getMessage() + " error type" + e.getClass());
            }
        } else {
            Logger.error("Unable to send command, socket is not connected.");
        }
        return serverResponse;
    }

    @Override
    public void sendActuatorChange(int nodeId, int actuatorId, boolean isOn) {
        String command = "ACTUATOR_CHANGE:" + nodeId + "," + actuatorId + "," + (isOn ? "ON" : "OFF");
        String respone = sendCommandToServer(command);
    }

    @Override
    public boolean open() {
        return isConnected;
    }

    @Override
    public boolean close() {

        boolean closed = false;
        
        try {
            if (socket != null) socket.close();
            if (socketReader != null) socketReader.close();
            if (socketWriter != null) socketWriter.close();
            isConnected = false;
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
   * @param specification A (temporary) manual configuration of the node in the following format
   *                      [nodeId] semicolon
   *                      [actuator_count_1] underscore [actuator_type_1] space ... space
   *                      [actuator_count_M] underscore [actuator_type_M]
   */
    public void askForNodes() {
        // String nodes = sendCommandToServer("GREENHOUSE;ALL;GET_NODE_ID");
        String nodes = "1";
        for (String node : nodes.split(";")) {
          int nodeId;
            try{
              nodeId = parseIntegerOrError(node, "Invalid node ID: " + node);
            }
            catch (NumberFormatException e) {
              System.err.println("Could not parse node ID: " + e.getMessage());
              continue;
            }
            this.spawnNode(node, 5);
        }
        // SensorActuatorNodeInfo nodeInfo = createSensorNodeInfoFrom(specification);
        // System.out.println("Spawning node " + specification);
        // logic.onNodeAdded(nodeInfo);
    }

    public void spawnNode(String nodeId, int START_DELAY) {
        String specification = sendCommandToServer("GREENHOUSE;" + nodeId + ";GET_NODE");
        Logger.info("Received node specification: " + specification);
        SensorActuatorNodeInfo nodeInfo = this.createSensorNodeInfoFrom(specification);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
          @Override
          public void run() {
            System.out.println("Spawning node " + specification);
            logic.onNodeAdded(nodeInfo);
          }
        }, START_DELAY * 1000L);
        // logic.onNodeAdded(nodeInfo);
        System.out.println("Spawning node " + specification);
    }

    private SensorActuatorNodeInfo createSensorNodeInfoFrom(String specification) {
        System.out.println("specification: " + specification);
        if (specification == null || specification.isEmpty()) {
          throw new IllegalArgumentException("Node specification can't be empty");
        }
        String[] parts = specification.split(";");
        if (parts.length > 3) {
          throw new IllegalArgumentException("Incorrect specification format");
        }
        int nodeId = parseIntegerOrError(parts[0], "Invalid node ID:" + parts[0]);
        SensorActuatorNodeInfo info = new SensorActuatorNodeInfo(nodeId);
        if (parts.length == 2) {
          parseActuators(parts[1], info);
        }
        return info;
      }

  private void parseActuators(String actuatorSpecification, SensorActuatorNodeInfo info) {
    String[] parts = actuatorSpecification.split(" ");
    for (String part : parts) {
      parseActuatorInfo(part, info);
    }
  }

  private void parseActuatorInfo(String s, SensorActuatorNodeInfo info) {
    String[] actuatorInfo = s.split("_");
    if (actuatorInfo.length != 2) {
      throw new IllegalArgumentException("Invalid actuator info format: " + s);
    }
    int actuatorCount = parseIntegerOrError(actuatorInfo[0],
        "Invalid actuator count: " + actuatorInfo[0]);
    String actuatorType = actuatorInfo[1];
    for (int i = 0; i < actuatorCount; ++i) {
      Actuator actuator = new Actuator(actuatorType, info.getId());
      actuator.setListener(logic);
      info.addActuator(actuator);
    }
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
    return new SensorReading(sensorType, value, unit);
  }

  /**
   * Advertise that an actuator has changed it's state.
   *
   * @param nodeId     ID of the node to which the actuator is attached
   * @param actuatorId ID of the actuator.
   * @param on         When true, actuator is on; off when false.
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
