package no.ntnu.messages.commands;

import no.ntnu.messages.Delimiters;

public class SuccessCommand extends Command {
  private String message;

  public SuccessCommand(String message) {
    super("SUCCESS");
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public String toProtocolString() {
    return this.getCommandString() + Delimiters.BODY_PARAMETERS_DELIMITER.getValue() + this.message;
  }


  public void setParameters(String[] parameters) {
    if (parameters.length != 1) {
      throw new IllegalArgumentException("Invalid number of parameters: " + parameters.length);
    }
    this.message = parameters[0];
  }
}