package no.ntnu.greenhouse.sensors;

public abstract class Sensor {
    protected final SensorReading reading;
    
    protected Sensor() {
        this.reading = null;
        // this.reading = new NumericSensorReading(type, 10, null);
        // ensureValueBoundsAndPrecision(current);
    }
    
    public String getType() {
        return reading.getType();
    }
    
    public SensorReading getReading() {
        return reading;
    }
    
    // public SensorAbstract createClone() {
    //     return new SensorAbstract(this.reading.getType(), this.min, this.max,
    //         this.reading.getValue(), this.reading.getUnit());
    // }
    public abstract Sensor createClone();

    public abstract void addRandomNoise();

    public abstract void applyImpact(double impact);

    // public void addRandomNoise() {
    //     double newValue = this.reading.getValue() + generateRealisticNoise();
    //     ensureValueBoundsAndPrecision(newValue);
    // }
    
    // private void ensureValueBoundsAndPrecision(double newValue) {
    //     newValue = roundToTwoDecimals(newValue);
    //     if (newValue < min) {
    //     newValue = min;
    //     } else if (newValue > max) {
    //     newValue = max;
    //     }
    //     this.reading.setValue(newValue);
    // }
    
    // private double roundToTwoDecimals(double value) {
    //     return Math.round(value * 100.0) / 100.0;
    // }
    
    // private double generateRealisticNoise() {
    //     return Math.random() * 0.1 - 0.05;
    // }

}
