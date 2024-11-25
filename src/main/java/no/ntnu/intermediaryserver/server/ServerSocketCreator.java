package no.ntnu.intermediaryserver.server;

import java.io.IOException;
import java.net.ServerSocket;

import no.ntnu.tools.Logger;

public class ServerSocketCreator {

    private ServerSocketCreator() {
        // Empty. Prevent instantiation.
    }

    private static final int MAX_ATTEMPTS = 5;

    public static ServerSocket getAvailableServerSocket() {
        ServerConfig.ensureDefaultPort();
        return createServerSocket(0);
    }
    
    private static ServerSocket createServerSocket(int attempt) {

        if (attempt > MAX_ATTEMPTS){
            Logger.error("Could not open server socket on port after " + MAX_ATTEMPTS + " attempts");
            return null;
        }
        int port = getPort(attempt);
        int nextAttempt = attempt + 1;

        return attemptToCreateServerSocket(nextAttempt, port); 
    }

    private static int getPort(int nextAttempt) {
        int port = ServerConfig.getPortNumber(); // Get the port from ServerConfig
        try{
            port += (20*nextAttempt);
        }
        catch (PortNumberOutOfRangeException e){
            Logger.error("Attempted port number was out of range, trying lower port number");
            port -= (20*(nextAttempt));
        }
        return port;
    }

    private static ServerSocket attemptToCreateServerSocket(int nextAttempt, int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Logger.info("Server listening on port " + port);
            ServerConfig.setPortNumber(port); // Update the port number
            return serverSocket;
        } catch (IOException e) {
            Logger.error("Could not open server socket on port " + port + ": " + e.getMessage());
            return ServerSocketCreator.createServerSocket(nextAttempt);
        }
    }
}
