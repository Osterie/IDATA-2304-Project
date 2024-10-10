package no.ntnu.greenhouse;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.listeners.greenhouse.NodeStateListener;
import no.ntnu.tools.Logger;

import static no.ntnu.intermediaryserver.ProxyServer.PORT_NUMBER;

import java.io.PrintWriter;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * Application entrypoint - a simulator for a greenhouse.
 */
public class GreenhouseSimulator {
  // The nodes in the greenhouse, keyed by their unique ID
  private final Map<Integer, SensorActuatorNode> nodes = new HashMap<>();

  private final List<PeriodicSwitch> periodicSwitches = new LinkedList<>(); //TODO remove me. Testing only?
  private final Map<Integer, NodeConnectionHandler> nodeConnections = new HashMap<>();  // Store connections for each node

  private final boolean fake; 

  private Socket socket;
  private PrintWriter socketWriter;
  private BufferedReader socketReader;

  /**
   * Create a greenhouse simulator.
   *
   * @param fake When true, simulate a fake periodic events instead of creating
   *             socket communication
   */
  public GreenhouseSimulator(boolean fake) {
    this.fake = fake;
  }


  public static void main(String[] args) {

    GreenhouseSimulator greenhouse1 = new GreenhouseSimulator(false);
    greenhouse1.start();

    // GreenhouseSimulator greenhouse2 = new GreenhouseSimulator(false);
    // greenhouse2.start();

    try{
      greenhouse1.sendCommandToServer("Test");
      greenhouse1.sendCommandToServer("Test");
      greenhouse1.sendCommandToServer("Test");
      // greenhouse2.sendCommandToServer("1");
      // greenhouse2.sendCommandToServer("Test2");
      greenhouse1.sendCommandToServer("Test");

      greenhouse1.stop();
      // greenhouse2.stop();
    }
    catch (IOException e) {
      System.err.println("Could not send command to server: " + e.getMessage());
    }    
  }

  /**
   * Initialise the greenhouse but don't start the simulation just yet.
   */
  public void initialize() {
    createNode(1, 2, 1, 0, 0);
    createNode(1, 0, 0, 2, 1);
    createNode(2, 0, 0, 0, 0);
    Logger.info("Greenhouse initialized");
  }

  private void createNode(int temperature, int humidity, int windows, int fans, int heaters) {
    SensorActuatorNode node = DeviceFactory.createNode(
        temperature, humidity, windows, fans, heaters);
    nodes.put(node.getId(), node);
  }

  /**
   * Start a simulation of a greenhouse - all the sensor and actuator nodes inside it.
   */
  public void start() {
    initiateCommunication();
    for (SensorActuatorNode node : nodes.values()) {
      node.start();
    }
    for (PeriodicSwitch periodicSwitch : periodicSwitches) {
      periodicSwitch.start();
    }

    Logger.info("Simulator started");
  }

  // public void start() {
  //   this.initiateCommunication();  // Set up the socket communication with the intermediary server

  //   for (SensorActuatorNode node : nodes.values()) {
  //       this.startNodeHandler(node);  // Start each node's handler in a separate thread
  //   }
  //   Logger.info("Simulator started");
  // }

  private void initiateCommunication() {
    if (fake) {
      initiateFakePeriodicSwitches();
    } else {
      initiateRealCommunication();
    }
  }

  /**
   * Start the remote control.
   * Able to send commands if started
   */
  public void initiateRealCommunication(){
    for (SensorActuatorNode node : nodes.values()) {
      try {
          NodeConnectionHandler handler = new NodeConnectionHandler(node, "localhost", PORT_NUMBER);
          nodeConnections.put(node.getId(), handler);
          new Thread(handler).start();
      } catch (IOException e) {
          System.err.println("Failed to connect node " + node.getId() + " to the server: " + e.getMessage());
      }
  }
    // try{
    //   this.socket = new Socket("localhost", PORT_NUMBER);
    //   this.socketReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    //   this.socketWriter = new PrintWriter(this.socket.getOutputStream(), true);
    //   this.socketWriter.println("GREENHOUSE");
    // }
    // catch (IOException e) {
    //   System.err.println("Could not establish connection to the server: " + e.getMessage());
    // }
  }

