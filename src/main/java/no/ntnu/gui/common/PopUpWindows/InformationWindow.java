package no.ntnu.gui.common.PopUpWindows;

import javafx.scene.control.Alert.AlertType;

/**
 * A utility class for displaying information messages to the user in a modal window.
 * This class extends AbstractAlertWindow and defines the behavior for information alerts.
 */
public class InformationWindow extends AlertWindow {

  @Override
  protected AlertType getAlertType() {
    // Return the INFORMATION alert type for informational messages
    return AlertType.INFORMATION;
  }

  @Override
  protected String getHeaderText() {
    // Header for information alerts
    return "Information";
  }
}