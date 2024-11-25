package no.ntnu.run;

import no.ntnu.gui.greenhouse.GreenhouseApplication;
import no.ntnu.intermediaryserver.server.IntermediaryServer;

/**
 * The ServerAndGreenhouseStarter class serves as the entry point to start both 
 * the intermediary server and the greenhouse application in a concurrent manner.
 *
 * This class performs the following actions:
 * 1. Starts the {@link no.ntnu.intermediaryserver.server.IntermediaryServer} in a separate thread.
 * 2. Launches the {@link no.ntnu.gui.greenhouse.GreenhouseApplication}.
 *
 * This ensures that the intermediary server and the greenhouse GUI can operate simultaneously.
 */
public class ServerAndGreenhouseStarter {

    /**
     * The main method serves as the entry point for starting the application.
     */
    public static void main(String[] args) {

        // Create an instance of the IntermediaryServer.
        IntermediaryServer proxyServer = new IntermediaryServer();

        // Start the IntermediaryServer in a separate thread.
        Thread serverThread = new Thread(proxyServer);
        serverThread.start();

        // Create and run the GreenhouseApplication.
        GreenhouseApplication greenhouseApplication = new GreenhouseApplication();
        greenhouseApplication.run();
    }
}