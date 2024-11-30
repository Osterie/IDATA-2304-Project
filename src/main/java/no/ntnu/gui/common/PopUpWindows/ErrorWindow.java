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
 * A utility class for displaying error messages to the user in a modal window.
 * This class allows developers to provide a message to display in a simple, user-friendly error window.
 */
public class ErrorWindow {

  /**
   * Displays an error window with the given message.
   *
   * @param title   The title of the error window.
   * @param message The error message to display.
   */
  public static void showError(String title, String message) {
    Platform.runLater(() -> {
      // Create an Alert dialog
      Alert alert = new Alert(AlertType.ERROR, message, ButtonType.OK);
      alert.setTitle(title);
      alert.setHeaderText("An Error Occurred");
      alert.initModality(Modality.APPLICATION_MODAL);
      alert.initStyle(StageStyle.UTILITY);

      // Set detailed text in case of long error messages
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
}
