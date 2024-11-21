package no.ntnu.run;

import no.ntnu.controlpanel.CommunicationChannel;
import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.controlpanel.ControlPanelCommunicationChannel;
import no.ntnu.gui.controlpanel.ControlPanelApplication;
import no.ntnu.intermediaryserver.ServerConfig;
import no.ntnu.tools.Logger;

/**
 * Starter class for the control panel.
 * Note: we could launch the Application class directly, but then we would have issues with the
 * debugger (JavaFX modules not found)
 */
public class ControlPanelStarter implements Runnable {

  private ControlPanelCommunicationChannel channel;

  public ControlPanelStarter() {
    // Empty
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
    ControlPanelStarter starter = new ControlPanelStarter();
    starter.start();
  }

  public void start() {
    ControlPanelLogic logic = new ControlPanelLogic();
    this.initiateCommunication(logic);

    Logger.info("Starting control panel application");
    ControlPanelApplication controlPanelApplication = new ControlPanelApplication();
    controlPanelApplication.startApp(logic, this.channel);

    // This code is reached only after the GUI-window is closed
    Logger.info("Exiting the control panel application");
    stopCommunication();
  }

  private void initiateCommunication(ControlPanelLogic logic) {
    Logger.info("initiating socket communication");
    this.channel = initiateSocketCommunication(logic); 
  }


  private ControlPanelCommunicationChannel initiateSocketCommunication(ControlPanelLogic logic) {
      ControlPanelCommunicationChannel channel = new ControlPanelCommunicationChannel(logic, "localhost", ServerConfig.getPortNumber());
      logic.setCommunicationChannel(channel);
      channel.askForNodes();
      return channel;
  }

  private void stopCommunication() {
    // TODO - here you stop the TCP/UDP socket communication
    this.channel.close();
  }
}
