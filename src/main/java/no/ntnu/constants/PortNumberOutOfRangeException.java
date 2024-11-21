package no.ntnu.constants;

public class PortNumberOutOfRangeException extends RuntimeException {
  public PortNumberOutOfRangeException(int portNumber) {
    super("Port number out of range: " + portNumber);
  }
}
