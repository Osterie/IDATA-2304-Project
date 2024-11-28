package no.ntnu.tools;

/**
 * The {@code AnsiColors} enum defines ANSI color codes for console text formatting.
 * These codes can be used to change the color of text printed to the terminal,
 * and are reset using the RESET color.
 *
 * <p>Example usage:
 * <pre>
 * System.out.println(AnsiColors.GREEN + "This text is green!" + AnsiColors.RESET);
 * </pre>
 *
 * <p>Supported colors:
 * <ul>
 *   <li>GREEN - Displays text in green.</li>
 *   <li>YELLOW - Displays text in yellow.</li>
 *   <li>RED - Displays text in red.</li>
 *   <li>RESET - Resets text color to the default terminal color.</li>
 * </ul>
 */
public enum AnsiColors {

    /** ANSI code for green text. */
    GREEN("\u001B[32m"),

    /** ANSI code for yellow text. */
    YELLOW("\u001B[33m"),

    /** ANSI code for red text. */
    RED("\u001B[31m"),

    /** ANSI code to reset text color to the default. */
    RESET("\u001B[0m");

    /** The ANSI escape code associated with the color. */
    private final String color;

    /**
     * Constructs an {@code AnsiColors} enum with the specified ANSI escape code.
     *
     * @param color The ANSI escape code for the color.
     */
    AnsiColors(String color) {
        this.color = color;
    }

    /**
     * Returns the ANSI escape code for the color.
     *
     * @return The ANSI escape code as a {@code String}.
     */
    public String getColor() {
        return color;
    }

    /**
     * Returns the ANSI escape code as a string representation of the enum.
     *
     * @return The ANSI escape code as a {@code String}.
     */
    @Override
    public String toString() {
        return color;
    }
}