package no.ntnu.messages.commands.greenhouse;

import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.Transmission;

/**
 * Represents a general greenhouse command.
 * excecute: Abstract method used executing command.
 */
public abstract class GreenhouseCommand extends Transmission {

  /**
   * Constructor for GreenhouseCommand.
   *
   * @param commandString the string representation of the command. For example
   *                      "TURN_OFF_ALL_ACTUATORS".
   */
  public GreenhouseCommand(String commandString) {
    super(commandString);
  }

  /**
   * Abstract method for executing command.
   */
  public abstract Message execute(NodeLogic logic, MessageHeader fromHeader);
}
