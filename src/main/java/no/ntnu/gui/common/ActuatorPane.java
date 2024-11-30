package no.ntnu.gui.common;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import no.ntnu.greenhouse.actuator.Actuator;
import no.ntnu.greenhouse.actuator.ActuatorCollection;

/**
 * ActuatorPane is a custom JavaFX pane that displays and manages a collection of actuators.
 * It extends the BasePane class and provides functionality to initialize the pane with
 * actuator components, add a button to turn off all actuators, and refresh the actuator display.
 *
 * <p>The class includes the following methods:
 * <ul>
 *   <li>{@link #ActuatorPane(ActuatorCollection)}: Constructor that initializes the pane with a given collection of actuators.</li>
 *   <li>{@link #initialize()}: Initializes the ActuatorPane by creating and adding GUI components for each actuator.</li>
 *   <li>{@link #addTurnOffAllButton()}: Adds a button to the pane that, when clicked, turns off all actuators.</li>
 *   <li>{@link #turnOffAllActuators()}: Turns off all actuators in the list and refreshes the actuator display.</li>
 *   <li>{@link #refreshActuatorDisplay()}: Refreshes the actuator display by clearing the existing UI components and re-initializing them.</li>
 * </ul>
 *
 * <p>Each actuator is represented by a GUI component created using the ActuatorComponentFactory.
 * The pane also maintains a map of actuators and their corresponding active states using SimpleBooleanProperty.
 */
public class ActuatorPane extends BasePane {

    private final ActuatorCollection actuators;
    private final Map<Actuator, SimpleBooleanProperty> actuatorActive = new HashMap<>();

    /**
     * A panel that displays and manages a collection of actuators.
     *
     * @param actuators The collection of actuators to be managed and displayed.
     */
    public ActuatorPane(ActuatorCollection actuators) {
        super("Actuators");
        this.actuators = actuators;
        initialize();
    }

    /**
     * Initializes the ActuatorPane by creating and adding GUI components for each actuator.
     * It iterates through the list of actuators, creates a corresponding GUI component for each actuator,
     * and adds it to the pane. Additionally, it adds a button to turn off all actuators.
     */
    private void initialize() {
        for (Actuator actuator : actuators) {
            Node actuatorGui = ActuatorComponentFactory.createActuatorComponent(actuator, actuatorActive);
            addComponent(actuatorGui);
        }
        addTurnOffAllButton();
    }


    /**
     * Adds a button to the pane that, when clicked, turns off all actuators.
     * The button is labeled "Turn Off All Actuators".
     */
    private void addTurnOffAllButton() {
        Button turnOffAllButton = new Button("Turn Off All Actuators");
        turnOffAllButton.setOnAction(e -> turnOffAllActuators());
        addComponent(turnOffAllButton);
    }

    /**
     * Turns off all actuators in the list and refreshes the actuator display.
     * This method iterates through all actuators and turns each one off.
     */
    private void turnOffAllActuators() {
        for (Actuator actuator : actuators) {
            actuator.turnOff(true);
        }
        refreshActuatorDisplay();
    }

    /**
     * Refreshes the actuator display by clearing the existing UI components
     * and re-initializing them to update the UI with the latest states.
     */
    public void refreshActuatorDisplay() {
        clearComponents(); // Clear the existing UI
        initialize(); // Re-initialize to update the UI with the latest states
    }

  }
