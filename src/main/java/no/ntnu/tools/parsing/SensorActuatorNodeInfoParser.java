package no.ntnu.tools.parsing;

import static no.ntnu.tools.parsing.Parser.parseBooleanOrError;
import static no.ntnu.tools.parsing.Parser.parseIntegerOrError;

import no.ntnu.controlpanel.SensorActuatorNodeInfo;
import no.ntnu.greenhouse.actuator.Actuator;
import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.messages.Delimiters;
import no.ntnu.tools.Logger;

/**
 * Utility class for parsing specifications to create SensorActuatorNodeInfo
 * objects.
 *
 * <p>
 * This class provides methods to parse specification strings and generate
 * {@link SensorActuatorNodeInfo} objects that include associated actuators.
 * The specification string contains details about the sensor-actuator nodes and
 * their actuators.
 *
 * <p>
 * Key functionalities:
 * <ul>
 * <li>Parse node ID and actuator details from a structured string format.</li>
 * <li>Create and configure {@link Actuator} objects based on the
 * specification.</li>
 * <li>Support integration with {@link ActuatorListener} for actuator
 * events.</li>
 * </ul>
 *
 * <p>
 * The specification string format is expected to adhere to the following
 * structure:
 * <ul>
 *
 * <li><b>Node specification:</b>
 * [node_id][delimiter][actuator_specifications]</li>
 * <li><b>Actuator specification:</b>
 * [type]_[id]_[on_text]_[off_text]_[state]</li>
 * </ul>
 *
 * <p>
 * Example specification:
 * 
 * <pre>
 * "1;temperature_101_ON_OFF_true;humidity_102_START_STOP_false"
 * </pre>
 *
 * <p>
 * This would create a {@link SensorActuatorNodeInfo} object with ID 1 and two
 * actuators:
 * <ul>
 * <li>Actuator of type "temperature" with ID 101, turned on, with texts "ON"
 * and "OFF".</li>
 * <li>Actuator of type "humidity" with ID 102, turned off, with texts "START"
 * and "STOP".</li>
 * </ul>
 *
 * <h3>Exceptions:</h3>
 * <ul>
 * <li>{@link IllegalArgumentException} is thrown for invalid or malformed
 * specifications.</li>
 * </ul>
 *
 * <p>
 * <strong>Note:</strong> This class is not intended to be
 * instantiated and only contains static methods.
 */
public class SensorActuatorNodeInfoParser {

  private SensorActuatorNodeInfoParser() {
    // Private constructor
  }

  /**
   * Create a SensorActuatorNodeInfo object from a specification string.
   * Parses the specification string to create a SensorActuatorNodeInfo object.
   *
   * @param specification The specification string
   * @return The created SensorActuatorNodeInfo object
   */
  public static SensorActuatorNodeInfo createSensorNodeInfoFrom(
      String specification, ActuatorListener listener) {
    Logger.info("specification: " + specification);
    if (specification == null || specification.isEmpty()) {
      throw new IllegalArgumentException("Node specification can't be empty");
    }

    String[] parts = specification.split(Delimiters.BODY_FIELD.getValue());
    if (parts.length < 1 || parts.length > 2) {
      throw new IllegalArgumentException("Invalid node specification: " + specification);
    }
    int nodeId = parseIntegerOrError(parts[0], "Invalid node ID:" + parts[0]);
    SensorActuatorNodeInfo info = new SensorActuatorNodeInfo(nodeId);

    if (parts.length == 2) {
      parseActuators(parts[1], info, listener);
    }
    return info;
  }

  /**
   * Parse actuators from a specification string and add them to the node info.
   * Extracts actuator information from the specification string and adds it to
   * the node info.
   *
   * @param actuatorSpecification The actuator specification string
   * @param info                  The SensorActuatorNodeInfo object to add
   *                              actuators to
   */
  private static void parseActuators(String actuatorSpecification,
      SensorActuatorNodeInfo info, ActuatorListener listener) {
    if (actuatorSpecification == null || actuatorSpecification.isEmpty()) {
      throw new IllegalArgumentException("Actuator specification can't be empty");
    }

    String[] parts = actuatorSpecification.split(Delimiters.BODY_FIELD_PARAMETERS.getValue());
    for (String part : parts) {
      parseActuatorInfo(part, info, listener);
    }
  }

  /**
   * Parse actuator information from a string and add it to the node info.
   * Extracts individual actuator details from the string and adds them to the
   * node info.
   *
   * @param s    The actuator information string
   * @param info The SensorActuatorNodeInfo object to add the actuator to
   */
  private static void parseActuatorInfo(String s,
      SensorActuatorNodeInfo info, ActuatorListener listener) {
    if (s == null || s.isEmpty()) {
      throw new IllegalArgumentException("Actuator info can't be empty");
    }
    String[] actuatorInfo = s.split(Delimiters.BODY_SENSOR_SEPARATOR.getValue());
    if (actuatorInfo.length != 5) {
      throw new IllegalArgumentException("Invalid actuator info format: " + s);
    }

    Actuator actuator = Actuator.fromString(s, info.getId());
    actuator.setListener(listener);
    info.addActuator(actuator);
  }
}
