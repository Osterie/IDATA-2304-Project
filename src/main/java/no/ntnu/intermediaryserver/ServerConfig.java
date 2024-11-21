package no.ntnu.intermediaryserver;

import no.ntnu.constants.PortNumberOutOfRangeException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.ServerSocket;
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

    private static final String CONFIG_FILE = "config/server_config.txt"; // File to store port number
    private static final int DEFAULT_PORT_NUMBER = 50500; // Default port number
    private static final int MAX_PORT_NUMBER = 65535; // Maximum allowed port number
    private static int CURRENT_PORT_NUMBER = DEFAULT_PORT_NUMBER; // Current port number

    static {
        // Read the port number from the file on class load
        readPortFromFile();
    }

    public static int getPortNumber() {
        readPortFromFile();
        return ServerConfig.CURRENT_PORT_NUMBER;
    }

    public static void ensureDefaultPort() {
        if (ServerConfig.CURRENT_PORT_NUMBER != DEFAULT_PORT_NUMBER) {
            ServerConfig.CURRENT_PORT_NUMBER = DEFAULT_PORT_NUMBER;
            writePortToFile(DEFAULT_PORT_NUMBER);
        }
    }

    public static void setPortNumber(int portNumber) {
        if (!isPortValid(portNumber)) {
            throw new PortNumberOutOfRangeException(portNumber);
        }
        if (isPortInUse(portNumber)) {
            System.err.println("Port " + portNumber + " is already in use. Incrementing to find an available port...");
            portNumber = findAvailablePort(portNumber);
        }
        ServerConfig.CURRENT_PORT_NUMBER = portNumber;
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
                    if (isPortValid(port)) {
                        ServerConfig.CURRENT_PORT_NUMBER = port;
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

    private static boolean isPortValid(int portNumber) {
        return portNumber >= 0 && portNumber <= MAX_PORT_NUMBER;
    }

    private static boolean isPortInUse(int portNumber) {
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            return false; // If we can bind successfully, the port is not in use
        } catch (IOException e) {
            return true; // Port is in use
        }
    }

    private static int findAvailablePort(int startingPort) {
        int port = startingPort;
        while (port <= MAX_PORT_NUMBER) {
            if (!isPortInUse(port)) {
                return port;
            }
            port++;
        }
        throw new RuntimeException("No available ports found in the range " + startingPort + " to " + MAX_PORT_NUMBER);
    }
}
