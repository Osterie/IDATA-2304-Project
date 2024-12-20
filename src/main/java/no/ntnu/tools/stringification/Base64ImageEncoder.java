package no.ntnu.tools.stringification;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import javax.imageio.ImageIO;

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
   * Converts image to string.
   *
   * @param bufferedImage image to be converted.
   * @param fileExtension image type.
   * @return stringed image
   * @throws IOException exception
   */
  public static String imageToString(BufferedImage bufferedImage,
                                     String fileExtension) throws IOException {
    
    if (bufferedImage.getType() != BufferedImage.TYPE_INT_RGB) {
      BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(),
              bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
      newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, null);
      bufferedImage = newBufferedImage;
    }
    
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    // Write the image to the output stream in the specified format (JPG/PNG)


    if (ImageIO.write(bufferedImage, "png", byteArrayOutputStream) == false) {
      throw new IOException("ImageIO.write() failed");
    }

    // Encode the byte array to Base64 and return as a string
    String testString = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());

    return testString;
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
      throw new IllegalArgumentException(
              "Unsupported image format. Only PNG and JPG are supported."
      );
    }
  }
}
