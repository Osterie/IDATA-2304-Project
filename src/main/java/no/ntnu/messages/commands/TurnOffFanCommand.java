package no.ntnu.messages.commands;

import no.ntnu.messages.Filler;
import no.ntnu.messages.Message;

public class TurnOffFanCommand extends Command {
    private int nodeId;
    private int actuatorId;

  @Override
  public Message executeCommand(Filler logic) {
    //TODO implement executeCommand when the logic is implemented
    return null;
  }

  @Override
    public String toString() {
        return "Turn off fan";
    }
}
