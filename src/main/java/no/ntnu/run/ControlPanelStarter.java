package no.ntnu.run;

import no.ntnu.controlpanel.CommunicationChannel;
import no.ntnu.controlpanel.ControlPanelLogicOld;
import no.ntnu.controlpanel.FakeCommunicationChannel;
import no.ntnu.gui.controlpanel.ControlPanelApplication;
import no.ntnu.tools.Logger;

import static no.ntnu.intermediaryserver.ProxyServer.PORT_NUMBER;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Platform;

/**
 * Starter class for the control panel.
 * Note: we could launch the Application class directly, but then we would have issues with the
 * debugger (JavaFX modules not found)
 */
public class ControlPanelStarter implements Runnable {
  private final boolean fake;
  private final ExecutorService executorService;


  public ControlPanelStarter(boolean fake) {
    this.fake = fake;

    // Create a thread pool with a single thread to handle socket communication
    this.executorService = Executors.newSingleThreadExecutor();
  }

  @Override
  public void run() {
    start();
  }

  /**
   * Entrypoint for the application.
   *
   * @param args Command line arguments, only the first one of them used: when it is "fake",
   *             emulate fake events, when it is either something else or not present,
   *             use real socket communication. Go to Run → Edit Configurations.
   *             Add "fake" to the Program Arguments field.
   *             Apply the changes.
   */
  public static void main(String[] args) {
    boolean fake = true;// make it true to test in fake mode
    if (args.length == 1 && "fake".equals(args[0])) {
      fake = true;
      Logger.info("Using FAKE events");
    }
    fake = false;
    ControlPanelStarter starter = new ControlPanelStarter(fake);
    starter.start();
  }

  public void start() {
    ControlPanelLogicOld logic = new ControlPanelLogicOld();
    CommunicationChannel channel = initiateCommunication(logic, fake);
    System.out.println("Starting control panel application");
    ControlPanelApplication controlPanelApplication = new ControlPanelApplication();
    controlPanelApplication.startApp(logic, channel);
    // This code is reached only after the GUI-window is closed
    // Logger.info("Exiting the control panel application");
    // stopCommunication();
  }

  private CommunicationChannel initiateCommunication(ControlPanelLogicOld logic, boolean fake) {
    CommunicationChannel channel;
    if (fake) {
      System.out.println("initiating fake spawner");
      channel = initiateFakeSpawner(logic);
    } else {
      System.out.println("initiating socket communication");
      channel = initiateSocketCommunication(logic);
    }
    return channel;
  }


  // private CommunicationChannel initiateSocketCommunication(ControlPanelLogic logic) {
  //   // TODO - here you initiate TCP/UDP socket communication
  //   // You communication class(es) may want to get reference to the logic and call necessary
  //   // logic methods when events happen (for example, when sensor data is received)
  //   return null;
  // }

  private CommunicationChannel initiateSocketCommunication(ControlPanelLogicOld logic) {
    try {
        // Connect to the server
        Socket socket = new Socket("localhost", PORT_NUMBER);  // Same port as in GreenhouseSimulator

        // Create input and output streams for communication
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

        // Example: read sensor data and send control commands back

    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
  }

  private CommunicationChannel initiateFakeSpawner(ControlPanelLogicOld logic) {
    // Here we pretend that some events will be received with a given delay
    FakeCommunicationChannel spawner = new FakeCommunicationChannel(logic);
    logic.setCommunicationChannel(spawner);
    final int START_DELAY = 5;
    spawner.spawnNode("4;3_window", START_DELAY);
    spawner.spawnNode("1", START_DELAY + 1);
    spawner.spawnNode("1", START_DELAY + 2);
    spawner.advertiseSensorData("4;temperature=27.4 °C,temperature=26.8 °C,humidity=80 %", START_DELAY + 2);
    spawner.spawnNode("8;2_heater", START_DELAY + 3);
    spawner.advertiseActuatorState(4, 1, true, START_DELAY + 3);
    spawner.advertiseActuatorState(4, 1, false, START_DELAY + 4);
    spawner.advertiseActuatorState(4, 1, true, START_DELAY + 5);
    spawner.advertiseActuatorState(4, 2, true, START_DELAY + 5);
    spawner.advertiseActuatorState(4, 1, false, START_DELAY + 6);
    spawner.advertiseActuatorState(4, 2, false, START_DELAY + 6);
    spawner.advertiseActuatorState(4, 1, true, START_DELAY + 7);
    spawner.advertiseActuatorState(4, 2, true, START_DELAY + 8);
    spawner.advertiseSensorData("4;temperature=22.4 °C,temperature=26.0 °C,humidity=81 %", START_DELAY + 9);
    spawner.advertiseSensorData("1;humidity=80 %,humidity=82 %", START_DELAY + 10);
    spawner.advertiseRemovedNode(8, START_DELAY + 11);
    spawner.advertiseRemovedNode(8, START_DELAY + 12);
    spawner.advertiseSensorData("1;temperature=25.4 °C,temperature=27.0 °C,humidity=67 %", START_DELAY + 13);
    spawner.advertiseSensorData("4;temperature=25.4 °C,temperature=27.0 °C,humidity=82 %", START_DELAY + 14);
    spawner.advertiseSensorData("4;temperature=25.4 °C,temperature=27.0 °C,humidity=82 %", START_DELAY + 16);
    return spawner;
  }

  private void stopCommunication() {
    // TODO - here you stop the TCP/UDP socket communication
  }
}
