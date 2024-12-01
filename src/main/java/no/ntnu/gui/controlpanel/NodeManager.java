package no.ntnu.gui.controlpanel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import no.ntnu.controlpanel.ControlPanelCommunicationChannel;
import no.ntnu.controlpanel.SensorActuatorNodeInfo;
import no.ntnu.greenhouse.actuator.Actuator;
import no.ntnu.greenhouse.sensor.SensorReading;
import no.ntnu.gui.common.ActuatorPane;
import no.ntnu.gui.common.SensorPane;
import no.ntnu.tools.Logger;

/**
 * Manages the nodes in the Control Panel Application.
 */
public class NodeManager {

  private final TabPane nodeTabPane;
  private final Runnable onAllNodesRemoved;
  private final Runnable onFirstNodeAdded;
  private final ControlPanelCommunicationChannel channel;

  private final Map<Integer, SensorPane> sensorPanes = new HashMap<>();
  private final Map<Integer, ActuatorPane> actuatorPanes = new HashMap<>();
  private final Map<Integer, SensorActuatorNodeInfo> nodeInfos = new HashMap<>();
  private final Map<Integer, Tab> nodeTabs = new HashMap<>();

  /**
   * Constructor for NodeManager.
   *
   * @param nodeTabPane       The TabPane to manage node-related tabs.
   * @param onAllNodesRemoved A callback to run when all nodes are removed.
   * @param onFirstNodeAdded  A callback to run when the first node is added.
   * @param channel           The communication channel for node updates.
   */
  public NodeManager(TabPane nodeTabPane, Runnable onAllNodesRemoved, Runnable onFirstNodeAdded,
                     ControlPanelCommunicationChannel channel) {
    this.nodeTabPane = nodeTabPane;
    this.onAllNodesRemoved = onAllNodesRemoved;
    this.onFirstNodeAdded = onFirstNodeAdded;
    this.nodeTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    this.channel = channel;
  }

  /**
   * Adds a new node to the TabPane.
   *
   * @param nodeInfo Information about the node to add.
   */
  public void addNode(SensorActuatorNodeInfo nodeInfo) {
    Platform.runLater(() -> {
      if (!nodeTabs.containsKey(nodeInfo.getId())) {
        if (nodeTabs.isEmpty() && onFirstNodeAdded != null) {
          onFirstNodeAdded.run();
        }

        Tab tab = createNodeTab(nodeInfo);
        nodeTabPane.getTabs().add(tab);
        nodeTabs.put(nodeInfo.getId(), tab);
        nodeInfos.put(nodeInfo.getId(), nodeInfo);
        Logger.info("Node " + nodeInfo.getId() + " added");
      }
    });
  }

  /**
   * Removes a node from the TabPane.
   *
   * @param nodeId The ID of the node to remove.
   */
  public void removeNode(int nodeId) {
    Platform.runLater(() -> {
      Tab tab = nodeTabs.remove(nodeId);
      if (tab != null) {
        nodeTabPane.getTabs().remove(tab);
        sensorPanes.remove(nodeId);
        actuatorPanes.remove(nodeId);
        nodeInfos.remove(nodeId);
        Logger.info("Node " + nodeId + " removed");

        if (nodeTabs.isEmpty() && onAllNodesRemoved != null) {
          onAllNodesRemoved.run();
        }
      }
    });
  }

  /**
   * Updates the sensor data for a node.
   *
   * @param nodeId  The ID of the node.
   * @param sensors The sensor readings to update.
   */
  public void updateSensorData(int nodeId, List<SensorReading> sensors) {
    Platform.runLater(() -> {
      SensorPane sensorPane = sensorPanes.get(nodeId);
      if (sensorPane != null) {
        sensorPane.update(sensors);
        Logger.info("Updated sensor data for node " + nodeId);
      } else {
        Logger.error("No sensor section for node " + nodeId + ", asking for node info again");
        channel.askForNodes();
      }
    });
  }

  /**
   * Updates the state of an actuator.
   *
   * @param nodeId     The ID of the node.
   * @param actuatorId The ID of the actuator.
   * @param isOn       The new state of the actuator.
   */
  public void updateActuatorState(int nodeId, int actuatorId, boolean isOn) {
    Platform.runLater(() -> {
      ActuatorPane actuatorPane = actuatorPanes.get(nodeId);
      if (actuatorPane != null) {
        Actuator actuator = nodeInfos.get(nodeId).getActuator(actuatorId);
        if (actuator != null) {
          actuator.set(isOn, false);
          actuatorPane.refreshActuatorDisplay();
          Logger.info("Updated actuator state for node " + nodeId + ", actuator " + actuatorId);
        }
      }
    });
  }

  /**
   * Creates a tab for a specified node.
   *
   * @param nodeInfo The node information.
   * @return A Tab representing the node.
   */
  private Tab createNodeTab(SensorActuatorNodeInfo nodeInfo) {
    SensorPane sensorPane = new SensorPane();
    ActuatorPane actuatorPane = new ActuatorPane(nodeInfo.getActuators());
    sensorPanes.put(nodeInfo.getId(), sensorPane);
    actuatorPanes.put(nodeInfo.getId(), actuatorPane);

    Tab tab = new Tab("Node " + nodeInfo.getId(), new VBox(sensorPane, actuatorPane));
    tab.setOnSelectionChanged(event -> {
      if (tab.isSelected()) {
        Logger.info("Selected node " + nodeInfo.getId());
      }
    });

    return tab;
  }
}
