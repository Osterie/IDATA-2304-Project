package no.ntnu.greenhouse;

import no.ntnu.greenhouse.sensorreading.CameraSensorReading;
import no.ntnu.greenhouse.sensorreading.SensorReading;

public class ImageSensor extends Sensor {
    private SensorReading reading;
    
    public ImageSensor(String type, String imagePath) {
        super(type);
        this.reading = createSensorReading(imagePath);
    }
 
    public SensorReading createSensorReading(String imagePath){
        return new CameraSensorReading("camera", imagePath);
    }

    @Override
    public void applyImpact(double impact) {
        // TODO: should impact the image data in some way
        // switch to another image, apply some filter, etc.
    }


}

