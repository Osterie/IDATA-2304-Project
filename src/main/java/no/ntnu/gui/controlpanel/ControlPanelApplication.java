package no.ntnu.gui.controlpanel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import javafx.geometry.Pos;
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

/**
 * JavaFX application for the control panel of a greenhouse system.
 * Provides a graphical interface to interact with sensor/actuator nodes, view data, and manage their states.
 *
 * Key features include:
 * - Adding and removing tabs dynamically based on connected nodes.
 * - Viewing sensor data and controlling actuators.
 * - Refreshing the application and handling communication channel states.
 */
public class ControlPanelApplication extends Application
        implements GreenhouseEventListener, CommunicationChannelListener {

  // Static references for shared logic and communication channel
  private static ControlPanelLogic logic;
  private static final int WINDOW_WIDTH = 500;
  private static final int WINDOW_HEIGHT = 420;
  private static ControlPanelCommunicationChannel channel;

  // TabPane to manage node tabs
  private TabPane nodeTabPane;

  // Main application scene
  private Scene mainScene;

  // Maps to store GUI components and node-related information
  private final Map<Integer, SensorPane> sensorPanes = new HashMap<>();
  private final Map<Integer, ActuatorPane> actuatorPanes = new HashMap<>();
  private final Map<Integer, SensorActuatorNodeInfo> nodeInfos = new HashMap<>();
  private final Map<Integer, Tab> nodeTabs = new HashMap<>();

  // Global ScrollPane for dynamic resizing of the application layout
  private ScrollPane scrollPane;

  /**
   * Starts the control panel application with the specified logic and communication channel.
   *
   * @param logic   The control panel logic to handle core operations.
   * @param channel The communication channel to interact with nodes.
   * @throws IllegalArgumentException if logic or channel is null.
   */
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

  /**
   * Initializes the main JavaFX application stage.
   *
   * @param stage The primary stage for this JavaFX application.
   */
  @Override
  public void start(Stage stage) {
    if (channel == null) {
      throw new IllegalStateException(
              "No communication channel. See the README on how to use fake event spawner!");
    }

    stage.setMinWidth(WINDOW_WIDTH);
    stage.setMinHeight(WINDOW_HEIGHT);
    stage.setTitle("Control panel");

    VBox rootLayout = new VBox();
    Node ribbon = createRibbon();
    rootLayout.getChildren().add(ribbon);
    rootLayout.getChildren().add(createEmptyContent());

    // Wrap the root layout in a ScrollPane
    scrollPane = new ScrollPane();
    scrollPane.setContent(rootLayout);
    scrollPane.setFitToWidth(true);

    // Dynamically adjust ScrollPane height based on content and window size
    rootLayout.heightProperty().addListener((obs, oldHeight, newHeight) -> {
      double newHeightValue = Math.min(newHeight.doubleValue(), stage.getHeight() - 50);
      scrollPane.setPrefHeight(newHeightValue);
    });
    
    mainScene = new Scene(scrollPane, WINDOW_WIDTH, WINDOW_HEIGHT);
    stage.setScene(mainScene);
    stage.show();

    logic.addListener(this);
    logic.setCommunicationChannelListener(this);
    if (!channel.isConnected()) {
      logic.onCommunicationChannelClosed();
    }
  }

  /**
   * Creates the ribbon containing the menu bar with application options.
   *
   * @return A VBox containing the ribbon components.
   */
  private VBox createRibbon() {
    // Create a menu bar for the ribbon
    MenuBar ribbonMenuBar = new MenuBar();

    // Options
    Menu fileMenu = new Menu("Options");

    // Option items
    MenuItem exitItem = new MenuItem("Exit");
    MenuItem refreshItem = new MenuItem("Refresh");
    refreshItem.setOnAction(event -> refreshControlPanel());
    MenuItem settingsItem = new MenuItem("Settings");
    settingsItem.setOnAction(event -> Logger.info("Settings clicked"));
    exitItem.setOnAction(event -> Platform.exit());

    fileMenu.getItems().add(refreshItem);
    fileMenu.getItems().add(settingsItem);
    fileMenu.getItems().add(exitItem);

    // Add menus to the ribbon
    ribbonMenuBar.getMenus().addAll(fileMenu);

    // Return the ribbon container
    return new VBox(ribbonMenuBar);
  }

  /**
   * Refreshes the control panel by restarting the application.
   */
  private void refreshControlPanel() {
    Logger.info("Refreshing Control Panel...");

    Platform.runLater(() -> {
      // Close the current stage
      Stage currentStage = (Stage) mainScene.getWindow();
      currentStage.close();

      // Create a new stage and reopen the control panel
      try {
        start(new Stage());
      } catch (Exception e) {
        Logger.error("Error reopening the control panel: " + e.getMessage());
        e.printStackTrace();
      }
    });
  }

  /**
   * Creates a placeholder label indicating that the application is waiting for node data.
   *
   * @return A Label with the placeholder text.
   */
  private static Label createEmptyContent() {
    Label l = new Label("Waiting for node data...");
    l.setAlignment(Pos.CENTER);
    return l;
  }

  /**
   * Callback for when a new node is added.
   * Dynamically adds a new tab for the node.
   *
   * @param nodeInfo Information about the added node.
   */
  @Override
  public void onNodeAdded(SensorActuatorNodeInfo nodeInfo) {
    Platform.runLater(() -> addNodeTab(nodeInfo));
  }

  /**
   * Callback for when a node is removed.
   * Removes the corresponding tab and clears associated data.
   *
   * @param nodeId The ID of the removed node.
   */
  @Override
  public void onNodeRemoved(int nodeId) {
    Tab nodeTab = nodeTabs.get(nodeId);
    if (nodeTab != null) {
      Platform.runLater(() -> {
        removeNodeTab(nodeId, nodeTab);
        forgetNodeInfo(nodeId);
        if (nodeInfos.isEmpty()) {
          removeNodeTabPane();
        }
      });
      Logger.info("Node " + nodeId + " removed");
    } else {
      Logger.error("Can't remove node " + nodeId + ", there is no Tab for it");
    }
  }

  /**
   * Removes the TabPane if there are no more nodes.
   */
  private void removeNodeTabPane() {
    VBox rootLayout = (VBox) scrollPane.getContent();
    rootLayout.getChildren().set(1, createEmptyContent());
    nodeTabPane = null;
  }

  /**
   * Updates sensor data for the specified node.
   *
   * @param nodeId  The ID of the node.
   * @param sensors A list of sensor readings for the node.
   */
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

  /**
   * Updates the state of an actuator for the specified node.
   *
   * @param nodeId     The ID of the node.
   * @param actuatorId The ID of the actuator.
   * @param isOn       The new state of the actuator.
   */
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
        actuatorPane.refreshActuatorDisplay();;
      } else {
        Logger.error("Actuator not found");
      }
    } else {
      Logger.error("No actuator section for node " + nodeId);
    }
  }

  /**
   * Retrieves a stored actuator for the specified node and actuator ID.
   *
   * @param nodeId     The ID of the node.
   * @param actuatorId The ID of the actuator.
   * @return The actuator, or null if not found.
   */
  private Actuator getStoredActuator(int nodeId, int actuatorId) {
    SensorActuatorNodeInfo nodeInfo = nodeInfos.get(nodeId);
    return nodeInfo != null ? nodeInfo.getActuator(actuatorId) : null;
  }

  /**
   * Clears stored data for a removed node.
   *
   * @param nodeId The ID of the removed node.
   */
  private void forgetNodeInfo(int nodeId) {
    sensorPanes.remove(nodeId);
    actuatorPanes.remove(nodeId);
    nodeInfos.remove(nodeId);
  }

  /**
   * Removes a tab for a specified node.
   *
   * @param nodeId  The ID of the node.
   * @param nodeTab The tab associated with the node.
   */
  private void removeNodeTab(int nodeId, Tab nodeTab) {
    nodeTab.getTabPane().getTabs().remove(nodeTab);
    nodeTabs.remove(nodeId);
  }

  /**
   * Adds a new tab for a specified node.
   *
   * @param nodeInfo Information about the node to add.
   */
  private void addNodeTab(SensorActuatorNodeInfo nodeInfo) {
    if (nodeTabPane == null) {
      nodeTabPane = new TabPane();
      nodeTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
      VBox rootLayout = (VBox) scrollPane.getContent();
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

  /**
   * Creates a tab for a specified node.
   *
   * @param nodeInfo Information about the node.
   * @return A Tab representing the node.
   */
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

  /**
   * Creates an empty SensorPane placeholder.
   *
   * @return An empty SensorPane instance.
   */
  private static SensorPane createEmptySensorPane() {
    return new SensorPane();
  }

  /**
   * Callback for when the communication channel is closed.
   * Exits the application.
   */
  @Override
  public void onCommunicationChannelClosed() {
    // TODO @TobyJavascript HER KAN DU VISE EN FEILMEDLING OM AT NOE HAR SKJEDD OG DET IKKE LENGER ER KOBLING MELLOM KONTROLLPANEL OG SERVER
    // TODO DU KAN VISE EN FEILMELDING ELLER NOE OG SI AT BRUKER KAN RELOADE ELLER NOE SÅNT.


    // Logger.info("Communication closed, closing the GUI");
    // Platform.runLater(Platform::exit);
  }
}