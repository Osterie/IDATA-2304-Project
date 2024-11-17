package no.ntnu.messages.commands;

public abstract class ParameterizedCommand extends Command {
    private String[] parameters;

    public ParameterizedCommand(String commandString) {
        super(commandString);
    }

    public abstract void setParameters(String[] parameters);
}
