package no.ntnu.gui.common;

import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
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
 * them to turn actuators on or off and observe their current state.
 */
public class ActuatorPane extends TitledPane {
  // Maps to hold the GUI state for each actuator.
  private final Map<Actuator, SimpleStringProperty> actuatorValue = new HashMap<>();
  private final Map<Actuator, SimpleBooleanProperty> actuatorActive = new HashMap<>();

  /**
   * Creates an `ActuatorPane` to display the given collection of actuators.
   *
   * @param actuators The collection of actuators to display in the pane.
   */
  public ActuatorPane(ActuatorCollection actuators) {
    super();
    setText("Actuators"); // Sets the title of the pane.
    VBox vbox = new VBox();
    vbox.setSpacing(10); // Adds spacing between the child elements.
    setContent(vbox); // Adds the VBox to the pane's content.
    addActuatorControls(actuators, vbox);
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
        actuator.turnOn(); // Turns the actuator on if the checkbox is selected.
      } else {
        actuator.turnOff(); // Turns the actuator off otherwise.
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
   * Generates the text representation of an actuator's type and state.
   *
   * @param actuator The actuator whose text representation is generated.
   * @return A string showing the actuator's type and whether it is on or off.
   */
  private String generateActuatorText(Actuator actuator) {
    String onOff = actuator.isOn() ? "ON" : "off";
    return actuator.getType() + ": " + onOff;
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