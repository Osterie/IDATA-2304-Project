package no.ntnu.gui.controlpanel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import no.ntnu.controlpanel.ControlPanelCommunicationChannel;
import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.controlpanel.SensorActuatorNodeInfo;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.sensors.SensorReading;
import no.ntnu.gui.common.ActuatorPane;
import no.ntnu.gui.common.SensorPane;
import no.ntnu.listeners.common.CommunicationChannelListener;
import no.ntnu.listeners.controlpanel.GreenhouseEventListener;
import no.ntnu.tools.Logger;

//TODO fix bug where actuators turn themselves off and on

public class ControlPanelApplication extends Application implements GreenhouseEventListener, CommunicationChannelListener {
  private static ControlPanelLogic logic;
  private static final int WIDTH = 500;
  private static final int HEIGHT = 420;
  private static ControlPanelCommunicationChannel channel;

  private TabPane nodeTabPane;
  private Scene mainScene;
  private final Map<Integer, SensorPane> sensorPanes = new HashMap<>();
  private final Map<Integer, ActuatorPane> actuatorPanes = new HashMap<>();
  private final Map<Integer, SensorActuatorNodeInfo> nodeInfos = new HashMap<>();
  private final Map<Integer, Tab> nodeTabs = new HashMap<>();

  private ComboBox<String> nodeSelector; // Dropdown menu for selecting nodes
  private Button turnOffAllButton; // Button for turning off all actuators

  public void startApp(ControlPanelLogic logic, ControlPanelCommunicationChannel channel) {
    if (logic == null) {
      throw new IllegalArgumentException("Control panel logic can't be null");
    }
    if (channel == null) {
      throw new IllegalArgumentException("Communication channel can't be null");
    }
    ControlPanelApplication.logic = logic;
    ControlPanelApplication.channel = channel;
    Logger.info("Running control panel GUI...");
    launch();
  }

  @Override
  public void start(Stage stage) {
    if (channel == null) {
      throw new IllegalStateException(
              "No communication channel. See the README on how to use fake event spawner!");
    }

    stage.setMinWidth(WIDTH);
    stage.setMinHeight(HEIGHT);
    stage.setTitle("Control panel");

    VBox rootLayout = new VBox();
    Node ribbon = createRibbon();
    rootLayout.getChildren().add(ribbon);

    rootLayout.getChildren().add(createEmptyContent());

    mainScene = new Scene(rootLayout, WIDTH, HEIGHT);
    stage.setScene(mainScene);
    stage.show();

    logic.addListener(this);
    logic.setCommunicationChannelListener(this);
    if (!channel.isOpen()) {
      logic.onCommunicationChannelClosed();
    }
  }

  private Node createRibbon() {
    ToolBar ribbon = new ToolBar();

    Button refreshButton = new Button("Refresh");
    refreshButton.setOnAction(event -> refreshControlPanel());
    // TODO: If settings is not needed, delete
    Button settingsButton = new Button("Settings");
    settingsButton.setOnAction(event -> Logger.info("Settings clicked"));

    //TODO make the ui look better maybe move the nodeSelector and turnOffAllButton under refresh and settings

    nodeSelector = new ComboBox<>();
    nodeSelector.setPromptText("Select Node");
    nodeSelector.setOnAction(event -> updateTurnOffButtonState());

    turnOffAllButton = new Button("Turn Off All Actuators");
    turnOffAllButton.setDisable(true); // disabled until you select a node
    turnOffAllButton.setOnAction(event -> turnOffAllActuators());

    HBox nodeControlBox = new HBox(nodeSelector, turnOffAllButton);
    nodeControlBox.setSpacing(10);

    ribbon.getItems().addAll(refreshButton, settingsButton, nodeControlBox);
    return ribbon;
  }

  private void updateTurnOffButtonState() {turnOffAllButton.setDisable(nodeSelector.getValue() == null);}

  private void turnOffAllActuators() {
    String selectedNode = nodeSelector.getValue();
    if (selectedNode != null) {
      int nodeId = Integer.parseInt(selectedNode);
      Logger.info("Turning off all actuators for node " + nodeId);

      SensorActuatorNodeInfo nodeInfo = nodeInfos.get(nodeId);
      if (nodeInfo != null) {
        nodeInfo.getActuators().forEach(actuator -> {
          actuator.turnOff();
          actuatorPanes.get(nodeId).update(actuator); // Update the actuator pane
        });
      } else {
        Logger.error("Node " + nodeId + " not found");
      }
    }
  }
  
  /**
   * Refresh the control panel application by closing and reopening it.
   */
  private void refreshControlPanel() {
    Logger.info("Refreshing Control Panel...");
    Platform.runLater(() -> {
      Stage currentStage = (Stage) mainScene.getWindow();
      currentStage.close(); // Close the current window

      // Optionally reset data or reload nodes
      resetControlPanelLogic();

      // Reopen the control panel
      start(new Stage());
    });
  }

