package no.ntnu.gui.common;

import javafx.scene.Node;
import javafx.scene.control.Label;
import no.ntnu.greenhouse.sensor.AudioSensorReading;
import no.ntnu.greenhouse.sensor.ImageSensorReading;
import no.ntnu.greenhouse.sensor.NumericSensorReading;
import no.ntnu.greenhouse.sensor.SensorReading;



/**
 * Factory class for creating Node components based on different types of sensor readings.
 * This class provides methods to create Node components for numeric, image, and audio sensor readings.
 * If the sensor type is unsupported, a Label indicating the unsupported type is returned.
 */
public class SensorComponentFactory {

    /**
     * Creates a Node component based on the type of the given sensor reading.
     *
     * @param sensor the sensor reading for which to create a UI component
     * @return a Node representing the Node component for the given sensor reading
     *         - If the sensor is a NumericSensorReading, a numeric component is created.
     *         - If the sensor is an ImageSensorReading, an image component is created.
     *         - If the sensor is an AudioSensorReading, an audio component is created.
     *         - If the sensor type is unsupported, a Label indicating the unsupported type is returned.
     */
    public static Node createComponent(SensorReading sensor) {
        if (sensor instanceof NumericSensorReading numericSensor) {
            return createNumericComponent(numericSensor);
        } else if (sensor instanceof ImageSensorReading imageSensor) {
            return createImageComponent(imageSensor);
        } else if (sensor instanceof AudioSensorReading audioSensor) {
            return createAudioComponent(audioSensor);
        } else {
            return new Label("Unsupported sensor type: " + sensor.getClass().getName());
        }
    }

    /**
     * Creates a Node component for displaying a numeric sensor reading.
     *
     * @param sensor the numeric sensor reading to be displayed
     * @return a Node containing the formatted sensor reading
     */
    private static Node createNumericComponent(NumericSensorReading sensor) {
        Label label = new Label(sensor.humanReadableInfo());
        return label;
    }

    /**
     * Creates a Node component for displaying an image sensor reading.
     *
     * @param sensor the ImageSensorReading object containing the sensor data
     * @return a Node containing the content for the image sensor
     */
    private static Node createImageComponent(ImageSensorReading sensor) {
        ImageSensorPane imagePane = new ImageSensorPane(sensor);
        return imagePane.createContent(sensor);
    }

    /**
     * Creates a Node component for displaying audio sensor readings.
     *
     * @param sensor the audio sensor reading to be displayed
     * @return a Node containing the graphical representation of the audio sensor reading
     */
    private static Node createAudioComponent(AudioSensorReading sensor) {
        AudioSensorPane audioPane = new AudioSensorPane(sensor);
        return audioPane.createContent();
    }
}
