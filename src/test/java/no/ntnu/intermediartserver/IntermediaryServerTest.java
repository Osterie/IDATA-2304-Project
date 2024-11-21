package no.ntnu.intermediartserver;

import no.ntnu.constants.CommandConstants;
import no.ntnu.constants.Endpoints;
import no.ntnu.contpanel.MockControlPanel;
import no.ntnu.greenhouse.MockGreenhouseNode;
import no.ntnu.intermediaryserver.IntermediaryServer;
import no.ntnu.messages.MessageBody;
import no.ntnu.messages.MessageHeader;
import no.ntnu.messages.Message;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static no.ntnu.constants.CommandConstants.PORT_NUMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for the IntermediaryServer class.
 * This class tests the communication between the control panel and the greenhouse node.
 */
public class IntermediaryServerTest {
  private IntermediaryServer server;
  private static final int PORT = PORT_NUMBER.getIntValue();
  private MockGreenhouseNode mockGreenhouseNode;
  private MockControlPanel mockControlPanel;

  @BeforeEach
  public void setUp() throws IOException, InterruptedException {
    // Start the server
    server = new IntermediaryServer();
    Thread serverThread = new Thread(server);
    serverThread.start();

    // Wait for the server to start
    Thread.sleep(10);

    // Start the mock greenhouse node
    mockGreenhouseNode = new MockGreenhouseNode("localhost", PORT);
    mockGreenhouseNode.sendCommand("GREENHOUSE;1");

    // Start the mock control panel
    mockControlPanel = new MockControlPanel("localhost", PORT);
    mockControlPanel.sendCommand("CONTROL_PANEL;2");
  }

  @AfterEach
  public void tearDown() throws IOException {
    mockGreenhouseNode.close();
    mockControlPanel.close();
    server.stopServer();
  }

  // /**
  //  * Test the communication between the control panel and the greenhouse node.
  //  * The control panel sends a command to the greenhouse node, and the greenhouse node responds.
  //  */
  // @Test
  // public void testControlPanelToGreenhouseNodeCommunication() throws IOException {

  //   MessageBody messageBody = new MessageBody("TURN_ON_FAN", "1");
  //   MessageHeader messageHeader = new MessageHeader(Clients.GREENHOUSE, "1");
  //   Message message = new Message(messageHeader, messageBody);

  //   String command = message.toProtocolString();

  //   mockControlPanel.sendCommand(command);

  //   String response = mockGreenhouseNode.receiveResponse();
  //   assertEquals("CONTROL_PANEL;2-TURN_ON_FAN;1", response);
  // }

  // /**
  //  * Test the communication between the greenhouse node and the control panel.
  //  * The greenhouse node sends a command to the control panel, and the control panel responds.
  //  */
  // @Test
  // public void testGreenhouseNodeToControlPanelCommunication() throws IOException {
  //   MessageBody messageBody = new MessageBody("SENSOR_NODE", "1");
  //   MessageHeader messageHeader = new MessageHeader(Clients.CONTROL_PANEL, "2");
  //   Message message = new Message(messageHeader, messageBody);

  //   String command = message.toProtocolString();


  //   mockGreenhouseNode.sendCommand(command);


  //   String response = mockControlPanel.receiveResponse();
  //   assertEquals("GREENHOUSE;1-SENSOR_NODE;1", response);
  // }


  // /**
  //  * Test the communication between the control panel and the greenhouse node with not valid client.
  //  */
  //   @Test
  //   public void testControlPanelToGreenhouseNodeCommunicationWithInvalidClient() throws IOException {

  //       MessageBody messageBody = new MessageBody("INVALID_COMMAND", "1");
  //       MessageHeader messageHeader = new MessageHeader(Clients.GREENHOUSE, "1");
  //       Message message = new Message(messageHeader, messageBody);

  //       String command = message.toProtocolString();

  //       try {
  //           mockControlPanel.sendCommand(command);
  //       } catch (IllegalArgumentException e) {
  //           assertEquals("Invalid client type", e.getMessage());
  //       }
  //   }

  // /**
  //  * Test the communication between the greenhouse node and the control panel with not valid client.
  //  * The greenhouse node sends a command to the control panel, and the control panel responds.
  //  */
  // @Test
  // public void testGreenhouseNodeToControlPanelCommunicationWithInvalidClient() throws IOException {
  //     MessageBody messageBody = new MessageBody("INVALID_COMMAND", "1");
  //     MessageHeader messageHeader = new MessageHeader(Clients.CONTROL_PANEL, "2");
  //     Message message = new Message(messageHeader, messageBody);

  //     String command = message.toProtocolString();

  //     try {
  //         mockGreenhouseNode.sendCommand(command);
  //     } catch (IllegalArgumentException e) {
  //         assertEquals("Invalid client type", e.getMessage());
  //     }
  // }


  // /**
  //  * Test the creation of a command when the data in body is null.
  //  */
  // @Test
  // public void testCreateMessageWithNullData() throws IOException {
  //   MessageBody messageBody = new MessageBody("TURN_ON_FAN", null);
  //   MessageHeader messageHeader = new MessageHeader(Clients.CONTROL_PANEL, "2");
  //   Message message = new Message(messageHeader, messageBody);

  //   try {
  //     String command = message.toProtocolString();
  //     mockGreenhouseNode.sendCommand(command);
  //   } catch (IllegalArgumentException e) {
  //     assertEquals("Invalid client type", e.getMessage());
  //   }
  // }


  /**
   * Test the creation of a command when the data in body is null.
   */
  @Test
  public void testCreateMessageWithNullCommand() throws IOException {
    MessageBody messageBody = new MessageBody(null, "1");
    MessageHeader messageHeader = new MessageHeader(Endpoints.CONTROL_PANEL, "2");
    Message message = new Message(messageHeader, messageBody);

    try {
      String command = message.toProtocolString();
      mockGreenhouseNode.sendCommand(command);
    } catch (IllegalArgumentException e) {
      assertEquals("Data type cannot be null", e.getMessage());
    }
  }

}