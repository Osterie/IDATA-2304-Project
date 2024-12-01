package no.ntnu.messages.commands.greenhouse;

import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.messages.Message;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.responses.SuccessResponse;

/**
 * Command to turn off all actuators in a node.
 */
public class TurnOffAllActuatorInNodeCommand extends GreenhouseCommand {

  /**
   * Creates a new instance of this command.
   */
  public TurnOffAllActuatorInNodeCommand() {
    super("TURN_OFF_ALL_ACTUATORS");
  }

  /**
   * Executes the command to turn off all actuators in a node.
   *
   * @param nodeLogic  The node logic to execute the command on.
   * @param fromHeader The header of the message that triggered this command.
   * @return A message containing the success response.
   */
  @Override
  public Message execute(NodeLogic nodeLogic, MessageHeader fromHeader) {
    nodeLogic.getNode().setAllActuators(false);
    SuccessResponse response = new SuccessResponse(this, "TURN_OFF_ALL_ACTUATORS_SUCCESS");
    MessageBody body = new MessageBody(response);
    return new Message(fromHeader, body);
  }

  /**
   * Converts the command to a string which follows transmission protocol.
   *
   * @return The command as a string following the transmission protocol.
   */
  @Override
  public String toString() {
    return this.getTransmissionString();
  }

}
