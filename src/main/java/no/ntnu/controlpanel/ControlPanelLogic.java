package no.ntnu.controlpanel;

import static no.ntnu.intermediaryserver.ProxyServer.PORT_NUMBER;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Remote control for a TV - a TCP client.
 */
public class ControlPanelLogic {
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
      System.out.println("  >>> " + serverResponse);
    }
    else {
      System.out.println("The remote control is off, cannot send command.");
    }
  }
}
