package no.ntnu.tools.Stringification;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Base64;

public class Base64ImageEncoder {
    /**
     * Converts an image (JPG or PNG) into a Base64 encoded string.
     *
     * @param imageFile The image file to be converted (PNG or JPG).
     * @return A Base64 encoded string representing the image.
     * @throws IOException if the image cannot be read.
     */
    public static String imageToString(File imageFile) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(imageFile);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // Determine if it's PNG or JPG
        String imageFormat = getImageFormat(imageFile);

        // Write the image to the output stream in the specified format (JPG/PNG)
        ImageIO.write(bufferedImage, imageFormat, byteArrayOutputStream);

        // Encode the byte array to Base64 and return as a string
        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
    }

    /**
     * Converts a Base64 encoded string back into an image (BufferedImage).
     *
     * @param base64String The Base64 string representing the image.
     * @return A BufferedImage created from the Base64 string.
     * @throws IOException if the Base64 string cannot be decoded.
     */
    public static BufferedImage stringToImage(String base64String) throws IOException {
        byte[] decodedBytes = Base64.getDecoder().decode(base64String);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decodedBytes);

        // Read and return the image from the input stream
        return ImageIO.read(byteArrayInputStream);
    }

    /**
     * Determines the format (PNG or JPG) of the image based on the file extension.
     *
     * @param imageFile The image file.
     * @return The format of the image, either "png" or "jpg".
     */
    private static String getImageFormat(File imageFile) {
        String fileName = imageFile.getName().toLowerCase();
        if (fileName.endsWith(".png")) {
            return "png";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "jpg";
        } else {
            throw new IllegalArgumentException("Unsupported image format. Only PNG and JPG are supported.");
        }
    }
}
