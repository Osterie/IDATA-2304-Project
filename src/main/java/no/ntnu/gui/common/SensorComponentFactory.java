package no.ntnu.gui.common;

import javafx.scene.Node;
import javafx.scene.control.Label;
import no.ntnu.greenhouse.sensors.AudioSensorReading;
import no.ntnu.greenhouse.sensors.ImageSensorReading;
import no.ntnu.greenhouse.sensors.NumericSensorReading;
import no.ntnu.greenhouse.sensors.SensorReading;

public class SensorComponentFactory {

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

    private static Node createNumericComponent(NumericSensorReading sensor) {
        Label label = new Label(sensor.getFormatted());
        label.getStyleClass().add("numeric-sensor-label");
        return label;
    }

    private static Node createImageComponent(ImageSensorReading sensor) {
        ImageSensorPane imagePane = new ImageSensorPane(sensor);
        return imagePane.createContent();
    }

    private static Node createAudioComponent(AudioSensorReading sensor) {
        AudioSensorPane audioPane = new AudioSensorPane(sensor);
        return audioPane.createContent();
    }
}
