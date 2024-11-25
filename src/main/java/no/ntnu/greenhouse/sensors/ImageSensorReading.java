package no.ntnu.greenhouse.sensors;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import no.ntnu.tools.stringification.Base64ImageEncoder;

import javax.imageio.ImageIO;

import no.ntnu.tools.Logger;

/**
 * Represents a sensor reading that contains an image.
 */
public class ImageSensorReading extends SensorReading{

    private BufferedImage currentImage;
    private String fileExtension;

    /**
     * Create an image sensor reading with no initial image.
     *
     * @param type The type of sensor being read
     */
    public ImageSensorReading(String type) {
        super(type);
        this.currentImage = null;
        this.fileExtension = null;
    }

    /**
     * Create an image sensor reading with an initial image.
     *
     * @param type  The type of sensor being read
     * @param image The initial image
     */
    public ImageSensorReading(String type, BufferedImage image) {
        super(type);
        this.currentImage = image;
        this.fileExtension = null;
    }

    /**
     * Generate a random image from the given file path.
     *
     * @param imagesFilePath The file path to search for images
     */
    public void generateRandomImage(String imagesFilePath) {
        // Gets a random image from the given file path.

        BufferedImage gottenImage = null;

        try {
            File dir = new File(imagesFilePath);
            // Logger.info("Looking for image files in: " + dir.getAbsolutePath());
            // Logger.info("Files in directory: " + dir.listFiles().length);
            File[] files = dir.listFiles((d, name) -> name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg"));
            if (files != null && files.length > 0) {
                int randomIndex = new Random().nextInt(files.length);
                File chosenFile = files[randomIndex];
                gottenImage = ImageIO.read(chosenFile);

                // Extract file extension for the chosen file
                this.fileExtension = chosenFile.getName().substring(chosenFile.getName().lastIndexOf("."));
            } else {
                Logger.info("No image files found in the specified directory.");
            }
        } catch (IOException e) {
            System.err.println("An error occurred while loading the image.");
            e.printStackTrace();
        }

        // Update current image if an image was successfully loaded
        this.currentImage = gottenImage;
    }

    /**
     * Get the current image.
     *
     * @return The current image
     */
    public BufferedImage getImage() {
        return currentImage;
    }

    /**
     * Get the file extension of the current image.
     *
     * @return The file extension of the current image
     */
    public String getFileExtension() {
        return fileExtension;
    }

    
    /**
     * Sets the file extension for the image sensor reading.
     *
     * @param fileExtension the file extension to set (e.g., "jpg", "png")
     */
    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param o the reference object with which to compare.
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ImageSensorReading that = (ImageSensorReading) o;
        return currentImage.equals(that.currentImage);
    }

    /**
     * Returns a hash code value for the object. This method is supported for the benefit of hash tables such as those provided by {@link java.util.HashMap}.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return currentImage.hashCode();
    }

    /**
     * Get a string representation of the sensor reading.
     *
     * @return A string representation of the sensor reading
     */
    @Override
    public String toString() {
        return "{ type=" + this.type + ", image=" + this.currentImage.toString() + ", fileExtension=" + this.fileExtension + " }";
    }

    /**
     * Get a human-readable (formatted) version of the current reading.
     * 
     * @return The sensor reading and the unit
     */
    @Override
    public String getFormatted() {
        return this.getType() + "=" + this.getImageFormatted() + " " + this.fileExtension;
    }

    /**
     * Get a Base64 encoded string representation of the image.
     *
     * @return A Base64 encoded string representing the image
     */
    private String getImageFormatted() {
        try {
            return Base64ImageEncoder.imageToString(this.currentImage, this.fileExtension);
        } catch (IOException e) {
            return "Error encoding image";
        }
    }
}
