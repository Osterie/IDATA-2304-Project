package no.ntnu.gui.common;

import javafx.scene.Node;
import javafx.scene.control.Button;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.ActuatorCollection;

public class ActuatorPane extends BasePane {

    private final ActuatorCollection actuators;

    public ActuatorPane(ActuatorCollection actuators) {
        super("Actuators");
        this.actuators = actuators;
        initialize();
    }

    private void initialize() {
        for (Actuator actuator : actuators) {
            Node actuatorGui = ActuatorComponentFactory.createActuatorComponent(actuator);
            addComponent(actuatorGui);
        }
        addTurnOffAllButton();
    }


    private void addTurnOffAllButton() {
        Button turnOffAllButton = new Button("Turn Off All Actuators");
        turnOffAllButton.setOnAction(e -> turnOffAllActuators());
        addComponent(turnOffAllButton);
    }

    private void turnOffAllActuators() {
        for (Actuator actuator : actuators) {
            actuator.turnOff(); // Update actuator state
        }
        refreshActuatorDisplay();
    }

    public void refreshActuatorDisplay() {
        clearComponents(); // Clear the existing UI
        initialize(); // Re-initialize to update the UI with the latest states
    }
  }
