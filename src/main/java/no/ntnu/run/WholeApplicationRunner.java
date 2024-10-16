package no.ntnu.run;

import no.ntnu.gui.greenhouse.GreenhouseApplication;
import no.ntnu.intermediaryserver.IntermediaryServer;

public class WholeApplicationRunner {
    public static void main(String[] args) {

        IntermediaryServer proxyServer = new IntermediaryServer();
        Thread serverThread = new Thread(proxyServer);
        serverThread.start();

        GreenhouseApplication greenhouseApplication = new GreenhouseApplication();
        greenhouseApplication.run();
    }
}
