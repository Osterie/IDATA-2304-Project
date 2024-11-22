package no.ntnu.messages.responses;

import no.ntnu.messages.commands.Command;

public class FailureResponse extends Response {

  public FailureResponse(Command command, String responseData) {
    super("FAILURE", command, responseData);
  }
}