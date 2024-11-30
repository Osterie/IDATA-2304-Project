package no.ntnu.gui.common;

import javafx.scene.Node;
import no.ntnu.greenhouse.sensor.SensorReading;

public class SensorPane extends BasePane {

    public SensorPane(Iterable<SensorReading> sensors) {
        super("Sensors");
        initialize(sensors);
    }

    public SensorPane() {
        super("Sensors");
    }

    private void initialize(Iterable<SensorReading> sensors) {
        for (SensorReading sensor : sensors) {
            Node component = SensorComponentFactory.createComponent(sensor);
            addComponent(component);
        }
    }

    public void update(Iterable<SensorReading> sensors) {
        clearComponents();
        initialize(sensors);
    }


}
