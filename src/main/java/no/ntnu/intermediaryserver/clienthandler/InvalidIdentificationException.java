package no.ntnu.intermediaryserver.clienthandler;

/**
 * Custom exception thrown when an identification message provided by a client is invalid.
 * This can occur when the message does not meet the expected format or contains unrecognized identifiers.
 */
public class InvalidIdentificationException extends RuntimeException {

    /**
     * Constructs a new InvalidIdentificationException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public InvalidIdentificationException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidIdentificationException with the specified detail message and cause.
     *
     * @param message the detail message explaining the reason for the exception
     * @param cause   the cause of the exception (a throwable cause, if available)
     */
    public InvalidIdentificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
