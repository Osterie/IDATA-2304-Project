package no.ntnu.messages;

import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.messages.commands.greenhouse.GetNodeIdCommand;
import no.ntnu.messages.MessageHeader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class GetNodeIdCommandTest {

    private NodeLogic nodeLogic;
    private GetNodeIdCommand command;

    @BeforeEach
    public void setUp() {
        nodeLogic = mock(NodeLogic.class);
        command = new GetNodeIdCommand();
    }

    @Test
    public void testExecute() {
        MessageHeader header = mock(MessageHeader.class);
        when(nodeLogic.getId()).thenReturn(456);

        Message result = command.execute(nodeLogic, header);

        assertEquals("456", result.getBody().getTransmission().getTransmissionString());
    }

    @Test
    public void testToProtocolString() {
        assertEquals("GET_NODE_ID", command);
    }
}