  /**
   * Send a command to the server.
   * 
   * @param command the command to send.
   * @throws IOException if an I/O error occurs when sending the command.
   */
  private void sendCommandToServer(String command) throws IOException {

      System.out.println("Sending command: " + command.toString());
      socketWriter.println(command);
      String serverResponse = socketReader.readLine();
      System.out.println("  >>> Response: " + serverResponse);
  }


  // private void initiateRealCommunication() {
  //   // TODO - here you can set up the TCP or UDP communication
  //   // TODO connect to the intermediary server

  //   try {
  //       this.socket = new Socket("localhost", PORT_NUMBER);
  //       this.socketReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
  //       this.socketWriter = new PrintWriter(this.socket.getOutputStream(), true);
        
  //       // Notify the server that this is a greenhouse connection
  //       socketWriter.println("GREENHOUSE");

  //       // Start sending sensor data to the intermediary server
  //       for (SensorActuatorNode node : nodes.values()) {
  //           new Thread(() -> {
  //               while (true) {
  //                   String sensorData = node.getSensorData();  // Method to get sensor data
  //                   socketWriter.println(sensorData);
  //                   try {
  //                       Thread.sleep(5000); // Send data every 5 seconds
  //                   } catch (InterruptedException e) {
  //                       e.printStackTrace();
  //                   }
  //               }
  //           }).start();
  //       }
        
  //       // Receive control commands from the server
  //       String controlCommand;
  //       while ((controlCommand = socketReader.readLine()) != null) {
  //           System.out.println("Received command: " + controlCommand);
  //           // Process the control command (e.g., turn on/off fans)
  //       }

  //   } catch (IOException e) {
  //       e.printStackTrace();
  //   }
  

  // }

  // private void initiateRealCommunication() {
  //   try {
  //     this.socket = new Socket("localhost", PORT_NUMBER);
  //     this.socketReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
  //     this.socketWriter = new PrintWriter(this.socket.getOutputStream(), true);

  //     // Notify the server that this is a greenhouse connection
  //     this.socketWriter.println("GREENHOUSE");
  //   } catch (IOException e) {
  //       e.printStackTrace();
  //   }
  // }

  private void startNodeHandler(SensorActuatorNode node) {
    NodeHandler nodeHandler = new NodeHandler(node, socketWriter, socketReader);
    new Thread(nodeHandler).start();  // Each node runs in its own thread
  }

  public void stop() {
    stopCommunication();
    for (SensorActuatorNode node : nodes.values()) {
        node.stop();
    }
}

  private void sendResponse(String response) {
    // String serializedResponse = this.messageSerializer.toString(response);
    // System.out.println(serializedResponse);
    this.socketWriter.println(response);
  } 


  private String handleServerRequest(String request) {

    System.out.println("Hanlding request: " + request);
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



  private void initiateFakePeriodicSwitches() {
    periodicSwitches.add(new PeriodicSwitch("Window DJ", nodes.get(1), 2, 20000));
    periodicSwitches.add(new PeriodicSwitch("Heater DJ", nodes.get(2), 7, 8000));
  }

  /**
   * Stop the simulation of the greenhouse - all the nodes in it.
   */
  // public void stop() {
  //   stopCommunication();
  //   for (SensorActuatorNode node : nodes.values()) {
  //     node.stop();
  //   }
  // }

  private void stopCommunication() {
    if (fake) {
      for (PeriodicSwitch periodicSwitch : periodicSwitches) {
        periodicSwitch.stop();
      }
    } else {
        for (NodeConnectionHandler handler : nodeConnections.values()) {
            handler.closeConnection();
        }
        for (SensorActuatorNode node : nodes.values()) {
            node.stop();
        }
        System.out.println("Greenhouse simulator stopped.");
        // TODO - here you stop the TCP/UDP communication
    }
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
