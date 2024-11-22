package no.ntnu.intermediaryserver;

import no.ntnu.constants.PortNumberOutOfRangeException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * ServerConfig is a singleton class that manages the port number for the server.
 * The port number is stored in a file so that it can be read and written to by the server.
 * Having it stored in a file allows other clients to know the port number if it is not the default port number.
 */
public class ServerConfig {

    private ServerConfig() {
        // Empty. Prevent instantiation.
    }

    private static final String CONFIG_FILE = "config/server_config.txt";  // File to store port number
    private static final int DEFAULT_PORT_NUMBER = 50500; // Default port number

    private static final int MIN_PORT_NUMBER = 20000; // Minimum port number
    private static final int MAX_PORT_NUMBER = 65535; // Maximum port number

    private static final String HOST = "localhost";

    private static int currentPort = DEFAULT_PORT_NUMBER; // Default port number


    static {
        // Read the port number from the file on class load
        readPortFromFile();
    }

    public static String getHost() {
        return HOST;
    }

    public static int getPortNumber() {
        readPortFromFile();
        return ServerConfig.currentPort;
    }

    public static void ensureDefaultPort() {
        if (ServerConfig.currentPort != DEFAULT_PORT_NUMBER) {
            ServerConfig.currentPort = DEFAULT_PORT_NUMBER;
            writePortToFile(DEFAULT_PORT_NUMBER);
        }
    }

    public static void setPortNumber(int portNumber) {
        if (!isValidPort(portNumber)) {
            throw new PortNumberOutOfRangeException(portNumber);
        }
        ServerConfig.currentPort = portNumber;
        // Write the new port number to the file
        writePortToFile(portNumber);
    }

    private static void writePortToFile(int portNumber) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(CONFIG_FILE))) {
            writer.write(String.valueOf(portNumber));
        } catch (IOException e) {
            System.err.println("Failed to write port number to file: " + e.getMessage());
        }
    }

    private static void readPortFromFile() {
        try {
            // Check if the file exists and is not empty
            if (Files.exists(Paths.get(CONFIG_FILE))) {
                String portString = new String(Files.readAllBytes(Paths.get(CONFIG_FILE))).trim();
                if (!portString.isEmpty()) {
                    int port = Integer.parseInt(portString);
                    if (isValidPort(port)) {
                        ServerConfig.currentPort = port;
                    } else {
                        System.err.println("Port number in file is invalid. Using default port.");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read port number from file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Port number in file is not a valid integer. Using default port.");
        }
    }

    private static boolean isValidPort(int portNumber) {
        return portNumber >= MIN_PORT_NUMBER && portNumber <= MAX_PORT_NUMBER;
    }
}
