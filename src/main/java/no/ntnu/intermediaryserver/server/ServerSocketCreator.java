package no.ntnu.intermediaryserver.server;

import java.io.IOException;
import java.net.ServerSocket;

import no.ntnu.tools.Logger;

/**
 * The ServerSocketCreator class is responsible for creating a server socket that is available to use.
 * If the port is not available, it will try the next port.
 */
public class ServerSocketCreator {

    private ServerSocketCreator() {
        // Empty. Prevent instantiation.
    }

    private static final int MAX_ATTEMPTS = 5;

    /**
     * Get a server socket that is available to use.
     * 
     * @return The server socket
     */
    public static ServerSocket getAvailableServerSocket() {
        ServerConfig.ensureDefaultPort();
        return createServerSocket(0);
    }
    
    /**
     * Create a server socket on a port number.
     * If the port is not available, try the next port.
     * If this fails after MAX_ATTEMPTS, return null.
     * 
     * @param attempt The number of attempts to create the server socket
     * @return The server socket, or null if it could not be created
     */
    private static ServerSocket createServerSocket(int attempt) {

        if (attempt > MAX_ATTEMPTS){
            Logger.error("Could not open server socket on port after " + MAX_ATTEMPTS + " attempts");
            return null;
        }
        int port = getPort(attempt);
        int nextAttempt = attempt + 1;

        return attemptToCreateServerSocket(nextAttempt, port); 
    }

    /**
     * Calculate the port number to use for the server socket, based on the attempt number.
     * 
     * @param attempt The number of attempts to create the server socket
     * @return The port number to use
     */
    private static int getPort(int attempt) {
        int port = ServerConfig.getPortNumber(); // Get the port from ServerConfig
        try{
            port += (20*attempt);
        }
        catch (PortNumberOutOfRangeException e){
            Logger.error("Attempted port number was out of range, trying lower port number");
            port -= (20*(attempt));
        }
        return port;
    }

    /**
     * Attempt to create a server socket on a port number.
     * 
     * @param nextAttempt The number of attempts to create the server socket
     * @param port The port number to use
     * @return The server socket, or null if it could not be created
     */
    private static ServerSocket attemptToCreateServerSocket(int nextAttempt, int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Logger.info("Server listening on port " + port);
            ServerConfig.setPortNumber(port); // Update the port number
            return serverSocket;
        } catch (IOException e) {
            Logger.warn("Could not open server socket on port " + port + ", trying other port, error message:" + e.getMessage());
            return ServerSocketCreator.createServerSocket(nextAttempt);
        }
    }
}
