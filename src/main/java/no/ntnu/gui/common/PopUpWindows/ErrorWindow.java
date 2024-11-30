package no.ntnu.gui.common.PopUpWindows;

import javafx.scene.control.Alert.AlertType;
import no.ntnu.gui.common.AlertWindow;

/**
 * A utility class for displaying error messages to the user in a modal window.
 * This class extends AbstractAlertWindow and defines the behavior for error alerts.
 */
public class ErrorWindow extends AlertWindow {

  @Override
  protected AlertType getAlertType() {
    return AlertType.ERROR; // Return the ERROR alert type for errors
  }

  @Override
  protected String getHeaderText() {
    return "An Error Occurred"; // Header for error alerts
  }
}