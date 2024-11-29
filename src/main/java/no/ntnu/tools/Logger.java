package no.ntnu.tools;

/**
 * A logger class for encapsulating all the logging. We can either reduce the number of SonarLint
 * warnings, or implement it properly. This class makes sure we sue the same logging in all
 * places of our code.
 */
public class Logger {

  private static final int MAX_MESSAGE_LENGTH = 1000;

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
    message = limitMessageLength(message);
    System.out.println(message);
  }

  /**
   * Log an info message without appending a newline to the log.
   *
   * @param message The message to log
   */
  public static void infoNoNewline(String message) {
    message = limitMessageLength(message);
    System.out.print(message);
  }

  public static void success(String message) {
    message = limitMessageLength(message);
    System.out.println(AnsiColors.GREEN + message + AnsiColors.RESET);
  }

  /**
   * Log a warning message.
   *
   * @param message The warning message to log
   */
  public static void warn(String message) {
    message = limitMessageLength(message);
    System.out.println(AnsiColors.YELLOW + "WARNING: " + message + AnsiColors.RESET);
  }

  /**
   * Log an error message.
   *
   * @param message The error message to log
   */
  public static void error(String message) {
    message = limitMessageLength(message);
    System.err.println(AnsiColors.RED + "Error: " + message + AnsiColors.RESET);
  }


  private static String limitMessageLength(String message) {
    if (message.length() > MAX_MESSAGE_LENGTH) {
      message = message.substring(0, 1000) + "...";
    }
    return message;
  }
}