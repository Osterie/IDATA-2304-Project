package no.ntnu.messages;

import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.greenhouse.SensorActuatorNode;
import no.ntnu.messages.commands.greenhouse.TurnOffAllActuatorInNodeCommand;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;



public class TurnOffAllActuatorInNodeCommandTest {

    private NodeLogic nodeLogic;
    private SensorActuatorNode node;
    private TurnOffAllActuatorInNodeCommand command;

    @BeforeEach
    public void setUp() {
        // Makes mock objects of node logic
        nodeLogic = mock(NodeLogic.class);
        node = mock(SensorActuatorNode.class);
        // When the nodeLogic.getNode() is called, it returns the node
        when(nodeLogic.getNode()).thenReturn(node);
        command = new TurnOffAllActuatorInNodeCommand();
    }

    // @Test
    // public void testExecute() {
    //     Message result = command.execute(nodeLogic);
    //     // Verifies that the setAllActuators method is called once by the node
    //     verify(node, times(1)).setAllActuators(false);
    //     assertEquals("TURN_ON_ALL_ACTUATORS_SUCCESS", result.getBody().getTransmission().getTransmissionString());
    //     assertEquals("TURN_OFF_ALL_ACTUATORS", result.getHeader().getDataType());
    // }

    @Test
    public void testToProtocolString() {
        assertEquals("TURN_OFF_ALL_ACTUATORS", command);
    }
}