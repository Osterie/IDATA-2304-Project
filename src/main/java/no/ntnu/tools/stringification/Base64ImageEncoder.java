package no.ntnu.tools.stringification;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Base64;

/**
 * Utility class for encoding and decoding images to and from Base64 strings.
 *
 * <p>This class provides methods to:
 * <ul>
 *   <li>Convert a {@link BufferedImage} into a Base64 encoded string.</li>
 *   <li>Convert a Base64 encoded string back into a {@link BufferedImage}.</li>
 *   <li>Determine the format (e.g., PNG or JPG) of an image based on its file extension.</li>
 * </ul>
 *
 * <p>Use this class to easily store or transmit images in text-based formats.
 * Only PNG and JPG formats are supported.
 */
public class Base64ImageEncoder {

    /**
     * Converts an image (BufferedImage) into a Base64 encoded string.
     *
     * @param bufferedImage The image to be converted.
     * @param fileExtension The file format/extension of the image (e.g., "png", "jpg", "jpeg").
     * @return A Base64 encoded string representing the image.
     * @throws IOException if the image cannot be read or written in the specified format.
     */
    public static String imageToString(BufferedImage bufferedImage, String fileExtension) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // Write the image to the output stream in the specified format (JPG/PNG)
        ImageIO.write(bufferedImage, fileExtension, byteArrayOutputStream);

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
