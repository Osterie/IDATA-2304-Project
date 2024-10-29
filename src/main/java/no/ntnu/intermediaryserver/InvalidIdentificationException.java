package no.ntnu.intermediaryserver;

public class InvalidIdentificationException extends RuntimeException {
    public InvalidIdentificationException(String message) {
        super(message);
    }

    public InvalidIdentificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
