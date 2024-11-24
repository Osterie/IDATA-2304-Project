package no.ntnu.messages.responses;

import no.ntnu.messages.commands.Command;

/**
 * Represents a successful response to a command in the messaging system.
 * This response indicates that the associated command was executed successfully.
 */
public class SuccessResponse extends Response {

  /**
   * Constructs a {@code SuccessResponse} with the specified command and response data.
   *
   * @param command      The {@link Command} associated with this success response.
   * @param responseData The data describing the success or additional details. Can be null or empty.
   */
  public SuccessResponse(Command command, String responseData) {
    super("SUCCESS", command, responseData);
  }
}