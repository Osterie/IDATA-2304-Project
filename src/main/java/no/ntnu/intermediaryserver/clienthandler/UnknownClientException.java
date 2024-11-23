package no.ntnu.intermediaryserver.clienthandler;

/**
 * Custom exception thrown when a client type is unknown or unrecognized.
 * This is used to handle cases where a client attempts to connect with an invalid or unexpected type.
 */
public class UnknownClientException extends RuntimeException {

    /**
     * Constructs a new UnknownClientException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public UnknownClientException(String message) {
        super(message);
    }
}