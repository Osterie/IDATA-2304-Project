package no.ntnu.run;

import no.ntnu.intermediaryserver.server.IntermediaryServer;

public class IntermediaryServerStarter {
    public static void main(String[] args) {
        // Create an instance of the IntermediaryServer.
        IntermediaryServer intermediaryServer = new IntermediaryServer();
        intermediaryServer.startServer();
    }
}
