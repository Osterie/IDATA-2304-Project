package no.ntnu.gui.common;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;


//TODO IS TITLEDPANE THE RIGHT CHOICE HERE? SHOULD IT BE SOME OTHER PANE?

public abstract class BasePane extends TitledPane {
    protected VBox contentBox = new VBox();

    public BasePane(String title) {
        setText(title);
        setContent(contentBox);
        contentBox.setSpacing(10);
    }

    public void addComponent(Node component) {
        Platform.runLater(() -> {
        contentBox.getChildren().add(component);
        });
    }

    public void clearComponents() {
        Platform.runLater(() -> {
        contentBox.getChildren().clear();
        });
    }
}
