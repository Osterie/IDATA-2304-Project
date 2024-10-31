package no.ntnu;

import no.ntnu.contpanel.MockControlPanel;
import no.ntnu.greenhouse.MockGreenhouseNode;
import no.ntnu.intermediaryserver.IntermediaryServer;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.MessageTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntermediaryServerTest {
  private static IntermediaryServer server;
  private static final int PORT = 50500;
  private static MockGreenhouseNode mockGreenhouseNode;
  private static MockControlPanel mockControlPanel;

  @BeforeAll
  public static void setUp() throws IOException {
    // Start the server
    server = new IntermediaryServer();
    Thread serverThread = new Thread(server);
    serverThread.start();

    // Start the mock greenhouse node
    mockGreenhouseNode = new MockGreenhouseNode("localhost", PORT);
    mockGreenhouseNode.sendCommand("GREENHOUSE;1");

    // Start the mock control panel
    mockControlPanel = new MockControlPanel("localhost", PORT);
    mockControlPanel.sendCommand("CONTROL_PANEL;2");
  }

  @AfterAll
  public static void tearDown() throws IOException {
    mockGreenhouseNode.close();
    mockControlPanel.close();
    server.stopServer();
  }

  @Test
  public void testControlPanelToGreenhouseNodeCommunication() throws IOException {

    MessageBody messageBody = new MessageBody("TURN_ON_FAN", "1");
    MessageHeader messageHeader = new MessageHeader(Clients.GREENHOUSE, "1");
    MessageTest message = new MessageTest(messageHeader, messageBody);

    String command = message.toProtocolString();

    mockControlPanel.sendCommand(command);

    String response = mockGreenhouseNode.receiveResponse();
    assertEquals("CONTROL_PANEL;2-TURN_ON_FAN;1", response);
  }

  @Test
  public void testGreenhouseNodeToControlPanelCommunication() throws IOException {
      MessageBody messageBody = new MessageBody("SENSOR_NODE", "1");
      MessageHeader messageHeader = new MessageHeader(Clients.CONTROL_PANEL, "2");
      MessageTest message = new MessageTest(messageHeader, messageBody);

    String command = message.toProtocolString();


    mockGreenhouseNode.sendCommand(command);


    String response = mockControlPanel.receiveResponse();
    assertEquals("GREENHOUSE;1-SENSOR_NODE;1", response);
  }
}