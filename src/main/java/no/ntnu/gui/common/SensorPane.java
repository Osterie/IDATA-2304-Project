package no.ntnu.gui.common;

import javafx.scene.Node;
import no.ntnu.greenhouse.sensor.SensorReading;

/**
 * The SensorPane class is a UI component that displays a collection of sensor readings.
 * It extends the BasePane class and provides methods to initialize and update the pane
 * with sensor data.
 * 
 * <p>There are two constructors available:
 * <ul>
 *   <li>{@link #SensorPane(Iterable)}: Constructs a new SensorPane with the title "Sensors" and initializes it with the provided sensor readings.</li>
 *   <li>{@link #SensorPane()}: Constructs a new SensorPane with the title "Sensors" without initializing it with sensor readings.</li>
 * </ul>
 * 
 * <p>The class also provides the following methods:
 * <ul>
 *   <li>{@link #initialize(Iterable)}: Initializes the SensorPane with the provided sensor readings.</li>
 *   <li>{@link #update(Iterable)}: Updates the SensorPane by clearing the current components and reinitializing them with the provided sensor readings.</li>
 * </ul>
 * 
 * <p>Each sensor reading is represented by a UI component created by the SensorComponentFactory.
 */
public class SensorPane extends BasePane {

    /**
     * Constructs a new SensorPane with the title "Sensors".
     * For each sensor in the collection, a corresponding UI component is created and added to the pane.
     */
    public SensorPane(Iterable<SensorReading> sensors) {
        super("Sensors");
        initialize(sensors);
    }

    /**
     * Constructs a new SensorPane with the title "Sensors".
     */
    public SensorPane() {
        super("Sensors");
    }

    /**
     * Initializes the SensorPane.
     * For each sensor in the collection, a corresponding UI component is created and added to the pane.
     *
     * @param sensors an iterable collection of SensorReading objects to be displayed in the pane
     */
    private void initialize(Iterable<SensorReading> sensors) {
        for (SensorReading sensor : sensors) {
            Node component = SensorComponentFactory.createComponent(sensor);
            addComponent(component);
        }
    }

    /**
     * Updates the sensor pane.
     * This method clears the current components and reinitializes them
     * with the provided sensor readings.
     *
     * @param sensors an Iterable of SensorReading objects to update the pane with
     */
    public void update(Iterable<SensorReading> sensors) {
        clearComponents();
        initialize(sensors);
    }


}
