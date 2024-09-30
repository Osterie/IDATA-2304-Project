package no.ntnu.tools;

/**
 * A helper class for parsing strings.
 */
public class Parser {
  /**
   * Not allowed to create instances of this class.
   */
  private Parser() {

  }

  /**
   * Try to parse a string as an integer, show an error message when the parsing fails.
   *
   * @param s            The string to parse
   * @param errorMessage The error message to show if parsing fails
   * @return The integer contained in the string
   * @throws NumberFormatException When the provided string does not contain a valid number,
   *                               throw an exception with the provided error message
   */
  public static int parseIntegerOrError(String s, String errorMessage) {
    try {
      return Integer.parseInt(s);
    } catch (NumberFormatException e) {
      throw new NumberFormatException(errorMessage);
    }
  }

  /**
   * Try to parse a string as a floating point number, show an error message when the parsing fails.
   *
   * @param s            The string to parse
   * @param errorMessage The error message to show if parsing fails
   * @return The floating point number contained in the string
   * @throws NumberFormatException When the provided string does not contain a valid number,
   *                               throw an exception with the provided error message
   */
  public static double parseDoubleOrError(String s, String errorMessage) {
    try {
      return Double.parseDouble(s);
    } catch (NumberFormatException e) {
      throw new NumberFormatException(errorMessage);
    }
  }
}
