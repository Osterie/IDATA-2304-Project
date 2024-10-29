package no.ntnu.intermediaryserver;

public class UnknownClientException extends RuntimeException {
    public UnknownClientException(String message) {
        super(message);
    }
}
