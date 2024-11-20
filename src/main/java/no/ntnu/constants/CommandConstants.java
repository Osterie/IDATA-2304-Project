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
    this.intValue = intValue;
    this.stringValue = null;
  }

}