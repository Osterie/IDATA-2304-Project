package no.ntnu.gui.common;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

/**
 * BasePane is an abstract class that extends TitledPane and provides a base structure
 * for creating custom panes with a title and a VBox content area.
 *
 * <p>This class ensures that modifications to the content area are performed on the
 * JavaFX Application Thread, making it safe to update the UI from any thread.</p>
 *
 * <p>Subclasses should provide specific implementations and can use the provided methods
 * to add or clear components in the content area.</p>
 *
 * <p>Usage example:</p>
 * <pre>
 * {@code
 * public class CustomPane extends BasePane {
 *     public CustomPane() {
 *         super("Custom Pane Title");
 *         // Add custom components
 *         addComponent(new Label("Hello, World!"));
 *     }
 * }
 * }
 * </pre>
 */
public abstract class BasePane extends TitledPane {
  protected VBox contentBox = new VBox();

  /**
   * Constructs a BasePane with the specified title.
   *
   * @param title the title of the pane
   */
  public BasePane(String title) {
    setText(title);
    setContent(contentBox);
    contentBox.setSpacing(10);
  }

  /**
   * Adds a component to the content box.
   * This method ensures that the operation is performed on the JavaFX Application Thread.
   *
   * @param component the Node to be added to the content box
   */
  public void addComponent(Node component) {
    Platform.runLater(() -> {
      contentBox.getChildren().add(component);
    });
  }

  /**
   * Clears all components from the contentBox.
   * This method ensures that the operation is performed on the JavaFX Application Thread.
   */
  public void clearComponents() {
    Platform.runLater(() -> {
      contentBox.getChildren().clear();
    });
  }
}
