package no.ntnu.greenhouse.sensorreading;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import no.ntnu.tools.stringification.Base64ImageEncoder;

/**
 * Represents one camera sensor reading.
 */
public class CameraSensorReading extends SensorReading {
    
    private final String imagePath;

    /**
     * Create a new camera sensor reading.
     *
     * @param type      The type of sensor being read
     * @param imagePath The path to the image file
     * @throws IllegalArgumentException If the imagePath is not a valid image file
     */
    public CameraSensorReading(String type, String imagePath) {
        super(type);
        validateImagePath(imagePath);
        this.imagePath = imagePath;
    }

    /**
     * Validate that the imagePath is a valid image file.
     *
     * @param imagePath The path to the image file
     * @throws IllegalArgumentException If the imagePath is not a valid image file
     */
    private void validateImagePath(String imagePath) {
        File file = new File(imagePath);
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("The provided path does not exist or is not a file: " + imagePath);
        }
        try {
            if (ImageIO.read(file) == null) {
                throw new IllegalArgumentException("The provided file is not a valid image: " + imagePath);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("An error occurred while reading the image file: " + imagePath, e);
        }
    }

    /**
     * Get the path to the image file.
     *
     * @return The path to the image file
     */
    public String getImage() {
        return imagePath;
    }
    
    /**
     * Get the sensor reading as a base64 encoded string.
     *
     * @return The sensor reading as a base64 encoded string
     */
    @Override
    public String readingAsString() {
        try {
            return Base64ImageEncoder.imageToString(new java.io.File(imagePath));
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}