  /**
   * Reset control panel logic for a clean state (if required).
   */
  private void resetControlPanelLogic() {
    nodeTabs.clear();
    sensorPanes.clear();
    actuatorPanes.clear();
    nodeInfos.clear();
    if (logic != null) {
      logic.resetState(); // Hypothetical method in ControlPanelLogic to reset internal state.
    }
  }

  private static Label createEmptyContent() {
    Label l = new Label("Waiting for node data...");
    l.setAlignment(Pos.CENTER);
    return l;
  }

  @Override
  public void onNodeAdded(SensorActuatorNodeInfo nodeInfo) {
    Platform.runLater(() -> {
      addNodeTab(nodeInfo);
      nodeSelector.getItems().add(String.valueOf(nodeInfo.getId()));
    });
  }

  @Override
  public void onNodeRemoved(int nodeId) {
    Tab nodeTab = nodeTabs.get(nodeId);
    if (nodeTab != null) {
      Platform.runLater(() -> {
        removeNodeTab(nodeId, nodeTab);
        forgetNodeInfo(nodeId);
        nodeSelector.getItems().remove(String.valueOf(nodeId));
        updateTurnOffButtonState();
        if (nodeInfos.isEmpty()) {
          removeNodeTabPane();
        }
      });
      Logger.info("Node " + nodeId + " removed");
    } else {
      Logger.error("Can't remove node " + nodeId + ", there is no Tab for it");
    }
  }

  private void removeNodeTabPane() {
    VBox rootLayout = (VBox) mainScene.getRoot();
    rootLayout.getChildren().set(1, createEmptyContent());
    nodeTabPane = null;
  }

  @Override
  public void onSensorData(int nodeId, List<SensorReading> sensors) {
    SensorPane sensorPane = sensorPanes.get(nodeId);
    if (sensorPane != null) {
      sensorPane.update(sensors);
    } else {
      Logger.error("No sensor section for node " + nodeId + ", asking for node info again");
      channel.askForNodes();
    }
  }

  @Override
  public void onActuatorStateChanged(int nodeId, int actuatorId, boolean isOn) {
    ActuatorPane actuatorPane = actuatorPanes.get(nodeId);
    if (actuatorPane != null) {
      Actuator actuator = getStoredActuator(nodeId, actuatorId);
      if (actuator != null) {
        if (isOn) {
          actuator.turnOn();
        } else {
          actuator.turnOff();
        }
        actuatorPane.update(actuator);
      } else {
        Logger.error("Actuator not found");
      }
    } else {
      Logger.error("No actuator section for node " + nodeId);
    }
  }

  private Actuator getStoredActuator(int nodeId, int actuatorId) {
    SensorActuatorNodeInfo nodeInfo = nodeInfos.get(nodeId);
    return nodeInfo != null ? nodeInfo.getActuator(actuatorId) : null;
  }

  private void forgetNodeInfo(int nodeId) {
    sensorPanes.remove(nodeId);
    actuatorPanes.remove(nodeId);
    nodeInfos.remove(nodeId);
  }

  private void removeNodeTab(int nodeId, Tab nodeTab) {
    nodeTab.getTabPane().getTabs().remove(nodeTab);
    nodeTabs.remove(nodeId);
  }

  private void addNodeTab(SensorActuatorNodeInfo nodeInfo) {
    if (nodeTabPane == null) {
      nodeTabPane = new TabPane();
      nodeTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
      VBox rootLayout = (VBox) mainScene.getRoot();
      rootLayout.getChildren().set(1, nodeTabPane);
    }

    Tab existingNodeTab = nodeTabs.get(nodeInfo.getId());
    if (existingNodeTab == null) {
      nodeInfos.put(nodeInfo.getId(), nodeInfo);
      nodeTabPane.getTabs().add(createNodeTab(nodeInfo));
    } else {
      Logger.info("Duplicate node spawned, ignore it");
    }
  }

  private Tab createNodeTab(SensorActuatorNodeInfo nodeInfo) {
    Tab tab = new Tab("Node " + nodeInfo.getId());
    SensorPane sensorPane = createEmptySensorPane();
    sensorPanes.put(nodeInfo.getId(), sensorPane);
    ActuatorPane actuatorPane = new ActuatorPane(nodeInfo.getActuators());
    actuatorPanes.put(nodeInfo.getId(), actuatorPane);
    tab.setContent(new VBox(sensorPane, actuatorPane));
    nodeTabs.put(nodeInfo.getId(), tab);

    if (nodeTabs.size() == 1) {
      ControlPanelApplication.channel.setSensorNodeTarget(String.valueOf(nodeInfo.getId()));
    }
    
    tab.setOnSelectionChanged(event -> {
      if (tab.isSelected()) {
        Logger.info("Selected node " + nodeInfo.getId());
        ControlPanelApplication.channel.setSensorNodeTarget(String.valueOf(nodeInfo.getId()));
      }
    });

    return tab;
  }

  private static SensorPane createEmptySensorPane() {
    return new SensorPane();
  }

  @Override
  public void onCommunicationChannelClosed() {
    Logger.info("Communication closed, closing the GUI");
    Platform.runLater(Platform::exit);
  }
}
