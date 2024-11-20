package no.ntnu.constants;

public enum CommandConstants {
  ALL("all"),
  PORT_NUMBER(50500),
  NONE("none");

  private final String stringValue;
  private final int intValue;

  CommandConstants (String stringValue) {
    this.intValue = -1;
    this.stringValue = stringValue;
  }
  CommandConstants (int intValue) {
    if (!intValid(intValue)) {
      throw new IllegalArgumentException("Invalid port number value");
    }
    this.intValue = intValue;
    this.stringValue = null;
  }

  public boolean intValid(int intValue) {
    return intValue >= 0 && intValue <= 65535;
  }

  public int getIntValue() {
    return intValue;
  }

}