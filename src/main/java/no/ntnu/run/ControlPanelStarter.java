package no.ntnu.run;

import no.ntnu.controlpanel.ControlPanelLogic;

import no.ntnu.controlpanel.ControlPanelCommunicationChannel;
import no.ntnu.gui.controlpanel.ControlPanelApplication;
import no.ntnu.intermediaryserver.server.ServerConfig;
import no.ntnu.tools.Logger;

/**
 * Starter class for the control panel.
 */
public class ControlPanelStarter implements Runnable {

  private ControlPanelCommunicationChannel channel;

  public ControlPanelStarter() {
    // Empty
  }

  /**
   * Run the control panel application using threads.
   */
  @Override
  public void run() {
    this.start();
  }

  /**
   * Entrypoint for the application.
   */
  public static void main(String[] args) {
    ControlPanelStarter starter = new ControlPanelStarter();
    starter.start();
  }

  /**
   * Start the control panel application.
   */
  public void start() {
    ControlPanelLogic logic = this.createLogic();

    this.startGui(logic);

    // This code is reached only after the GUI-window is closed
    Logger.info("Exiting the control panel application");
    this.stopCommunication();
  }

  /**
   * Create the logic for the control panel.
   * 
   * @return The logic for the control panel.
   */
  private ControlPanelLogic createLogic() {
    ControlPanelLogic logic = new ControlPanelLogic();
    this.channel = this.createCommunicationChannel(logic);
    logic.setCommunicationChannel(this.channel);
    this.channel.askForNodes();
    this.channel.askForSensorDataPeriodically(4);
    return logic;
  }

  /**
   * Start the GUI for the control panel.
   * 
   * @param logic The logic for the control panel.
   */
  private void startGui(ControlPanelLogic logic) {
    Logger.info("Starting control panel application");
    ControlPanelApplication controlPanelApplication = new ControlPanelApplication();
    controlPanelApplication.startApp(logic, this.channel);
  }

  /**
   * Create the communication channel for the control panel.
   * 
   * @param logic The logic for the control panel.
   * @return The communication channel for the control panel.
   */
  private ControlPanelCommunicationChannel createCommunicationChannel(ControlPanelLogic logic) {
    return new ControlPanelCommunicationChannel(logic, ServerConfig.getHost(), ServerConfig.getPortNumber());
  }

  /**
   * Stop the communication channel.
   */
  private void stopCommunication() {
    if (this.channel != null) {
      this.channel.close();
      Logger.info("Communication channel closed.");
    }
  }
}
