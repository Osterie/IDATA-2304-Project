package no.ntnu.gui.controlpanel;

import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
/**
 * Factory class for creating the ribbon in the control panel.
 */
public class RibbonFactory {

    /**
     * Creates the ribbon containing the menu bar with application options.
     *
     * @param refreshAction  The action to perform when the refresh option is clicked.
     * @param settingsAction The action to perform when the settings option is clicked.
     * @return A VBox containing the ribbon components.
     */
    public static VBox createRibbon(Runnable refreshAction, Runnable settingsAction) {
        MenuBar ribbonMenuBar = new MenuBar();

        Menu fileMenu = new Menu("Options");

        MenuItem refreshItem = new MenuItem("Refresh");
        MenuItem settingsItem = new MenuItem("Settings");
        MenuItem exitItem = new MenuItem("Exit");

        refreshItem.setOnAction(event -> {
            if (refreshAction != null) {
                refreshAction.run();
            }
        });

        settingsItem.setOnAction(event -> {
            if (settingsAction != null) {
                settingsAction.run();
            }
        });

        exitItem.setOnAction(event -> Platform.exit());

        fileMenu.getItems().addAll(refreshItem, settingsItem, exitItem);
        ribbonMenuBar.getMenus().add(fileMenu);

        return new VBox(ribbonMenuBar);
    }
}
