package no.ntnu.commands;

/**
 * Represents a general command
 *
 * excecuteCommand: Abstract method used for carrying out command
 */
public abstract class Command implements Message {

    /**
     * Abstract method for executing command
     */
    public abstract Message executeCommand(Filler logic);
    // TODO: Fill in with the right logic class
    /**
     * Abstract method for converting command to string
     */
    public abstract String toString();
}
