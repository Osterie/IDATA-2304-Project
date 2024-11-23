package no.ntnu.greenhouse;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import no.ntnu.intermediaryserver.server.ServerConfig;
import no.ntnu.listeners.greenhouse.NodeStateListener;
import no.ntnu.tools.Logger;

/**
 * Application entrypoint - a simulator for a greenhouse.
 */
public class GreenhouseSimulator {
  // The nodes in the greenhouse, keyed by their unique ID
  private final Map<Integer, SensorActuatorNode> nodes = new HashMap<>();

  private final List<PeriodicSwitch> periodicSwitches = new LinkedList<>(); //TODO remove me. Testing only?
  private final Map<Integer, NodeConnectionHandler> nodeConnections = new HashMap<>();  // Store connections for each node

  private final ExecutorService threadPool = Executors.newCachedThreadPool();


  /**
   * Create a new greenhouse simulator.
   */
  public GreenhouseSimulator() {
    // Empty
  }

  /**
   * Initialise the greenhouse but don't start the simulation just yet.
   */
  public void initialize() {
    createNode(1, 2, 1, 0, 0,0);
    createNode(1, 0, 0, 2, 1,0);
    createNode(2, 0, 0, 0, 0,0);
    createNode(0, 0, 0, 0, 0, 1);
    Logger.info("Greenhouse initialized");
  }

  /**
   * Create a new node in the greenhouse.
   * 
   * @param temperature number of temperature sensors
   * @param humidity number of humidity sensors
   * @param windows number of window actuators
   * @param fans number of fan actuators
   * @param heaters number of heater actuators
   * @param cameras number of camera sensors
   */
  private void createNode(int temperature, int humidity, int windows, int fans, int heaters, int cameras) {
    SensorActuatorNode node = DeviceFactory.createNode(
        temperature, humidity, windows, fans, heaters, cameras);
    nodes.put(node.getId(), node);
  }

  /**
   * Start a simulation of a greenhouse - all the sensor and actuator nodes inside it.
   */
  public void start() {
    this.initiateCommunication();
    for (SensorActuatorNode node : nodes.values()) {
      node.start();
    }
    for (PeriodicSwitch periodicSwitch : periodicSwitches) {
      periodicSwitch.start();
    }

    Logger.info("Simulator started");
  }

  /**
   * Start the remote control.
   * Able to send commands if started
   */
  public void initiateCommunication(){
    for (SensorActuatorNode node : nodes.values()) {
      this.startNodeHandler(node);
    }
  }

  private void startNodeHandler(SensorActuatorNode node) {
    NodeConnectionHandler nodeHandler = new NodeConnectionHandler(node, ServerConfig.getHost(), ServerConfig.getPortNumber());
    this.nodeConnections.put(node.getId(), nodeHandler);
    threadPool.submit(nodeHandler);
  }

  public void stop() {
    this.stopCommunication();
    for (SensorActuatorNode node : nodes.values()) {
        node.stop();
    }
    threadPool.shutdown();
}

  // TODO remove me after learning what is to learn. Method from teacher
  private String handleServerRequest(String request) {

    Logger.info("Hanlding request: " + request);
    return "OK";

    // Example request: "GET_SENSOR_DATA nodeId=1"
    // if (request.startsWith("GET_SENSOR_DATA")) {
    //     int nodeId = extractNodeIdFromRequest(request); // A method to extract the node ID
    //     SensorActuatorNode node = nodes.get(nodeId);
    //     if (node != null) {
    //         String sensorData = node.getReading();
    //         socketWriter.println(sensorData);  // Send the sensor data back to the server
    //     } else {
    //         socketWriter.println("ERROR: Node not found");
    //     }
    // } else if (request.startsWith("CONTROL_COMMAND")) {
    //     // Example: "CONTROL_COMMAND fan=on nodeId=1"
    //     processControlCommand(request);
    // }
    // You can add more command types here, such as turning on/off heaters, fans, etc.
}

// TODO remove me after learning what is to learn. Method from teacher
private int extractNodeIdFromRequest(String request) {
  return -1;
  // TODO figure out which node the request is for
    // Logic to parse the node ID from the request string
    // Example: "GET_SENSOR_DATA nodeId=1"
    // String[] parts = request.split(" ");
    // for (String part : parts) {
    //     if (part.startsWith("nodeId=")) {
    //         return Integer.parseInt(part.split("=")[1]);
    //     }
    // }
    // return -1;  // Or throw an exception if not found
}

// TODO remove me after learning what is to learn. Method from teacher
private void processControlCommand(String command) {
    // Example: "CONTROL_COMMAND fan=on nodeId=1"
    int nodeId = extractNodeIdFromRequest(command);
    SensorActuatorNode node = nodes.get(nodeId);
    if (node != null) {
      return;
      // TODO handle the command!
    //     // Logic to control actuators like fans or heaters
    //     if (command.contains("fan=on")) {
    //         node.turnFanOn();  // Assuming this method exists in SensorActuatorNode
    //         socketWriter.println("SUCCESS: Fan turned on for node " + nodeId);
    //     } else if (command.contains("fan=off")) {
    //         node.turnFanOff();  // Assuming this method exists
    //         socketWriter.println("SUCCESS: Fan turned off for node " + nodeId);
    //     }
    // } else {
    //     socketWriter.println("ERROR: Node not found");
    }
  }

  private void stopCommunication() {
  
    for (NodeConnectionHandler handler : nodeConnections.values()) {
        handler.close();
    }
    for (SensorActuatorNode node : nodes.values()) {
        node.stop();
    }
    Logger.info("Greenhouse simulator stopped.");
    // TODO - here you stop the TCP/UDP communication
  }

  /**
   * Add a listener for notification of node staring and stopping.
   *
   * @param listener The listener which will receive notifications
   */
  public void subscribeToLifecycleUpdates(NodeStateListener listener) {
    for (SensorActuatorNode node : nodes.values()) {
      node.addStateListener(listener);
    }
  }
}
