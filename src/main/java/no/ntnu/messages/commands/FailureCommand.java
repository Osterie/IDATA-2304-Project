package no.ntnu.messages.commands;

import no.ntnu.messages.Delimiters;

public class FailureCommand extends Command {
  private String errorMessage;

  public FailureCommand(String errorMessage) {
    super("FAILURE");
    this.errorMessage = errorMessage;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  @Override
  public String toProtocolString() {
    return this.getCommandString() + Delimiters.BODY_FIELD_PARAMETERS.getValue() + this.errorMessage;
  }


  public void setParameters(String[] parameters) {
    if (parameters.length != 1) {
      throw new IllegalArgumentException("Invalid number of parameters: " + parameters.length);
    }
    this.errorMessage = parameters[0];
  }
}