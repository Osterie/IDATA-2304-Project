// package no.ntnu.gui.common;

// import javafx.scene.Node;
// import javafx.scene.control.Label;
// import javafx.scene.image.Image;
// import javafx.scene.image.ImageView;
// import javafx.scene.layout.Pane;
// import javafx.scene.layout.VBox;
// import no.ntnu.greenhouse.sensors.SensorReading;

// import java.io.InputStream;

// /**
//  * A general-purpose component creator that generates JavaFX components based on data type.
//  */
// public class ComponentBuilder {

//     /**
//      * Creates a JavaFX component based on the input data.
//      *
//      * @param data The data used to create the component. It can be of various types (e.g., text, image, SensorReading).
//      * @return A Node that can be added to the JavaFX GUI.
//      */
//     public Node createComponent(Object data) {
//         if (data instanceof String textData) {
//             return createTextComponent(textData);
//         } else if (data instanceof InputStream imageData) {
//             return createImageComponent(imageData);
//         } else if (data instanceof SensorReading sensorReading) {
//             return createSensorReadingComponent(sensorReading);
//         } else {
//             return new Label("Unsupported data type");
//         }
//     }

//     /**
//      * Creates a text-based component.
//      *
//      * @param text The text to display.
//      * @return A Node displaying the provided text.
//      */
//     private Node createTextComponent(String text) {
//         Label label = new Label(text);
//         label.getStyleClass().add("text-label");
//         return label;
//     }

//     /**
//      * Creates an image-based component.
//      *
//      * @param imageData The input stream of the image.
//      * @return An ImageView displaying the image.
//      */
//     private Node createImageComponent(InputStream imageData) {
//         ImageView imageView = new ImageView(new Image(imageData));
//         imageView.setPreserveRatio(true);
//         imageView.setFitWidth(200); // Set preferred width
//         imageView.getStyleClass().add("image-view");
//         return imageView;
//     }

//     /**
//      * Creates a component based on sensor data.
//      *
//      * @param sensorReading The sensor reading data.
//      * @return A Node displaying sensor data.
//      */
//     private Node createSensorReadingComponent(SensorReading sensorReading) {
//         VBox container = new VBox();
//         container.getChildren().add(new Label("Sensor Type: " + sensorReading.getType()));
//         container.getChildren().add(new Label("Reading: " + sensorReading.getFormatted()));
//         container.getStyleClass().add("sensor-container");
//         return container;
//     }

//     /**
//      * A factory method that can return a Pane containing multiple components.
//      *
//      * @param components Varargs of components to add.
//      * @return A Pane containing all provided components.
//      */
//     public Pane createPaneWithComponents(Node... components) {
//         VBox container = new VBox(10); // Spacing of 10 units between elements
//         container.getChildren().addAll(components);
//         container.getStyleClass().add("component-pane");
//         return container;
//     }
// }
