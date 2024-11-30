package no.ntnu.run;

import no.ntnu.intermediaryserver.server.IntermediaryServer;

public class IntermediaryServerStarter {
    public static void main(String[] args) {
        // Create an instance of the IntermediaryServer.
        IntermediaryServer intermediaryServer = new IntermediaryServer();

        // Start the IntermediaryServer in a separate thread.
        Thread serverThread = new Thread(intermediaryServer);
        serverThread.start();
    }
}