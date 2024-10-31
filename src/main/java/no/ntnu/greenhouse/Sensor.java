package no.ntnu.greenhouse;

import no.ntnu.greenhouse.sensorreading.SensorReading;

public abstract class Sensor {
    protected SensorReading reading;

    public Sensor(String type){
    }

    /**
     * Get the type of the sensor.
     *
     * @return The type of the sensor
    */
    public String getType() {
        return reading.getType();
    }

    /**
     * Get the current value of the sensor.
     *
     * @return The current value of the sensor
     */
    public SensorReading getReading() {
        return reading;
    }

    /**
    * Apply an external impact (from an actuator) to the current value of the sensor.
    *
    * @param impact The impact to apply - the delta for the value
    */
    public abstract void applyImpact(double impact);

    /**
    * Create a clone of this sensor.
    *
    * @return A clone of this sensor, where all the fields are the same
    */
    public abstract Sensor createClone();

}
