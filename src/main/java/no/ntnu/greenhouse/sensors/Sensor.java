package no.ntnu.greenhouse.sensors;

public abstract class Sensor {
    protected SensorReading reading;
    
    protected Sensor() {
        this.reading = null;
    }
    
    public String getType() {
        return reading.getType();
    }
    
    public SensorReading getReading() {
        return reading;
    }
    
    public abstract Sensor createClone();

    public abstract void addRandomNoise();

    public abstract void applyImpact(double impact);
}
