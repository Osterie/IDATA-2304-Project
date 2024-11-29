package no.ntnu.tools.parsing;

import static no.ntnu.tools.parsing.Parser.parseBooleanOrError;
import static no.ntnu.tools.parsing.Parser.parseIntegerOrError;

import no.ntnu.controlpanel.SensorActuatorNodeInfo;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.messages.Delimiters;
import no.ntnu.tools.Logger;

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
  public static SensorActuatorNodeInfo createSensorNodeInfoFrom(String specification, ActuatorListener listener) {
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
   * Extracts actuator information from the specification string and adds it to the node info.
   *
   * @param actuatorSpecification The actuator specification string
   * @param info The SensorActuatorNodeInfo object to add actuators to
   */
  private static void parseActuators(String actuatorSpecification, SensorActuatorNodeInfo info, ActuatorListener listener) {
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
   * Extracts individual actuator details from the string and adds them to the node info.
   *
   * @param s The actuator information string
   * @param info The SensorActuatorNodeInfo object to add the actuator to
   */
  private static void parseActuatorInfo(String s, SensorActuatorNodeInfo info, ActuatorListener listener) {
    if (s == null || s.isEmpty()) {
      throw new IllegalArgumentException("Actuator info can't be empty");
    }
    String[] actuatorInfo = s.split("_"); //TODO enum for delimiter or what?
    if (actuatorInfo.length != 3) {
      throw new IllegalArgumentException("Invalid actuator info format: " + s);
    }

    String actuatorType = actuatorInfo[0];
    int  actuatorId = parseIntegerOrError(actuatorInfo[1],
        "Invalid actuator count: " + actuatorInfo[1]);
    boolean isOn = parseBooleanOrError(actuatorInfo[2],
        "Invalid actuator state: " + actuatorInfo[2]);

    Actuator actuator = new Actuator(actuatorId, actuatorType, info.getId());

    if (isOn) {
      actuator.turnOn();
    } else {
      actuator.turnOff();
    }

    actuator.setListener(listener);
    info.addActuator(actuator);
  }
}
