package no.ntnu.gui.common;

import java.util.Map;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import no.ntnu.greenhouse.actuator.Actuator;

//FACTORY IS TO EASALY EXTEND THE CODE FURTHER

/**
 * The ActuatorComponentFactory class provides methods to create UI components
 * for actuators.
 * 
 * <p>
 * Methods:
 * </p>
 * <ul>
 * <li>{@link #createActuatorComponent(Actuator, Map)}: Creates a UI component
 * for an actuator.</li>
 * <li>{@link #generateActuatorText(Actuator)}: Generates a text representation
 * of the actuator's state.</li>
 * <li>{@link #createActuatorCheckbox(Actuator, Map)}: Creates a CheckBox for an
 * Actuator and binds its selected property to a SimpleBooleanProperty.</li>
 * </ul>
 */
public class ActuatorComponentFactory {

  // Example for further extension:
  // public static Node createComponent(ActuatorType actuator) {
  // if (actuator instanceof actuator1 actuator1) {
  // return createActuator1Component(actuator);
  // } else if (actuator instanceof actuator2 actuator1) {
  // return createActuator1Component(Actuator);
  // }

  /**
   * Creates a UI component for an actuator, consisting of a label and a checkbox.
   *
   * @param actuator       the actuator for which the component is created
   * @param actuatorActive a map that holds the active state of each actuator
   * @return a Node containing the label and checkbox for the actuator
   */
  public static Node createActuatorComponent(Actuator actuator, Map<Actuator, SimpleBooleanProperty> actuatorActive) {
    HBox box = new HBox();
    box.setSpacing(5);

    // Label
    Label label = new Label(generateActuatorText(actuator));

    // Checkbox
    CheckBox checkBox = createActuatorCheckbox(actuator, actuatorActive);
    box.getChildren().addAll(label, checkBox);
    return box;
  }

  /**
   * Generates a text representation of the actuator's state.
   *
   * @param actuator the actuator for which to generate the text
   * @return a string representing the actuator's type and its current state
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

  /**
   * Creates a CheckBox for an Actuator and binds its selected property to a
   * SimpleBooleanProperty.
   * The CheckBox's selected state is synchronized with the Actuator's state.
   * 
   * @param actuator       The Actuator for which the CheckBox is created.
   * @param actuatorActive A map to store the SimpleBooleanProperty associated
   *                       with the Actuator.
   * @return The created CheckBox.
   */
  private static CheckBox createActuatorCheckbox(Actuator actuator,
      Map<Actuator, SimpleBooleanProperty> actuatorActive) {
    CheckBox checkbox = new CheckBox();
    SimpleBooleanProperty isSelected = new SimpleBooleanProperty(actuator.isOn());
    actuatorActive.put(actuator, isSelected); // Store the property for later updates.
    checkbox.selectedProperty().bindBidirectional(isSelected);
    checkbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null && newValue) {
        actuator.turnOn(true); // Turns the actuator on if the checkbox is selected.
      } else {
        actuator.turnOff(true); // Turns the actuator off otherwise.
      }
    });
    return checkbox;
  }
}
