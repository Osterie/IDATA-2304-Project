package no.ntnu.intermediaryserver.server;

/**
 * A custom runtime exception that is thrown when a port number is outside the
 * valid range of allowed values.
 * This exception helps ensure that only valid port numbers are used when
 * configuring a server or network service. Port numbers must typically fall
 * within the range of 0 to 65535.
 */
public class PortNumberOutOfRangeException extends RuntimeException {

  /**
   * Constructs a new PortNumberOutOfRangeException with a detailed error message.
   *
   * @param portNumber The invalid port number that caused the exception.
   *                   This value is included in the exception message.
   */
  public PortNumberOutOfRangeException(int portNumber) {
    super("Port number out of range: " + portNumber);
  }
}