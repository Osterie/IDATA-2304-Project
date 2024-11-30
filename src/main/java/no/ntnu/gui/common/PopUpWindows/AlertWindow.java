package no.ntnu.gui.common.PopUpWindows;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

/**
 * An abstract utility class for displaying alert messages to the user in a modal window.
 * This class contains the common logic for displaying alert windows, and subclasses should define the alert type.
 */
public abstract class AlertWindow {

  /**
   * Displays an alert window with the given message.
   *
   * @param title   The title of the alert window.
   * @param message The message to display in the alert window.
   */
  public void showAlert(String title, String message) {
    Platform.runLater(() -> {
      // Create the alert using the specific alert type defined by the subclass
      Alert alert = new Alert(getAlertType(), message, ButtonType.OK);
      alert.setTitle(title);
      alert.setHeaderText(getHeaderText());
      alert.initModality(Modality.APPLICATION_MODAL);
      alert.initStyle(StageStyle.UTILITY);

      // Set detailed text in case of long messages
      TextArea textArea = new TextArea(message);
      textArea.setEditable(false);
      textArea.setWrapText(true);
      textArea.setMaxWidth(Double.MAX_VALUE);
      textArea.setMaxHeight(Double.MAX_VALUE);

      VBox dialogPaneContent = new VBox();
      dialogPaneContent.getChildren().add(textArea);

      alert.getDialogPane().setContent(dialogPaneContent);

      alert.showAndWait();
    });
  }

  /**
   * Abstract method to get the alert type (to be implemented by subclasses).
   *
   * @return The alert type (ERROR or INFORMATION).
   */
  protected abstract AlertType getAlertType();

  /**
   * Abstract method to get the header text for the alert (to be implemented by subclasses).
   *
   * @return The header text for the alert.
   */
  protected abstract String getHeaderText();
}