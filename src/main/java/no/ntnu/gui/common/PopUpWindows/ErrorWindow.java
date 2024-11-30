package no.ntnu.gui.common.PopUpWindows;

import javafx.scene.control.Alert.AlertType;

/**
 * A utility class for displaying error messages to the user in a modal window.
 * This class extends AbstractAlertWindow and defines the behavior for error alerts.
 */
public class ErrorWindow extends AlertWindow {

  @Override
  protected AlertType getAlertType() {
    // Return the ERROR alert type for errors
    return AlertType.ERROR;
  }

  @Override
  protected String getHeaderText() {
    // Header for error alerts
    return "An Error Occurred";
  }
}