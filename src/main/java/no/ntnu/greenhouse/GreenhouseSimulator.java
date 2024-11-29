package no.ntnu.greenhouse;

import java.util.HashMap;
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
    this.createNodes();
    Logger.info("Greenhouse initialized");
  }

  private void createNodes(){
    SensorActuatorNode node1 = new DeviceBuilder().addTemperatureSensor(1)
        .addHumiditySensor(2)
        .addWindowActuator(1)
        .build();
    nodes.put(node1.getId(), node1);

    SensorActuatorNode node2 = new DeviceBuilder().addTemperatureSensor(1)
        .addFanActuator(2)
        .addHeaterActuator(1)
        .addPhSensor(1)
        .build();

    nodes.put(node2.getId(), node2);

    SensorActuatorNode node3 = new DeviceBuilder().addTemperatureSensor(2)
        .addLightSensor(2)
        .addLightActuator(1)
        .build();
    nodes.put(node3.getId(), node3);

    // SensorActuatorNode node4 = new DeviceBuilder().addAudioSensor(1)
    //     .build();
    // nodes.put(node4.getId(), node4);

    SensorActuatorNode node5 = new DeviceBuilder().addImageSensor(1)
        .build();
    nodes.put(node5.getId(), node5);
        
  }

  /**
   * Start a simulation of a greenhouse - all the sensor and actuator nodes inside it.
   */
  public void start() {
    this.initiateCommunication();
    for (SensorActuatorNode node : nodes.values()) {
      node.start();
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

  private void stopCommunication() {
  
    for (NodeConnectionHandler handler : nodeConnections.values()) {
        handler.close();
    }
    for (SensorActuatorNode node : nodes.values()) {
        node.stop();
    }
    Logger.info("Greenhouse simulator stopped.");
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
