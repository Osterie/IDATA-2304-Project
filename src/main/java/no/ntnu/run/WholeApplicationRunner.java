package no.ntnu.run;

import javafx.application.Platform;
import no.ntnu.gui.greenhouse.GreenhouseApplication;
import no.ntnu.intermediaryserver.ProxyServer;

public class WholeApplicationRunner {
    public static void main(String[] args) {


        // // Start ProxyServer in a separate thread
        // ProxyServer proxyServer = new ProxyServer();
        // Thread serverThread = new Thread(proxyServer);
        // serverThread.start();

        // // Launch the JavaFX application for the greenhouse
        // Platform.startup(() -> {
        //     Thread greenhouseThread = new Thread(() -> {
        //         GreenhouseApplication.startApp(false);
        //     });
        //     greenhouseThread.start();
        // });

        
        ProxyServer proxyServer = new ProxyServer();
        Thread serverThread = new Thread(proxyServer);
        serverThread.start();

        Thread greenhouseThread = new Thread(new GreenhouseApplication());
        greenhouseThread.start();
        // GreenhouseApplication greenhouseApplication = new GreenhouseApplication();
        // greenhouseApplication.run();
        
        // Thread controlPanelThread = new Thread(new ControlPanelStarter(false));
        // controlPanelThread.start();

        // ControlPanelStarter starter = new ControlPanelStarter(true);
        // starter.start();
    }
}
