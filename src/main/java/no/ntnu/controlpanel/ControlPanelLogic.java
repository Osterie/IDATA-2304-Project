package no.ntnu.controlpanel;

import static no.ntnu.intermediaryserver.ProxyServer.PORT_NUMBER;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.SensorReading;
import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.listeners.common.CommunicationChannelListener;
import no.ntnu.listeners.controlpanel.GreenhouseEventListener;
import no.ntnu.tools.Logger;

/**
 * Remote control for a TV - a TCP client.
 */
public class ControlPanelLogic implements GreenhouseEventListener, ActuatorListener,
    CommunicationChannelListener  {

  private final List<GreenhouseEventListener> listeners = new LinkedList<>();

  private CommunicationChannel communicationChannel;
  private CommunicationChannelListener communicationChannelListener;

  private Socket socket;
  private BufferedReader socketReader;
  private PrintWriter socketWriter;

  private boolean isOn = false;

  private static final int DEFAULT_PORT_NUMBER = PORT_NUMBER;
  private int currentPortNumber = DEFAULT_PORT_NUMBER;

  private final String DEFAULT_HOST = "localhost";
  private String currentHost = DEFAULT_HOST;

  /**
  * Initializes a remote control. 
  */
  public static void main(String[] args) {

    ControlPanelLogic controlPanelLogic1 = new ControlPanelLogic();
    controlPanelLogic1.start();

    ControlPanelLogic controlPanleLogic2 = new ControlPanelLogic();
    controlPanleLogic2.start();

    try{
      controlPanelLogic1.sendCommandToServer("Test");
      controlPanelLogic1.sendCommandToServer("Test");
      controlPanelLogic1.sendCommandToServer("Test");
      // controlPanleLogic2.sendCommandToServer("1");
      controlPanleLogic2.sendCommandToServer("Test");
      controlPanelLogic1.sendCommandToServer("Test");

      controlPanelLogic1.stop();
      controlPanleLogic2.stop();
    }
    catch (IOException e) {
      System.err.println("Could not send command to server: " + e.getMessage());
    }    
  }

  /**
   * Set the host.
   * 
   * @param host the host to set.
   */
  public void setHost(String host) {
    this.currentHost = host;
  }

  /**
   * Returns the host.
   * 
   * @return the host.
   */
  public String getHost() {
    return this.currentHost;
  }

  /**
   * Set the port number.
   * 
   * @param port the port number to set.
   */
  public void setPort(int port) {
    this.currentPortNumber = port;
  }

  /**
   * Returns the port number.
   * 
   * @return the port number.
   */
  public int getPort() {
    return this.currentPortNumber;
  }

  /**
   * Sets the host to be the default host.
   */
  public void setDefaultHost() {
    this.currentHost = DEFAULT_HOST;
  }

  /**
   * Sets the port number to be the default port number.
   */
  public void setDefaultPort() {
    this.currentPortNumber = DEFAULT_PORT_NUMBER;
  }

  /**
   * Start the remote control.
   * Able to send commands if started
   */
  public void start(){
    try{
      this.socket = new Socket(this.currentHost, this.currentPortNumber);
      this.socketReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
      this.socketWriter = new PrintWriter(this.socket.getOutputStream(), true);
      this.isOn = true;
    }
    catch (IOException e) {
      System.err.println("Could not establish connection to the server: " + e.getMessage());
    }
  }

  /**
   * Stop the remote control.
   * Unable to send commands if stopped
   */
  public void stop(){
    try {
      this.socket.close();
    } catch (IOException e) {
      System.err.println("Could not close connection to the server: " + e.getMessage());
    }
    this.isOn = false;
  }

  public String getRemoteControlString() {
    return this.currentHost + ":" + this.currentPortNumber;
  }

  // private void run() {
  // while (this.isOn) {

  // }
  // }

  // private void testRun() {
  //     sendCommandToServer("c");
  //     sendCommandToServer("g");
  //     sendCommandToServer("1");
  //     sendCommandToServer("c");
  //     sendCommandToServer("g");
  //     sendCommandToServer("s13");
  //     sendCommandToServer("sDdd");
  //     sendCommandToServer("s15");
  //     sendCommandToServer("s0");
  //     sendCommandToServer("s-2");
  //     sendCommandToServer("g");
  //     sendCommandToServer("s4");
  //     sendCommandToServer("g");
  //     sendCommandToServer("0");
  //     sendCommandToServer("g");
  //     sendCommandToServer("s12");
  //     sendCommandToServer("1");
  //     sendCommandToServer("g");
  //     sendCommandToServer("0");
  // }

  /**
   * Send a command to the server.
   * 
   * @param command the command to send.
   * @throws IOException if an I/O error occurs when sending the command.
   */
  private void sendCommandToServer(String command) throws IOException {

    if (this.isOn){
      System.out.println("Sending command: " + command.toString());
      socketWriter.println(command);
      String serverResponse = socketReader.readLine();
      System.out.println("  >>> Response: " + serverResponse);
    }
    else {
      System.out.println("The remote control is off, cannot send command.");
    }
  }


    /**
   * Set the channel over which control commands will be sent to sensor/actuator nodes.
   *
   * @param communicationChannel The communication channel, the event sender
   */
  public void setCommunicationChannel(CommunicationChannel communicationChannel) {
    this.communicationChannel = communicationChannel;
  }

  
  /**
   * Set listener which will get notified when communication channel is closed.
   *
   * @param listener The listener
   */
  public void setCommunicationChannelListener(CommunicationChannelListener listener) {
    this.communicationChannelListener = listener;
  }

  /**
   * Add an event listener.
   *
   * @param listener The listener who will be notified on all events
   */
  public void addListener(GreenhouseEventListener listener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  @Override
  public void onNodeAdded(SensorActuatorNodeInfo nodeInfo) {
    listeners.forEach(listener -> listener.onNodeAdded(nodeInfo));
  }

  @Override
  public void onNodeRemoved(int nodeId) {
    listeners.forEach(listener -> listener.onNodeRemoved(nodeId));
  }

  @Override
  public void onSensorData(int nodeId, List<SensorReading> sensors) {
    listeners.forEach(listener -> listener.onSensorData(nodeId, sensors));
  }

  @Override
  public void onActuatorStateChanged(int nodeId, int actuatorId, boolean isOn) {
    listeners.forEach(listener -> listener.onActuatorStateChanged(nodeId, actuatorId, isOn));
  }

  @Override
  public void actuatorUpdated(int nodeId, Actuator actuator) {
    if (communicationChannel != null) {
      communicationChannel.sendActuatorChange(nodeId, actuator.getId(), actuator.isOn());
    }
    listeners.forEach(listener ->
        listener.onActuatorStateChanged(nodeId, actuator.getId(), actuator.isOn())
    );
  }

  @Override
  public void onCommunicationChannelClosed() {
    Logger.info("Communication closed, updating logic...");
    if (communicationChannelListener != null) {
      communicationChannelListener.onCommunicationChannelClosed();
    }
  }
}
