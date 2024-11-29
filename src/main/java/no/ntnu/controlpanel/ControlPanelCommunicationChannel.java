package no.ntnu.controlpanel;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static no.ntnu.tools.Parser.parseIntegerOrError;


import no.ntnu.SensorReadingsParser;
import no.ntnu.SocketCommunicationChannel;
import no.ntnu.constants.Endpoints;
import no.ntnu.greenhouse.sensors.NoSensorReading;
import no.ntnu.greenhouse.sensors.SensorReading;
import no.ntnu.intermediaryserver.clienthandler.ClientIdentification;
import no.ntnu.tools.Logger;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.commands.greenhouse.ActuatorChangeCommand;
import no.ntnu.messages.commands.greenhouse.GetNodeCommand;
import no.ntnu.messages.commands.greenhouse.GetSensorDataCommand;
import no.ntnu.messages.Message;

// TODO refactor this class. it does sooooo much

/**
 * A communication channel for the control panel. This class is responsible for
 * sending commands to the server and receiving responses. It also listens for
 * incoming events from the server.
 */
public class ControlPanelCommunicationChannel extends SocketCommunicationChannel implements CommunicationChannel {
  
  private final ControlPanelResponseHandler responseHandler;
  
  private final ControlPanelLogic logic;
  private String targetId = Endpoints.BROADCAST.getValue(); // Used to target a greenhouse node for sensor data requests
  

  /**
   * Create a communication channel for the control panel.
   * Initializes the communication channel with the specified logic, host, and port.
   *
   * @param logic The control panel logic
   * @param host  The server host
   * @param port  The server port
   */
  public ControlPanelCommunicationChannel(ControlPanelLogic logic, String host, int port) {
    super(host, port);
    this.logic = logic;
    this.responseHandler = new ControlPanelResponseHandler(this, this.logic);

    ClientIdentification clientIdentification = new ClientIdentification(Endpoints.CONTROL_PANEL, Endpoints.NOT_PREDEFINED.getValue());
    this.establishConnectionWithServer(clientIdentification);
  }

  /**
   * Handle a message received from the server.
   * Parses the server message and processes it based on the client type.
   *
   * @param serverMessage The message received from the server
   */
  @Override
  protected void handleMessage(Message message) {
    Logger.info("Received message from server: " + message);
    
    MessageHeader header = message.getHeader();
    MessageBody body = message.getBody();
    Endpoints client = header.getReceiver();
    
    this.responseHandler.handleResponse(client, body);
  }

  /**
   * Send an actuator change command to the server.
   * Constructs and sends a message to change the state of an actuator.
   *
   * @param nodeId     The ID of the node
   * @param actuatorId The ID of the actuator
   * @param isOn       The desired state of the actuator
   */
  @Override
  public void sendActuatorChange(int nodeId, int actuatorId, boolean isOn) {
    // TODO do not just catch exception. No clue what exception is caught.
    // Does creating the message cause an exception?
    // Does sending the message cause an exception?
    // By having both in try catch, seems like both can fail, but in reality probably only sending can fail.
    // Have custom exceptions.
    // don't use chatgpt or copilot preferably...
    try {
      String nodeIdStr = Integer.toString(nodeId);
      MessageHeader header = new MessageHeader(Endpoints.GREENHOUSE, nodeIdStr);
      MessageBody body = new MessageBody(new ActuatorChangeCommand(actuatorId, isOn));
      Message message = new Message(header, body);
      this.sendMessage(message);
    } catch (Exception e) {
      Logger.error("Failed to send actuator change: " + e.getMessage());
    }
  }

  /**
   * Set the target sensor node ID.
   *
   * @param targetId The target sensor node ID
   */
  public void setSensorNodeTarget(String targetId){
    this.targetId = targetId;
  }

  /**
   * Get the target sensor node ID.
   *
   * @return The target sensor node ID
   */
  public String getSensorNoderTarget(){
    return this.targetId;
  }

  /**
   * Request sensor data periodically.
   * Schedules periodic requests for sensor data from the server.
   *
   * @param period The period in seconds between requests
   */
  public void askForSensorDataPeriodically(int period) {
    ControlPanelCommunicationChannel self = this;

    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        MessageHeader header = new MessageHeader(Endpoints.GREENHOUSE, self.getSensorNoderTarget());
        MessageBody body = new MessageBody(new GetSensorDataCommand());
        Message message = new Message(header, body);
        if (self.isOn && !self.isReconnecting()) {
          self.sendMessage(message);
        }
        else{
          Logger.info("Unable to send message...");
        }
      }
    }, 5000, period * 1000L);
  }

  /**
   * Spawn a new sensor/actuator node information after a given delay.
   *
   * @param specification A (temporary) manual configuration of the node in the
   *                      following format
   *                      [nodeId] semicolon
   *                      [actuator_count_1] underscore [actuator_type_1] space
   *                      ... space
   *                      [actuator_count_M] underscore [actuator_type_M]
   */
  public void askForNodes() {
    // TODO do not just catch exception. No clue what exception is caught.
    // Does creating the message cause an exception?
    // Does sending the message cause an exception?
    // By having both in try catch, seems like both can fail, but in reality probably only sending can fail.
    // Have custom exceptions.
    // don't use chatgpt or copilot preferably...
    try {
      MessageHeader header = new MessageHeader(Endpoints.GREENHOUSE, Endpoints.BROADCAST.getValue());
      MessageBody body = new MessageBody(new GetNodeCommand());
      Message message = new Message(header, body);
      this.sendMessage(message);
    } catch (Exception e) {
      Logger.error("Failed to ask for nodes: " + e.getMessage());
    }
  }

  /**
   * Spawn a new sensor/actuator node information after a given delay.
   * Sends a command to the server to spawn a new node after a specified delay.
   *
   * @param nodeId The ID of the node to spawn
   * @param START_DELAY The delay in seconds before spawning the node
   */
  public void spawnNode(String nodeId, int START_DELAY) {
    try {
      MessageHeader header = new MessageHeader(Endpoints.GREENHOUSE, nodeId);
      MessageBody body = new MessageBody(new GetNodeCommand());
      Message message = new Message(header, body);
    //   TODO do differently
      this.sendMessage(message);
    } catch (Exception e) {
      Logger.error("Failed to spawn node: " + e.getMessage());
    }
  }
}
