package no.ntnu.greenhouse.sensorreading;

import java.awt.image.BufferedImage;

/**
 * Represents one picture sensor reading.
 */
public class PictureSensorReading extends SensorReading {
    private final BufferedImage image;

    public PictureSensorReading(String type, double value, String unit, BufferedImage image) {
        super(type, value, unit);
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    @Override
    public String toString() {
        return super.toString() + ", image=" + (image != null ? "available" : "not available");
    }
}


