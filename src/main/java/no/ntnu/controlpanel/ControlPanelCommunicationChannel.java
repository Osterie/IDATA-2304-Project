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
import no.ntnu.greenhouse.SensorReading;
import no.ntnu.tools.Logger;

public class ControlPanelCommunicationChannel implements CommunicationChannel {
    private final ControlPanelLogic logic;
    private Socket socket;
    private BufferedReader socketReader;
    private PrintWriter socketWriter;
    private boolean isOn;

    public ControlPanelCommunicationChannel(ControlPanelLogic logic, String host, int port) throws IOException {
        this.logic = logic;
        start(host, port);
    }

    private void start(String host, int port) throws IOException {
        try {
            this.socket = new Socket(host, port);
            this.socket.setKeepAlive(true);
            this.socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.socketWriter = new PrintWriter(socket.getOutputStream(), true);
            this.isOn = true;
            // Send initial identifier to server
            String identifierMessage = Clients.CONTROL_PANEL.getValue() + ";0"; // TODO generate unique identifier, or let server do it?
            socketWriter.println(identifierMessage);
            Logger.info("connecting control panel 0 with identifier: " + identifierMessage);
            Logger.info("Socket connection established with " + host + ":" + port);
        } catch (IOException e) {
          Logger.error("Could not establish connection to the server: " + e.getMessage());
        }
    }


  public String sendCommandToServerSingleResponse(String command) {
    String serverResponse = "No response";
    if (isOn && socketWriter != null) {
        Logger.info("Trying to send command...");
        socketWriter.println(command);
        Logger.info("Sent command to server: " + command);
        try {
            Logger.info("Trying to read response...");
            serverResponse = socketReader.readLine();
            Logger.info("Received response from server: " + serverResponse);
        } catch (IOException e) {
            Logger.error("Error reading server response: " + e.getMessage() + " error type" + e.getClass() + " error cause" + e.getCause() + " error stack trace" + e.getStackTrace());
            e.printStackTrace();
        }
      } else {
          Logger.error("Unable to send command, socket is not connected.");
      }
      return serverResponse;
  }

  public List<String> sendCommandToServerMultipleResponses(String command) {
      List<String> serverResponses = new LinkedList<>();
      if (isOn && socketWriter != null) {
          Logger.info("Trying to send command...");
          socketWriter.println(command);
          Logger.info("Sent command to server: " + command);
  
          // Start a background thread to listen for multiple responses
          Thread responseReaderThread = new Thread(() -> {
              try {
                  String response;
                  long startTime = System.currentTimeMillis();
                  long timeout = 2000; // Timeout after 2 seconds if no response
  
                  while ((System.currentTimeMillis() - startTime) < timeout) {
                      if (socketReader.ready()) {
                          response = socketReader.readLine();
                          if (response != null && !response.isEmpty()) {
                              Logger.info("Received response from server: " + response);
                              serverResponses.add(response);
                              startTime = System.currentTimeMillis(); // Reset the timeout on each response
                          }
                      }
                  }
              } catch (IOException e) {
                  Logger.error("Error reading server response: " + e.getMessage());
                  e.printStackTrace();
              }
          });
  
          responseReaderThread.start();
          try {
              responseReaderThread.join(); // Wait for the thread to finish
          } catch (InterruptedException e) {
              Logger.error("Response reading interrupted: " + e.getMessage());
          }
  
      } else {
          Logger.error("Unable to send command, socket is not connected.");
      }
      return serverResponses;
    }

  
    @Override
    public void sendActuatorChange(int nodeId, int actuatorId, boolean isOn) {
        String command = "ACTUATOR_CHANGE:" + nodeId + "," + actuatorId + "," + (isOn ? "ON" : "OFF");
        String respone = sendCommandToServerSingleResponse(command);
    }

    @Override
    public boolean open() {
        return isOn;
    }

    @Override
    public boolean close() {

        boolean closed = false;
        
        try {
            if (socket != null) socket.close();
            if (socketReader != null) socketReader.close();
            if (socketWriter != null) socketWriter.close();
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
   * @param specification A (temporary) manual configuration of the node in the following format
   *                      [nodeId] semicolon
   *                      [actuator_count_1] underscore [actuator_type_1] space ... space
   *                      [actuator_count_M] underscore [actuator_type_M]
   */
    public void askForNodes() {
        // String nodes = sendCommandToServer("GREENHOUSE;ALL;GET_NODE_ID");
        List<String> responses = sendCommandToServerMultipleResponses("GREENHOUSE;ALL;GET_NODE_ID");

        for (String response : responses) {
            String[] parts = response.split(";");
            String node = parts[2];

            int nodeId;
            try {
                nodeId = parseIntegerOrError(node, "Invalid node ID: " + node);
            } catch (NumberFormatException e) {
                Logger.error("Could not parse node ID: " + e.getMessage());
                continue;
            }
            this.spawnNode(node, 5);
        }

        // Logger.info("Received nodes: " + nodes);
        // for (String node : nodes.split(";")) {
        //   int nodeId;
        //     try{
        //       nodeId = parseIntegerOrError(node, "Invalid node ID: " + node);
        //     }
        //     catch (NumberFormatException e) {
        //       Logger.error("Could not parse node ID: " + e.getMessage());
        //       continue;
        //     }
        //     this.spawnNode(node, 5);
        // }
    }

    public void spawnNode(String nodeId, int START_DELAY) {
        String specification = sendCommandToServerSingleResponse("GREENHOUSE;" + nodeId + ";GET_NODE");
        Logger.info("Received node specification: " + specification);
        String info = specification.split(";")[2];
        SensorActuatorNodeInfo nodeInfo = this.createSensorNodeInfoFrom(info);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
          @Override
          public void run() {
            Logger.info("Spawning node " + specification);
            logic.onNodeAdded(nodeInfo);
          }
        }, START_DELAY * 1000L);
    }

    private SensorActuatorNodeInfo createSensorNodeInfoFrom(String specification) {
        Logger.info("specification: " + specification);
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
