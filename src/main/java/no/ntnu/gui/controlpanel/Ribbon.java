package no.ntnu.gui.controlpanel;

import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;

/**
 * Factory class for creating the ribbon in the control panel.
 * * The ribbon contains menu options for the control panel.
 */
public class Ribbon {

  /**
   * Creates the ribbon containing the menu bar with application options.
   *
   * @param refreshAction  The action to perform when the refresh option is clicked.
   * @return A VBox containing the ribbon components.
   */
  public static VBox createRibbon(Runnable refreshAction) {
    MenuBar ribbonMenuBar = new MenuBar();

    // Create the Options menu
    Menu fileMenu = new Menu("Options");

    // Create the Refresh menu item
    MenuItem refreshItem = new MenuItem("Refresh");
    refreshItem.setOnAction(event -> {
      if (refreshAction != null) {
        refreshAction.run();
      }
    });

    // Create the Exit menu item
    MenuItem exitItem = new MenuItem("Exit");
    exitItem.setOnAction(event -> Platform.exit());

    //Adds the items to the menu bar
    fileMenu.getItems().addAll(refreshItem, exitItem);
    ribbonMenuBar.getMenus().add(fileMenu);

    return new VBox(ribbonMenuBar);
  }
}
