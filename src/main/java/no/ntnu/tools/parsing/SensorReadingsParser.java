package no.ntnu.tools.parsing;

import static no.ntnu.tools.parsing.Parser.parseDoubleOrError;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import no.ntnu.greenhouse.SensorType;
import no.ntnu.greenhouse.sensors.AudioSensorReading;
import no.ntnu.greenhouse.sensors.ImageSensorReading;
import no.ntnu.greenhouse.sensors.NoSensorReading;
import no.ntnu.greenhouse.sensors.NumericSensorReading;
import no.ntnu.greenhouse.sensors.SensorReading;
import no.ntnu.tools.Logger;
import no.ntnu.tools.stringification.Base64AudioEncoder;
import no.ntnu.tools.stringification.Base64ImageEncoder;

public class SensorReadingsParser {
    
  /**
   * Parse sensor readings from a string.
   * Extracts sensor readings from the provided string and returns them as a list.
   *
   * @param sensorInfo The sensor information string
   * @return A list of parsed sensor readings
   */
  public static List<SensorReading> parseSensors(String sensorInfo) {
    if (sensorInfo == null || sensorInfo.isEmpty()) {
      throw new IllegalArgumentException("Sensor info can't be empty");
    }
    List<SensorReading> readings = new LinkedList<>();
    String[] readingInfo = sensorInfo.split(",");
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
   * Parses a sensor reading from a string and returns a SensorReading object.
   *
   * @param reading the sensor reading string in the format
   *                "type=value unit" or "image=base64String fileExtension"
   * @return a SensorReading object representing the parsed sensor reading
   * @throws IllegalArgumentException if the reading is null, empty, or not in the expected format
   */
  private static SensorReading parseReading(String reading) {
    Logger.info("Reading: " + reading);
    if (reading == null || reading.isEmpty()) {
      throw new IllegalArgumentException("Sensor reading can't be empty");
    }
    String[] formatParts = reading.split(":");
    if (formatParts.length != 2) {
      throw new IllegalArgumentException("Invalid sensor format/data: " + reading);
    }
    String[] assignmentParts = formatParts[1].split("=");
    //TODO ADDED ANOTHER ACCEPTED VALUE BECAUSE THE WAY BASE64 CLASS MAKES AUDIO FILE TO STRING. SHOUL PROBABLY BE CHANGED
    if (assignmentParts.length != 2 && assignmentParts.length != 4) {
      throw new IllegalArgumentException("Invalid sensor reading specified: " + reading);
    }
    String[] valueParts = assignmentParts[1].split(" ");
    //TODO ADDED ANOTHER ACCEPTED VALUE BECAUSE THE WAY BASE64 CLASS MAKES AUDIO FILE TO STRING. SHOUL PROBABLY BE CHANGED
    if (valueParts.length != 3 && valueParts.length != 2) {
      throw new IllegalArgumentException("Invalid sensor value/unit: " + reading);
    }
    if (formatParts[0].equals("IMG")) {
      if ((SensorType.NONE.equals(valueParts[2]))) {
        return new NoSensorReading();
      }
      String type = assignmentParts[0];
      String base64String = valueParts[1];
      String fileExtension = valueParts[2];

      BufferedImage image;
      try {
        image = Base64ImageEncoder.stringToImage(base64String);
      } catch (IOException e) {
        throw new IllegalArgumentException("Failed to decode image: " + e.getMessage(), e);
      }
      ImageSensorReading imageReading = new ImageSensorReading(SensorType.fromString(type), image);
      imageReading.setFileExtension(fileExtension);
      
      return imageReading;
    } else if (formatParts[0].equals("NUM")) {
      String type = assignmentParts[0];
      double value = parseDoubleOrError(valueParts[1], "Invalid sensor value: " + valueParts[1]);
      String unit = "";
      if (valueParts.length == 3) {
        unit = valueParts[2];
      }
      return new NumericSensorReading(SensorType.fromString(type), value, unit);
    } else if (formatParts[0].equals("AUD")) {
      if ((SensorType.NONE.equals(valueParts[2]))) {
        return new NoSensorReading();
      }
      String type = assignmentParts[0];
      String base64String = valueParts[1];

      File audioFile;
      try {
        audioFile = Base64AudioEncoder.stringToAudio(base64String);
      } catch (IOException e) {
        throw new IllegalArgumentException("Failed to decode audio: " + e.getMessage(), e);
      }
      return new AudioSensorReading(SensorType.fromString(type), audioFile);
    } else {
      throw new IllegalArgumentException("Unknown sensor format: " + formatParts[0]);
    }
  }
}
