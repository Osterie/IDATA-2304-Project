package no.ntnu.gui.common;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import no.ntnu.greenhouse.actuator.Actuator;


//FACTORY IS TO EASALY EXTEND THE CODE FURTHER

public class ActuatorComponentFactory {


    // Example for further extension:
    // public static Node createComponent(ActuatorType actuator) {
    //     if (actuator instanceof actuator1 actuator1) {
    //         return createActuator1Component(actuator);
    //     } else if (actuator instanceof actuator2 actuator1) {
    //         return createActuator1Component(Actuator);
    //     }

    public static Node createActuatorComponent(Actuator actuator) {
        HBox box = new HBox();
        box.setSpacing(5);

        // Label
        Label label = new Label(generateActuatorText(actuator));

        // Checkbox
        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(actuator.isOn());
        checkBox.selectedProperty().addListener((observer, oldValue, newValue) -> {
            if (newValue) actuator.turnOn(true);
            else actuator.turnOff(true);
        });
        
        box.getChildren().addAll(label, checkBox);
        return box;
    }

    /**
   * Generates the text representation of an actuator's state.
   *
   * @param actuator The actuator whose text representation is generated.
   * @return A string showing the actuator's type and its specific state.
   */
  private static String generateActuatorText(Actuator actuator) {
    String state;
    if (actuator.isOn()) {
      state = actuator.getTurnOnText();
    } else {
      state = actuator.getTurnOffText();
    }
    return actuator.getType() + ": " + state;
  }
}
