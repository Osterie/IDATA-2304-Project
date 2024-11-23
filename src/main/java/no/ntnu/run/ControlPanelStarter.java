package no.ntnu.run;

import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.controlpanel.ControlPanelCommunicationChannel;
import no.ntnu.gui.controlpanel.ControlPanelApplication;
import no.ntnu.intermediaryserver.ServerConfig;
import no.ntnu.tools.Logger;

/**
 * Starter class for the control panel.
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
   */
  public static void main(String[] args) {
    ControlPanelStarter starter = new ControlPanelStarter();
    starter.start();
  }

  // TODO refactor
  public void start() {
    ControlPanelLogic logic = new ControlPanelLogic();
    this.channel = this.createCommunicationChannel(logic);

    Logger.info("Starting control panel application");
    ControlPanelApplication controlPanelApplication = new ControlPanelApplication();
    controlPanelApplication.startApp(logic, this.channel);

    Logger.info("Setting communication channel to the logic");
    logic.setCommunicationChannel(this.channel);
    Logger.info("Asking for node data");
    this.channel.askForNodes();
    // this.channel.askForSensorDataPeriodically(4);


    // This code is reached only after the GUI-window is closed
    Logger.info("Exiting the control panel application");
    stopCommunication();
  }

  private ControlPanelCommunicationChannel createCommunicationChannel(ControlPanelLogic logic) {
    return new ControlPanelCommunicationChannel(logic, ServerConfig.getHost(), ServerConfig.getPortNumber());
  }

  private void stopCommunication() {
    // TODO - here you stop the TCP/UDP socket communication
    if (this.channel != null) {
      this.channel.close();
      Logger.info("Communication channel closed.");
    }

  }
}
