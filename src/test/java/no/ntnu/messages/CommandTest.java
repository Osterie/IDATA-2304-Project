package no.ntnu.messages;

import no.ntnu.Clients;
import no.ntnu.contpanel.MockControlPanel;
import no.ntnu.greenhouse.MockGreenhouseNode;
import no.ntnu.intermediaryserver.IntermediaryServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for the Command class.
 * This class tests the creation of a command.
 */
public class CommandTest {
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


  /**
   * Test the creation of a command when the body is not null.
   */
  @Test
  public void testCreatingCommandWithNullBody() throws IOException {
    MessageHeader messageHeader = new MessageHeader(Clients.CONTROL_PANEL, "2");
    Message message = new Message(messageHeader, null);

    try {
      String command = message.toProtocolString();
    } catch (IllegalArgumentException e) {
      assertEquals("Header and body cannot be null", e.getMessage());
    }
  }

  // /**
  //  * Test the creation of a command when the header is null.
  //  */
  // @Test
  // public void testCreatingCommandWithNullHeader() throws IOException {
  //   MessageBody messageBody = new MessageBody("TURN_ON_FAN", "1");
  //   Message message = new Message(null, messageBody);

  //   try {
  //     String command = message.toProtocolString();
  //   } catch (IllegalArgumentException e) {
  //     assertEquals("Header and body cannot be null", e.getMessage());
  //   }
  // }


}
