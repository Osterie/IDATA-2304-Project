package no.ntnu.messages;

import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.ActuatorCollection;
import no.ntnu.messages.commands.greenhouse.GetNodeCommand;
import no.ntnu.messages.MessageHeader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Arrays;

public class GetNodeCommandTest {

    private NodeLogic nodeLogic;
    private ActuatorCollection actuators;
    private GetNodeCommand command;

    @BeforeEach
    public void setUp() {
        nodeLogic = mock(NodeLogic.class);
        actuators = mock(ActuatorCollection.class);
        when(nodeLogic.getNode().getActuators()).thenReturn(actuators);
        command = new GetNodeCommand();
    }

    @Test
    public void testExecute() {
        MessageHeader header = mock(MessageHeader.class);
        Actuator actuator1 = new Actuator("TypeA", 1);
        Actuator actuator2 = new Actuator("TypeB", 2);

        when(actuators.iterator()).thenReturn(Arrays.asList(actuator1, actuator2).iterator());
        when(nodeLogic.getId()).thenReturn(123);

        Message result = command.execute(nodeLogic, header);

        assertEquals("123;TypeA_1;TypeB_2", result.getBody().getTransmission().getTransmissionString());
    }

    @Test
    public void testToProtocolString() {
        assertEquals("GET_NODE", command.toProtocolString());
    }
}
