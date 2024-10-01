package no.ntnu.commands;

/**
 * Message class used to display an error to the user
 */
public class ErrorMessage implements Message {
    private String message;

    public ErrorMessage(String message) {
        this.message = message;
    }

    public String toString() {
        return message;
    }
}
