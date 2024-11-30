package no.ntnu.gui.controlpanel;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import no.ntnu.controlpanel.ControlPanelCommunicationChannel;
import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.controlpanel.SensorActuatorNodeInfo;
import no.ntnu.greenhouse.actuator.Actuator;
import no.ntnu.greenhouse.sensor.SensorReading;
import no.ntnu.gui.common.ActuatorPane;
import no.ntnu.gui.common.SensorPane;
import no.ntnu.listeners.common.CommunicationChannelListener;
import no.ntnu.listeners.controlpanel.GreenhouseEventListener;
import no.ntnu.tools.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JavaFX application for the control panel of a greenhouse system.
 * Provides a graphical interface to interact with sensor/actuator nodes, view data, and manage their states.
 */
public class ControlPanelApplication extends Application
        implements GreenhouseEventListener, CommunicationChannelListener {

    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 420;

    private static ControlPanelLogic logic;
    private static ControlPanelCommunicationChannel channel;

    private Scene mainScene;

    private final Map<Integer, SensorPane> sensorPanes = new HashMap<>();
    private final Map<Integer, ActuatorPane> actuatorPanes = new HashMap<>();
    private final Map<Integer, SensorActuatorNodeInfo> nodeInfos = new HashMap<>();
    private final Map<Integer, Tab> nodeTabs = new HashMap<>();
    private TabPane nodeTabPane;

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
        stage.setTitle("Control Panel");

        VBox rootLayout = new VBox();

        // Creates the ribbon using the RibbonFactory
        Node ribbon = RibbonFactory.createRibbon(this::refreshControlPanel, () -> Logger.info("Settings clicked"));
        rootLayout.getChildren().add(ribbon);

        rootLayout.getChildren().add(createEmptyContent());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(rootLayout);
        scrollPane.setFitToWidth(true);

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
     * Refreshes the control panel by restarting the application.
     */
    private void refreshControlPanel() {
        Logger.info("Refreshing Control Panel...");

        Platform.runLater(() -> {
            Stage currentStage = (Stage) mainScene.getWindow();
            currentStage.close();

            try {
                start(new Stage());
            } catch (Exception e) {
                Logger.error("Error reopening the control panel: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private static Label createEmptyContent() {
        Label label = new Label("Waiting for node data...");
        label.setAlignment(javafx.geometry.Pos.CENTER);
        return label;
    }

    /**
     * Dynamically adds a new tab for the node.
     *
     * @param nodeInfo Information about the added node.
     */
    @Override
    public void onNodeAdded(SensorActuatorNodeInfo nodeInfo) {
        Platform.runLater(() -> {
            if (nodeTabPane == null) {
                nodeTabPane = new TabPane();
                nodeTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
                VBox rootLayout = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
                rootLayout.getChildren().set(1, nodeTabPane);
            }

            if (!nodeTabs.containsKey(nodeInfo.getId())) {
                Tab tab = createNodeTab(nodeInfo);
                nodeTabPane.getTabs().add(tab);
                nodeTabs.put(nodeInfo.getId(), tab);
            }
        });
    }

    /**
     * Removes the corresponding tab and clears associated data.
     *
     * @param nodeId The ID of the removed node.
     */
    @Override
    public void onNodeRemoved(int nodeId) {
        Platform.runLater(() -> {
            Tab tab = nodeTabs.remove(nodeId);
            if (tab != null) {
                nodeTabPane.getTabs().remove(tab);
                forgetNodeInfo(nodeId);
                if (nodeTabs.isEmpty()) {
                    VBox rootLayout = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
                    rootLayout.getChildren().set(1, createEmptyContent());
                    nodeTabPane = null;
                }
            }
        });
    }

    /**
     * Updates sensor data for the specified node.
     *
     * @param nodeId  The ID of the node.
     * @param sensors A list of sensor readings for the node.
     */
    @Override
    public void onSensorData(int nodeId, List<SensorReading> sensors) {
        Platform.runLater(() -> {
            SensorPane sensorPane = sensorPanes.get(nodeId);
            if (sensorPane != null) {
                sensorPane.update(sensors);
            }
        });
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
        Platform.runLater(() -> {
            ActuatorPane actuatorPane = actuatorPanes.get(nodeId);
            if (actuatorPane != null) {
                Actuator actuator = nodeInfos.get(nodeId).getActuator(actuatorId);
                if (actuator != null) {
                    actuator.set(isOn, false);
                    actuatorPane.refreshActuatorDisplay();
                }
            }
        });
    }

    @Override
    public void onCommunicationChannelClosed() {
        Logger.error("Communication channel closed. Exiting...");
        Platform.runLater(Platform::exit);
    }

    private Tab createNodeTab(SensorActuatorNodeInfo nodeInfo) {
        SensorPane sensorPane = new SensorPane(); // Placeholder since we don't have sensor access
        ActuatorPane actuatorPane = new ActuatorPane(nodeInfo.getActuators());

        sensorPanes.put(nodeInfo.getId(), sensorPane);
        actuatorPanes.put(nodeInfo.getId(), actuatorPane);
        nodeInfos.put(nodeInfo.getId(), nodeInfo);

        VBox content = new VBox(sensorPane, actuatorPane);
        return new Tab("Node " + nodeInfo.getId(), content);
    }

    private void forgetNodeInfo(int nodeId) {
        sensorPanes.remove(nodeId);
        actuatorPanes.remove(nodeId);
        nodeInfos.remove(nodeId);
    }
}
