package no.ntnu.tools.stringification;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import no.ntnu.constants.Resources;
import no.ntnu.tools.Logger;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Base64ImageEncoderTest {
    private static File inputImageFile;
    private static File outputImageFile;
    private static BufferedImage bufferedImage;

    @BeforeAll
    public static void setup() throws IOException {
        // Provide valid image paths for testing
        inputImageFile = new File("resources\\images\\picsart_chuck.jpg"); // Replace with your image path

        bufferedImage = ImageIO.read(inputImageFile);

    }

    @Test
    public void testImageToString() throws IOException {
        // Test converting image to Base64 string
        String base64String = Base64ImageEncoder.imageToString(bufferedImage, "png");
        assertNotNull(base64String, "Base64 string should not be null.");
        assertFalse(base64String.isEmpty(), "Base64 string should not be empty.");
        Logger.info("Image successfully converted to Base64 string.");
    }
}
