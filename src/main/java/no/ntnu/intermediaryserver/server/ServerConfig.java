package no.ntnu.intermediaryserver.server;

import static no.ntnu.tools.parsing.Parser.parseIntegerOrError;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import no.ntnu.tools.Logger;

/**
 * ServerConfig is a singleton class that manages the port number and host for
 * the server.
 * The port number is stored in a file so that it can be read and written to by
 * the server.
 * Having it stored in a file allows other clients to know the port number if it
 * is not the default port number.
 */
public class ServerConfig {

  private ServerConfig() {
    // Empty. Prevent instantiation.
  }

  private static final String CONFIG_FILE = "config/server_config.txt"; // File to store port number
  private static final int DEFAULT_PORT_NUMBER = 50500; // Default port number

  private static final int MIN_PORT_NUMBER = 20000; // Minimum port number
  private static final int MAX_PORT_NUMBER = 65535; // Maximum port number

  private static final String HOST = "localhost";

  private static int currentPort = DEFAULT_PORT_NUMBER; // Default port number


  static {
    readPortFromFile();
  }

  /**
   * Get the host name/ip for the server.
   *
   * @return The host name/ip
   */
  public static String getHost() {
    return HOST;
  }

  /**
   * Get the port number for the server.
   *
   * @return The port number
   */
  public static int getPortNumber() {
    readPortFromFile();
    return ServerConfig.currentPort;
  }

  /**
   * Ensure that the default port number is used.
   */
  public static void ensureDefaultPort() {
    if (ServerConfig.currentPort != DEFAULT_PORT_NUMBER) {
      ServerConfig.currentPort = DEFAULT_PORT_NUMBER;
      writePortToFile(DEFAULT_PORT_NUMBER);
    }
  }

  /**
   * Set the port number for the server.
   *
   * @param portNumber The port number to set
   */
  public static void setPortNumber(int portNumber) {
    if (!isValidPort(portNumber)) {
      throw new PortNumberOutOfRangeException(portNumber);
    }
    ServerConfig.currentPort = portNumber;
    // Write the new port number to the file
    writePortToFile(portNumber);
  }

  /**
   * Write the port number to file.
   *
   * @param portNumber The port number to write
   */
  private static void writePortToFile(int portNumber) {
    try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(CONFIG_FILE))) {
      Logger.info("Writing port number to file: " + portNumber);
      writer.write(String.valueOf(portNumber));
    } catch (IOException e) {
      Logger.error("Failed to write port number to file: " + e.getMessage());
    }
  }

  /**
   * Read the port number from file.
   * If the file does not exist, or the port number is invalid, the port number is
   * not changed.
   */
  private static void readPortFromFile() {
    int port = getPortNumberFromFile();

    if (isValidPort(port)) {
      ServerConfig.currentPort = port;
    }
  }

  /**
   * Get the port number from the file.
   *
   * @return The port number from the file, or -1 if the file does not exist
   */
  private static int getPortNumberFromFile() {

    int port = -1;
    if (!Files.exists(Paths.get(CONFIG_FILE))) {
      return port;
    }

    try {
      String portString = new String(Files.readAllBytes(Paths.get(CONFIG_FILE))).trim();
      if (!portString.isEmpty()) {
        port = parseIntegerOrError(portString, "Invalid port number in file");
      }
    } catch (IOException e) {
      Logger.error("Failed to read port number from file: " + e.getMessage());
    } catch (NumberFormatException e) {
      Logger.error("Port number in file is not a valid integer. Using default port.");
      port = -1;
    }
    return port;
  }

  /**
   * Check if a port number is valid.
   *
   * @param portNumber The port number to check
   * @return True if the port number is valid, false otherwise
   */
  private static boolean isValidPort(int portNumber) {
    return portNumber >= MIN_PORT_NUMBER && portNumber <= MAX_PORT_NUMBER;
  }
}
