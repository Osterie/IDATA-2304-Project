package no.ntnu.messages.commands;

import no.ntnu.messages.Filler;
import no.ntnu.messages.Message;

public class GetNodeCommand extends Command {
    private int nodeId;

    public GetNodeCommand(int nodeId) {
        this.nodeId = nodeId;
    }

  @Override
  public Message executeCommand(Filler logic) {
    Message message;
    //TODO implement executeCommand when the logic is implemented



    return null;
  }

  @Override
  public String toString() {
    return "Get node";
  }

}
