package no.ntnu.gui.controlpanel;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import no.ntnu.controlpanel.ControlPanelCommunicationChannel;
import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.controlpanel.SensorActuatorNodeInfo;
import no.ntnu.greenhouse.sensor.SensorReading;
import no.ntnu.listeners.common.CommunicationChannelListener;
import no.ntnu.listeners.controlpanel.GreenhouseEventListener;
import no.ntnu.tools.Logger;

import java.util.List;

/**
 * JavaFX application for the control panel of a greenhouse system.
 * Provides a graphical interface to interact with sensor/actuator nodes, view data, and manage their states.
 */
public class ControlPanelApplication extends Application
        implements GreenhouseEventListener, CommunicationChannelListener {

    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 440;

    private static ControlPanelLogic logic;
    private static ControlPanelCommunicationChannel channel;

    private Scene mainScene;
    private NodeManager nodeManager;

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

        // ScrollPane and TabPane setup
        ScrollPane scrollPane = new ScrollPane();
        TabPane tabPane = new TabPane();
        scrollPane.setContent(tabPane);
        scrollPane.setFitToWidth(true);

        // NodeManager setup
        nodeManager = new NodeManager(tabPane, () -> {
            // Add the placeholder message
            Label placeholder = createEmptyContent();
            rootLayout.getChildren().set(1, placeholder);
        }, () -> {
            // Remove the placeholder message
            rootLayout.getChildren().set(1, scrollPane);
        });

        rootLayout.getChildren().add(createEmptyContent()); // Start with the placeholder

        mainScene = new Scene(rootLayout, WINDOW_WIDTH, WINDOW_HEIGHT);
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

    /**
     * Creates a placeholder label indicating that the application is waiting for node data.
     *
     * @return A Label with the placeholder text.
     */
    private static Label createEmptyContent() {
        Label label = new Label("Waiting for node data...");
        label.setAlignment(javafx.geometry.Pos.CENTER);
        return label;
    }

    @Override
    public void onNodeAdded(SensorActuatorNodeInfo nodeInfo) {
        nodeManager.addNode(nodeInfo);
    }

    @Override
    public void onNodeRemoved(int nodeId) {
        nodeManager.removeNode(nodeId);
    }

    @Override
    public void onSensorData(int nodeId, List<SensorReading> sensors) {
        nodeManager.updateSensorData(nodeId, sensors);
    }

    @Override
    public void onActuatorStateChanged(int nodeId, int actuatorId, boolean isOn) {
        nodeManager.updateActuatorState(nodeId, actuatorId, isOn);
    }

    @Override
    public void onCommunicationChannelClosed() {
        Logger.error("Communication channel closed. Exiting...");
        Platform.runLater(Platform::exit);
    }
}
