package no.ntnu.gui.common;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import no.ntnu.greenhouse.sensors.AudioSensorReading;
import no.ntnu.greenhouse.sensors.ImageSensorReading;
import no.ntnu.greenhouse.sensors.NumericSensorReading;
import no.ntnu.greenhouse.sensors.SensorReading;
import no.ntnu.tools.Logger;

/**
 * A utility class to create JavaFX components for various types of data.
 * This class generates components based on the type of data provided, such as text, images, or sensor readings.
 */
public class ComponentBuilder {

    /**
     * Create a JavaFX component based on the provided data.
     *
     * @param data The data for which a component needs to be created.
     *             It can be a string, InputStream, BufferedImage, or a subclass of SensorReading.
     * @return A Node that represents the appropriate UI component for the provided data.
     */
    public Node createComponent(Object data) {
        if (data instanceof String) {
            return createTextComponent((String) data);
        } else if (data instanceof InputStream) {
            return createImageComponent((InputStream) data);
        } else if (data instanceof BufferedImage) {
            return createBufferedImageComponent((BufferedImage) data);
        } else if (data instanceof SensorReading) {
            return createSensorReadingComponent((SensorReading) data);
        } else {
            Logger.warn("Unsupported data type: " + data.getClass());
            return new Label("Unsupported data type");
        }
    }

    /**
     * Create a simple text-based component.
     *
     * @param text The text to display.
     * @return A Label containing the provided text.
     */
    private Node createTextComponent(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("text-label");
        return label;
    }

    /**
     * Create an image-based component from an InputStream.
     *
     * @param imageStream The InputStream of the image to display.
     * @return An ImageView displaying the image.
     */
    private Node createImageComponent(InputStream imageStream) {
        try {
            ImageView imageView = new ImageView(new Image(imageStream));
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(200); // Set preferred width
            return imageView;
        } catch (Exception e) {
            Logger.error("Failed to load image: " + e.getMessage());
            return new Label("Invalid image data");
        }
    }

    /**
     * Create an image-based component from a BufferedImage.
     *
     * @param image The BufferedImage to display.
     * @return An ImageView displaying the image.
     */
    private Node createBufferedImageComponent(BufferedImage image) {
        try {
            ImageView imageView = new ImageView(SwingFXUtils.toFXImage(image, null));
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(200); // Set preferred width
            return imageView;
        } catch (Exception e) {
            Logger.error("Failed to create image component: " + e.getMessage());
            return new Label("Invalid image data");
        }
    }

    /**
     * Create a component for a specific sensor reading.
     * This dynamically determines the type of sensor reading and displays appropriate content.
     *
     * @param sensorReading The sensor reading for which to create a component.
     * @return A Node representing the sensor reading.
     */
    private Node createSensorReadingComponent(SensorReading sensorReading) {
        if (sensorReading instanceof NumericSensorReading numericReading) {
            return createNumericSensorComponent(numericReading);
        } else if (sensorReading instanceof ImageSensorReading imageReading) {
            return createImageSensorComponent(imageReading);
        } else if (sensorReading instanceof AudioSensorReading audioReading) {
            return createAudioSensorComponent(audioReading);
        } else {
            Logger.warn("Unsupported sensor reading type: " + sensorReading.getClass());
            return new Label("Unsupported sensor reading type");
        }
    }

    /**
     * Create a component for a numeric sensor reading.
     *
     * @param reading The numeric sensor reading.
     * @return A Label displaying the formatted reading.
     */
    private Node createNumericSensorComponent(NumericSensorReading reading) {
        return new Label(reading.getFormatted());
    }

    /**
     * Create a component for an image sensor reading.
     *
     * @param reading The image sensor reading.
     * @return A VBox containing the image thumbnail and sensor type.
     */
    private Node createImageSensorComponent(ImageSensorReading reading) {
        VBox container = new VBox();
        BufferedImage image = reading.getImage();
        if (image != null) {
            ImageView imageView = new ImageView(SwingFXUtils.toFXImage(image, null));
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(150);
            container.getChildren().add(imageView);
        }
        container.getChildren().add(new Label("Sensor: " + reading.getType()));
        container.getStyleClass().add("sensor-container");
        return container;
    }

    /**
     * Create a component for an audio sensor reading.
     *
     * @param reading The audio sensor reading.
     * @return A VBox containing a play button and sensor type.
     */
    private Node createAudioSensorComponent(AudioSensorReading reading) {
        VBox container = new VBox();
        File audioFile = reading.getAudioFile();
        if (audioFile != null) {
            container.getChildren().add(new Label("Audio Sensor: " + reading.getType()));
            container.getChildren().add(new Label("File: " + audioFile.getName()));
        } else {
            container.getChildren().add(new Label("No audio file available"));
        }
        container.getStyleClass().add("sensor-container");
        return container;
    }
}
