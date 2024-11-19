package no.ntnu.constants;

public enum PortNumber {
  PORT_NUMBER(50500);

  private final int port;

  PortNumber(int port) {
    this.port = port;
  }

  public int getPort() {
    return port;
  }
}