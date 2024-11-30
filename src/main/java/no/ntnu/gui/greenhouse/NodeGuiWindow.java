package no.ntnu.gui.greenhouse;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import no.ntnu.greenhouse.SensorActuatorNode;
import no.ntnu.greenhouse.actuator.Actuator;
import no.ntnu.greenhouse.sensor.Sensor;
import no.ntnu.gui.common.ActuatorPane;
import no.ntnu.gui.common.SensorPane;
import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.listeners.greenhouse.SensorListener;

import java.util.List;

/**
 * Represents a GUI window that provides an overview and control interface for a specific sensor/actuator node in the greenhouse system.
 * This window displays information about the sensors and actuators of the node and allows interaction with them.
 */
public class NodeGuiWindow extends Stage implements SensorListener, ActuatorListener {

  // Constants for window layout and size
  private static final double VERTICAL_OFFSET = 50;
  private static final double HORIZONTAL_OFFSET = 150;
  private static final double WINDOW_WIDTH = 300;
  private static final double WINDOW_HEIGHT = 300;

  private final SensorActuatorNode node;

  // GUI component displaying actuators
  private ActuatorPane actuatorPane;
  // GUI component displaying sensors
  private SensorPane sensorPane;

  /**
   * Constructs a new GUI window for a specific sensor/actuator node.
   *
   * @param node The node which will be represented and controlled in this window.
   */
  public NodeGuiWindow(SensorActuatorNode node) {
    this.node = node;

    // Set up the scene and apply CSS
    Scene scene = new Scene(createContent(), WINDOW_WIDTH, WINDOW_HEIGHT);
    scene.getStylesheets().add(getClass().getResource("/css/sensorNode.css").toExternalForm());
    setScene(scene);

    // Set the title of the window
    setTitle("Node " + node.getId());

    // Initialize listeners for sensor and actuator updates
    initializeListeners(node);

    // Set the window's initial position and size
    setPositionAndSize();
  }

  /**
   * Positions the window based on the node's ID and sets the minimum size.
   *
   * The window's position will vary according to the node's ID, using horizontal and vertical offsets.
   */
  private void setPositionAndSize() {
    setX((node.getId() - 1) * HORIZONTAL_OFFSET);
    setY(node.getId() * VERTICAL_OFFSET);
    setMinWidth(WINDOW_HEIGHT);
    setMinHeight(WINDOW_WIDTH);
  }

  /**
   * Initializes listeners for the node's sensor and actuator updates.
   * These listeners will update the GUI when changes occur in the node's sensors or actuators.
   *
   * @param node The node whose sensors and actuators are being listened to.
   */
  private void initializeListeners(SensorActuatorNode node) {
    // Set up a listener for the window's close event to shut down the node
    setOnCloseRequest(windowEvent -> shutDownNode());

    // Add the current window as a listener to the node's sensors and actuators
    node.addSensorListener(this);
    node.addActuatorListener(this);
  }

  /**
   * Shuts down the node when the window is closed.
   * This method stops the node and ensures it cleans up any resources.
   */
  private void shutDownNode() {
    // Stop the node's operation
    node.stop();
  }

  /**
   * Creates the main content of the window, including panels for sensors and actuators, wrapped in a ScrollPane.
   *
   * @return The root container (ScrollPane) that contains the sensor and actuator panels.
   */
  private Parent createContent() {
    // Create panels for displaying sensors and actuators
    actuatorPane = new ActuatorPane(node.getActuators());
    sensorPane = new SensorPane(node.getSensorReadings());

    // Combine sensor and actuator panels in a vertical layout
    VBox root = new VBox(sensorPane, actuatorPane);

    // Apply style class for CSS styling
    root.getStyleClass().add("root");

    // Wrap the content in a ScrollPane
    ScrollPane scrollPane = new ScrollPane();
    scrollPane.setContent(root);
    // Ensure ScrollPane adapts to the window size
    scrollPane.setFitToWidth(true);  // Allow content to fit width
    scrollPane.setFitToHeight(true); // Allow ScrollPane to manage height automatically
    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);  // Only show vertical scrollbar when needed
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // No horizontal scrollbar unless you need it

    return scrollPane;
  }

  /**
   * This method is called when the sensors of the node are updated.
   * It updates the sensor pane with the latest sensor values.
   *
   * @param sensors The updated list of sensors for this node.
   */
  @Override
  public void sensorsUpdated(List<Sensor> sensors) {
    // Update the sensor pane with the new list of sensors
    // TODO NOTE TO SELF SEB: CHANGE. 
    if (sensorPane != null) {
      for (Sensor sensor : sensors) {
        sensorPane.update(List.of(sensor.getReading()));
      }
    }
  }

  /**
   * This method is called when an actuator of the node is updated.
   * It updates the actuator pane with the new state of the actuator.
   *
   * @param nodeId The ID of the node whose actuator was updated.
   * @param actuator The updated actuator.
   */
  @Override
  public void actuatorUpdated(int nodeId, Actuator actuator) {
    // Update the actuator pane with the new state of the actuator
    if (actuatorPane != null) {
      actuatorPane.refreshActuatorDisplay();
    }
  }
}