package no.ntnu.run;

import no.ntnu.gui.greenhouse.GreenhouseApplication;
import no.ntnu.intermediaryserver.server.IntermediaryServer;

public class ServerAndGreenhouseStarter {
    public static void main(String[] args) {

        IntermediaryServer proxyServer = new IntermediaryServer();
        Thread serverThread = new Thread(proxyServer);
        serverThread.start();

        GreenhouseApplication greenhouseApplication = new GreenhouseApplication();
        greenhouseApplication.run();
    }
}
