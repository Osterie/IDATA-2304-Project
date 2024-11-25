package no.ntnu.tools;

/**
 * A logger class for encapsulating all the logging. We can either reduce the number of SonarLint
 * warnings, or implement it properly. This class makes sure we sue the same logging in all
 * places of our code.
 */
public class Logger {

  // ANSI escape codes for colors
  private static final String GREEN = "\u001B[32m";
  private static final String YELLOW = "\u001B[33m";
  private static final String RED = "\u001B[31m";
  private static final String RESET = "\u001B[0m";

  /**
   * Not allowed to create an instance of this class.
   */
  private Logger() {
  }

  /**
   * Log an information message.
   *
   * @param message The message to log. A newline is appended automatically.
   */
  public static void info(String message) {
    System.out.println(message);
  }

  /**
   * Log an info message without appending a newline to the log.
   *
   * @param message The message to log
   */
  public static void infoNoNewline(String message) {
    System.out.print(message);
  }

  public static void success(String message) {
    System.out.println(Logger.GREEN + message + Logger.RESET);
  }

  /**
   * Log a warning message.
   *
   * @param message The warning message to log
   */
  public static void warn(String message) {
    System.out.println(Logger.YELLOW + "WARNING: " + message + Logger.RESET);
  }

  /**
   * Log an error message.
   *
   * @param message The error message to log
   */
  public static void error(String message) {
    System.err.println(Logger.RED + "Error: " + message + Logger.RESET);
  }
}