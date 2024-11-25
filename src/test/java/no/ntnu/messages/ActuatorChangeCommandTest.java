package no.ntnu.messages;

import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.greenhouse.SensorActuatorNode;
import no.ntnu.messages.commands.greenhouse.ActuatorChangeCommand;
import no.ntnu.messages.MessageHeader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ActuatorChangeCommandTest {

    private NodeLogic nodeLogic;
    private SensorActuatorNode node;
    private ActuatorChangeCommand command;

    @BeforeEach
    public void setUp() {
        nodeLogic = mock(NodeLogic.class);
        node = mock(SensorActuatorNode.class);
        when(nodeLogic.getNode()).thenReturn(node);
        command = new ActuatorChangeCommand();
    }

    @Test
    public void testExecute() {
        MessageHeader header = mock(MessageHeader.class);
        command.setParameters(new String[]{"1", "ON"});

        Message result = command.execute(nodeLogic, header);

        verify(node, times(1)).setActuator(1, true);
        assertEquals("1;ON", result.getBody().getTransmission().getTransmissionString());
    }

    @Test
    public void testToProtocolString() {
        command.setParameters(new String[]{"1", "ON"});
        assertEquals("ACTUATOR_CHANGE;1;ON", command.toProtocolString());
    }
}
