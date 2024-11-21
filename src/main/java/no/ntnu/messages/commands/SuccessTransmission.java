package no.ntnu.messages.commands;

import no.ntnu.messages.Delimiters;
import no.ntnu.messages.Transmission;

public class SuccessTransmission extends Transmission {
  private String message;

  public SuccessTransmission(String message) {
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
    return this.getTransmissionString() + Delimiters.BODY_FIELD_PARAMETERS.getValue() + this.message;
  }

  public void setParameters(String[] parameters) {
    if (parameters.length != 1) {
      throw new IllegalArgumentException("Invalid number of parameters: " + parameters.length);
    }
    this.message = parameters[0];
  }
}