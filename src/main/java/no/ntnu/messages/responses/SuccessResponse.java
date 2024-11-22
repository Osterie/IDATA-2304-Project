package no.ntnu.messages.responses;

import no.ntnu.messages.commands.Command;

public class SuccessResponse extends Response {

  public SuccessResponse(Command command, String responseData) {
    super("SUCCESS", command, responseData);
  }
}