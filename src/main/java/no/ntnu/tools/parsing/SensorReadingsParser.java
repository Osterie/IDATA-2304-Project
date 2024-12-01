package no.ntnu.tools.parsing;

import static no.ntnu.tools.parsing.Parser.parseDoubleOrError;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import no.ntnu.greenhouse.sensor.AudioSensorReading;
import no.ntnu.greenhouse.sensor.ImageSensorReading;
import no.ntnu.greenhouse.sensor.NoSensorReading;
import no.ntnu.greenhouse.sensor.NumericSensorReading;
import no.ntnu.greenhouse.sensor.SensorReading;
import no.ntnu.greenhouse.sensor.SensorType;
import no.ntnu.messages.Delimiters;
import no.ntnu.tools.Logger;
import no.ntnu.tools.stringification.Base64AudioEncoder;
import no.ntnu.tools.stringification.Base64ImageEncoder;

/**
 * The SensorReadingsParser class provides methods to parse sensor information
 * strings
 * into lists of SensorReading objects. It supports parsing of different types
 * of sensor
 * readings including image, numeric, and audio readings.
 *
 *
 * <p>Supported formats for sensor readings:
 * <ul>
 * <li>IMG: "type,base64String,fileExtension"</li>
 * <li>NUM: "type,value,unit"</li>
 * <li>AUD: "type,base64String,fileExtension"</li>
 * </ul>
 *
 *
 *
 * <p>Methods:
 * <ul>
 * <li>{@link #parseSensors(String)}: Parses a string containing sensor
 * information and returns a list of SensorReading objects.</li>
 * <li>{@link #parseReading(String)}: Parses a sensor reading from a string and
 * returns the corresponding SensorReading object.</li>
 * <li>{@link #parseImageReading(String, String, String)}: Parses an image
 * reading from a base64 encoded string.</li>
 * <li>{@link #parseAudioReading(String, String)}: Parses an audio reading from
 * a base64 encoded string.</li>
 * </ul>
 *
 *
 * <p>Exceptions:
 * <ul>
 * <li>{@link IllegalArgumentException}: Thrown if the input string is null,
 * empty, or has an invalid format.</li>
 * </ul>
 */
public class SensorReadingsParser {

  /**
   * Parses a string containing sensor information and returns a list of
   * SensorReading objects.
   *
   * @param sensorInfo the string containing sensor information, separated by a
   *                   specific delimiter.
   * @return a list of SensorReading objects parsed from the input string.
   * @throws IllegalArgumentException if the sensorInfo is null or empty.
   */
  public static List<SensorReading> parseSensors(String sensorInfo) {
    if (sensorInfo == null || sensorInfo.isEmpty()) {
      throw new IllegalArgumentException("Sensor info can't be empty");
    }
    List<SensorReading> readings = new LinkedList<>();

    String[] readingInfo = sensorInfo.split(Delimiters.BODY_SENSOR_SEPARATOR.getValue());
    for (String reading : readingInfo) {
      try {
        readings.add(parseReading(reading));
      } catch (IllegalArgumentException e) {
        Logger.error("Failed to parse sensor reading: " + e.getMessage());
      }
    }
    return readings;
  }

  /**
   * Parses a sensor reading from a string and returns the corresponding
   * SensorReading object.
   * The input string should be in the format "TYPE:DATA", where TYPE can be
   * "IMG", "NUM", or "AUD".
   * - For "IMG" (image) readings, DATA should be in the format
   * "type,base64String,fileExtension".
   * - For "NUM" (numeric) readings, DATA should be in the format
   * "type,value,unit".
   * - For "AUD" (audio) readings, DATA should be in the format
   * "type,base64String,fileExtension".
   *
   * @param reading the sensor reading string to parse
   * @return the parsed SensorReading object
   * @throws IllegalArgumentException if the reading is null, empty, or has an
   *                                  invalid format
   */
  private static SensorReading parseReading(String reading) {

    SensorReading sensorReadingToReturn = null;
    // Logger.info("Reading: " + reading);
    if (reading == null || reading.isEmpty()) {
      throw new IllegalArgumentException("Sensor reading can't be empty");
    }
    String[] formatParts = reading.split(":");
    if (formatParts.length != 2) {
      throw new IllegalArgumentException("Invalid sensor format/data: " + reading);
    }
    String[] assignmentParts = formatParts[1].split(Delimiters.BODY_FIELD_PARAMETERS.getValue());
    if (assignmentParts.length != 3) {
      throw new IllegalArgumentException("Invalid sensor reading specified: " + reading);
    }
    if (formatParts[0].equals("IMG")) {
      if ((SensorType.NONE.equals(assignmentParts[0]))) {
        return new NoSensorReading();
      }
      String type = assignmentParts[0];
      String base64String = assignmentParts[1];
      String fileExtension = assignmentParts[2];

      sensorReadingToReturn = parseImageReading(type, base64String, fileExtension);

      return sensorReadingToReturn;

    } else if (formatParts[0].equals("NUM")) {
      String type = assignmentParts[0];
      double value = parseDoubleOrError(assignmentParts[1],
              "Invalid sensor value: " + assignmentParts[1]);
      String unit = "";
      if (assignmentParts.length == 3) {
        unit = assignmentParts[2];
      }
      return new NumericSensorReading(SensorType.fromString(type), value, unit);
    } else if (formatParts[0].equals("AUD")) {
      if ((SensorType.NONE.equals(assignmentParts[0]))) {
        return new NoSensorReading();
      }
      String type = assignmentParts[0];
      String base64String = assignmentParts[1];

      return parseAudioReading(type, base64String);
    } else {
      throw new IllegalArgumentException("Unknown sensor format: " + formatParts[0]);
    }
  }

  /**
   * Parses an image reading from a base64 encoded string.
   *
   * @param type          the type of the sensor reading
   * @param base64String  the base64 encoded string representing the image
   * @param fileExtension the file extension of the image
   * @return an ImageSensorReading object containing the parsed image and its
   *         metadata
   * @throws IllegalArgumentException if the base64 string cannot be decoded into
   *                                  an image
   */
  private static ImageSensorReading parseImageReading(String type,
                                                      String base64String, String fileExtension) {
    BufferedImage image;
    try {
      image = Base64ImageEncoder.stringToImage(base64String);
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to decode image: " + e.getMessage(), e);
    }
    ImageSensorReading imageReading = new ImageSensorReading(SensorType.fromString(type), image);
    imageReading.setFileExtension(fileExtension);

    return imageReading;
  }

  /**
   * Parses an audio reading from a base64 encoded string.
   *
   * @param type         the type of the sensor as a string
   * @param base64String the base64 encoded string representing the audio data
   * @return an AudioSensorReading object containing the sensor type and the
   *         decoded audio file
   * @throws IllegalArgumentException if the audio data cannot be decoded
   */
  private static AudioSensorReading parseAudioReading(String type, String base64String) {
    File audioFile;
    try {
      audioFile = Base64AudioEncoder.stringToAudio(base64String);
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to decode audio: " + e.getMessage(), e);
    }
    return new AudioSensorReading(SensorType.fromString(type), audioFile);
  }

}
