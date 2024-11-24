package no.ntnu.messages.responses;

import no.ntnu.messages.commands.Command;

/**
 * Represents a failure response to a command.
 * This response is used to indicate that the execution of a command has failed.
 */
public class FailureResponse extends Response {

  /**
   * Constructs a {@code FailureResponse} with the specified command and response data.
   *
   * @param command      The {@link Command} associated with this failure response.
   * @param responseData The data describing the failure or additional details. Can be empty or null.
   */
  public FailureResponse(Command command, String responseData) {
    super("FAILURE", command, responseData);
  }
}