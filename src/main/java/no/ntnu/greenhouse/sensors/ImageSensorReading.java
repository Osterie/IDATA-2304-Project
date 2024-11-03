package no.ntnu.greenhouse.sensors;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import no.ntnu.tools.Logger;

public class ImageSensorReading extends SensorReading{

    private BufferedImage currentImage;
    private String fileExtension;

    public ImageSensorReading(String type) {
        super(type);
        this.currentImage = null;
        this.fileExtension = null;
    }

    public void generateRandomImage(String imagesFilePath) {
        // Gets a random image from the given file path.

        BufferedImage gottenImage = null;

        try {
            File dir = new File(imagesFilePath);
            Logger.info("Looking for image files in: " + dir.getAbsolutePath());
            Logger.info("Files in directory: " + dir.listFiles().length);
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
    public BufferedImage getImage() {
        return currentImage;
    }

    public String getFileExtension() {
        return fileExtension;
    }

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

    @Override
    public int hashCode() {
        return currentImage.hashCode();
    }

    @Override
    public String toString() {
        return "{ type=" + this.type + ", image=" + this.currentImage.toString() + ", fileExtension=" + this.fileExtension + " }";
    }

    @Override
    public String getFormatted() {
        return "Image";
    }
}
