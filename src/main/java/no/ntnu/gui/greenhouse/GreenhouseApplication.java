package no.ntnu.gui.greenhouse;

import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.greenhouse.SensorActuatorNode;
import no.ntnu.gui.common.popupwindows.ErrorWindow;
import no.ntnu.listeners.greenhouse.NodeStateListener;
import no.ntnu.tools.Logger;

/**
 * JavaFX application that runs a greenhouse simulation with a graphical user
 * interface (GUI).
 * This class manages the lifecycle of the application, the GUI window,
 * and interaction with sensor/actuator nodes.
 */
public class GreenhouseApplication extends Application implements NodeStateListener, Runnable {

  // The greenhouse simulator instance
  private static GreenhouseSimulator simulator;
  // Mapping of nodes to their GUI windows
  private final Map<SensorActuatorNode, NodeGuiWindow> nodeWindows = new HashMap<>();
  // The primary stage for the JavaFX application
  private Stage mainStage;

  // Error window
  ErrorWindow errorWindow = new ErrorWindow();

  /**
   * Initializes the GUI and sets up the simulation environment.
   * This method sets up the main window for the GUI, initializes the simulator,
   * subscribes to lifecycle updates, and starts the simulator.
   *
   * @param mainStage The primary stage for the JavaFX application
   */
  @Override
  public void start(Stage mainStage) {
    this.mainStage = mainStage;

    // Setting the main content scene for the application window
    mainStage.setScene(new MainGreenhouseGuiWindow());
    mainStage.setMinWidth(MainGreenhouseGuiWindow.WIDTH); // Set minimum width
    mainStage.setMinHeight(MainGreenhouseGuiWindow.HEIGHT); // Set minimum height
    mainStage.setTitle("Greenhouse simulator");
    mainStage.show();

    Logger.info("GUI subscribes to lifecycle events");

    // Initialize simulator and subscribe to updates
    simulator.initialize();
    simulator.subscribeToLifecycleUpdates(this);

    // Set up a listener for closing the application
    mainStage.setOnCloseRequest(event -> closeApplication());

    // Start the simulator
    simulator.start();
  }

  /**
   * Starts the application with an option to emulate fake events (for testing or
   * simulation purposes).
   * This is called from the main method when launching the application.
   */
  public static void startApp() {
    Logger.info("Running greenhouse simulator with JavaFX GUI...");
    // Initialize the simulator with the option to fake events
    simulator = new GreenhouseSimulator();
    // Launch the JavaFX application
    launch();
  }

  /**
   * Closes the application, stopping the simulator and performing any necessary
   * cleanup.
   * This method is invoked when the window is closed or the user exits the
   * application.
   */
  private void closeApplication() {
    Logger.info("Closing Greenhouse application...");
    // Stop the simulator
    simulator.stop();
    try {
      // Stop the JavaFX application
      stop();
    } catch (Exception e) {
      errorWindow.showAlert("Error", "Could not stop the application: " + e.getMessage());
      Logger.error("Could not stop the application: " + e.getMessage());
    }
  }

  /**
   * Executes the simulation in a separate thread,
   * starting the application in a non-blocking manner.
   * This is used when the application is run in a threaded environment.
   */
  @Override
  public void run() {
    // Start the application without fake events
    startApp();
  }

  /**
   * Called when a sensor/actuator node is ready and has been initialized.
   * A GUI window is created for the node and displayed to the user.
   *
   * @param node The sensor/actuator node that has been initialized
   */
  @Override
  public void onNodeReady(SensorActuatorNode node) {
    Logger.info("Starting window for node " + node.getId());

    // Create and show a GUI window for the node
    NodeGuiWindow window = new NodeGuiWindow(node);
    nodeWindows.put(node, window);

    // Show the window for this node
    window.show();
  }

  /**
   * Called when a sensor/actuator node is stopped.
   * The associated GUI window is closed and removed from the list of open
   * windows.
   * If there are no more windows open, the main application window is also
   * closed.
   *
   * @param node The sensor/actuator node that has been stopped
   */
  @Override
  public void onNodeStopped(SensorActuatorNode node) {
    // Retrieve and remove the GUI window for the node
    NodeGuiWindow window = nodeWindows.remove(node);

    if (window != null) {
      // Run the closing of the window on the JavaFX application thread
      Platform.runLater(window::close);

      // If no other windows are open, close the main application window
      if (nodeWindows.isEmpty()) {
        Platform.runLater(mainStage::close);
      }
    }
  }
}