package no.ntnu.tools.stringification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import no.ntnu.constants.Resources;
import no.ntnu.tools.Logger;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Base64ImageEncoderTest {
    private static File inputImageFile;
    private static File outputImageFile;

    @BeforeAll
    public static void setup() {
        // Provide valid image paths for testing
        inputImageFile = new File(Resources.IMAGES.getPath() + "picsart_chuck.jpeg");  // Replace with your image path
    }

    // @Test
    // public void testImageToString() throws IOException {
    //     // Test converting image to Base64 string
    //     String base64String = Base64ImageEncoder.imageToString(inputImageFile);
    //     assertNotNull(base64String, "Base64 string should not be null.");
    //     assertFalse(base64String.isEmpty(), "Base64 string should not be empty.");
    //     Logger.info("Image successfully converted to Base64 string.");
    // }

    // @Test
    // public void testStringToImage() throws IOException {
    //     // Convert image to Base64 string first
    //     String base64String = Base64ImageEncoder.imageToString(inputImageFile);

    //     // Test converting Base64 string back to BufferedImage
    //     BufferedImage image = Base64ImageEncoder.stringToImage(base64String);
    //     assertNotNull(image, "BufferedImage should not be null.");
    //     assertEquals(image.getWidth(), 1024);
    //     assertEquals(image.getHeight(), 1024);
    //     Logger.info("Base64 string successfully converted back to image.");
    // }
}
