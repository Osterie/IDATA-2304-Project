package no.ntnu.greenhouse.sensors;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

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
            File[] files = dir.listFiles((d, name) -> name.endsWith(".jpg") || name.endsWith(".png"));
            if (files != null && files.length > 0) {
                int randomIndex = new Random().nextInt(files.length);
                gottenImage = ImageIO.read(files[randomIndex]);
                this.fileExtension = files[randomIndex].getName().substring(files[randomIndex].getName().lastIndexOf("."));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

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
