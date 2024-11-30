package no.ntnu.gui.common;

import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.ActuatorCollection;

/**
 * Represents a GUI component displaying a list of actuators and their states.
 * The `ActuatorPane` allows users to view and interact with actuators, enabling
 * them to turn actuators on or off and observe their current state. Additionally,
 * a "Turn Off All Actuators" button allows users to turn off all actuators within
 * the same node.
 */
public class ActuatorPane extends TitledPane {
  // Maps to hold the GUI state for each actuator.
  private final Map<Actuator, SimpleStringProperty> actuatorValue = new HashMap<>();
  private final Map<Actuator, SimpleBooleanProperty> actuatorActive = new HashMap<>();
  private final ActuatorCollection actuators;

  /**
   * Creates an `ActuatorPane` to display the given collection of actuators.
   *
   * @param actuators The collection of actuators to display in the pane.
   */
  public ActuatorPane(ActuatorCollection actuators) {
    super();
    this.actuators = actuators;
    setText("Actuators"); // Sets the title of the pane.
    VBox vbox = new VBox();
    vbox.setSpacing(10); // Adds spacing between the child elements.
    setContent(vbox); // Adds the VBox to the pane's content.
    addActuatorControls(actuators, vbox);
    addTurnOffAllButton(vbox); // Add the "Turn Off All Actuators" button.
    GuiTools.stretchVertically(this); // Ensures the pane stretches vertically.
  }

  /**
   * Adds controls for each actuator in the collection to the provided parent container.
   *
   * @param actuators A collection of actuators to display.
   * @param parent    The parent container where the controls will be added.
   */
  private void addActuatorControls(ActuatorCollection actuators, Pane parent) {
    actuators.forEach(actuator ->
            parent.getChildren().add(createActuatorGui(actuator))
    );
  }

  /**
   * Creates the GUI representation for a single actuator.
   *
   * @param actuator The actuator for which to create a GUI.
   * @return A Node representing the GUI of the actuator.
   */
  private Node createActuatorGui(Actuator actuator) {
    HBox actuatorGui = new HBox(createActuatorLabel(actuator), createActuatorCheckbox(actuator));
    actuatorGui.setSpacing(5); // Adds spacing between the label and checkbox.
    return actuatorGui;
  }

  /**
   * Creates a checkbox linked to the given actuator, allowing the user to toggle its state.
   *
   * @param actuator The actuator to control with the checkbox.
   * @return A CheckBox bound to the actuator's state.
   */
  private CheckBox createActuatorCheckbox(Actuator actuator) {
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

  /**
   * Creates a label for the given actuator, showing its type and current state.
   *
   * @param actuator The actuator for which to create the label.
   * @return A Label bound to the actuator's state text.
   */
  private Label createActuatorLabel(Actuator actuator) {
    SimpleStringProperty props = new SimpleStringProperty(generateActuatorText(actuator));
    actuatorValue.put(actuator, props);
    Label label = new Label();
    label.textProperty().bind(props);
    return label;
  }

  /**
   * Generates the text representation of an actuator's state.
   *
   * @param actuator The actuator whose text representation is generated.
   * @return A string showing the actuator's type and its specific state.
   */
  private String generateActuatorText(Actuator actuator) {
    String state;
    if (actuator.isOn()) {
      state = actuator.getTurnOnText();
    } else {
      state = actuator.getTurnOffText();
    }
    return actuator.getType() + ": " + state;
  }

  /**
   * Adds a "Turn Off All Actuators" button to the pane.
   *
   * @param parent The parent container where the button will be added.
   */
  private void addTurnOffAllButton(Pane parent) {
    Button turnOffAllButton = new Button("Turn Off All Actuators");
    turnOffAllButton.setOnAction(event -> turnOffAllActuators());
    parent.getChildren().add(turnOffAllButton);
  }

  /**
   * Turns off all actuators in the collection.
   */
  private void turnOffAllActuators() {
    actuators.forEach(actuator -> {
      actuator.turnOff(true);
      update(actuator); // Update the actuator display
    });
  }

  /**
   * Updates the GUI to reflect the current state of the given actuator.
   *
   * @param actuator The actuator whose state has changed.
   * @throws IllegalStateException if the actuator is not managed by this pane.
   */
  public void update(Actuator actuator) {
    SimpleStringProperty actuatorText = actuatorValue.get(actuator);
    SimpleBooleanProperty actuatorSelected = actuatorActive.get(actuator);
    if (actuatorText == null || actuatorSelected == null) {
      throw new IllegalStateException("Can't update GUI for an unknown actuator: " + actuator);
    }

    Platform.runLater(() -> {
      actuatorText.set(generateActuatorText(actuator)); // Updates the label text.
      actuatorSelected.set(actuator.isOn()); // Updates the checkbox state.
    });
  }
}
