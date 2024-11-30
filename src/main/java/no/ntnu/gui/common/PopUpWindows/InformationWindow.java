package no.ntnu.gui.common.PopUpWindows;

import javafx.scene.control.Alert.AlertType;

/**
 * A utility class for displaying information messages to the user in a modal window.
 * This class extends AbstractAlertWindow and defines the behavior for information alerts.
 */
public class InformationWindow extends AlertWindow {

  @Override
  protected AlertType getAlertType() {
    return AlertType.INFORMATION; // Return the INFORMATION alert type for informational messages
  }

  @Override
  protected String getHeaderText() {
    return "Information"; // Header for information alerts
  }
}