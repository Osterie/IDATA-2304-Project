package no.ntnu.messages;

import no.ntnu.greenhouse.NodeLogic;
import no.ntnu.messages.commands.greenhouse.GetSensorDataCommand;
import no.ntnu.messages.MessageHeader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class GetSensorDataCommandTest {

    private NodeLogic nodeLogic;
    private GetSensorDataCommand command;

    @BeforeEach
    public void setUp() {
        nodeLogic = mock(NodeLogic.class);
        command = new GetSensorDataCommand();
    }

    @Test
    public void testExecute() {
        MessageHeader header = mock(MessageHeader.class);
        when(nodeLogic.getSensorData()).thenReturn("Temperature=25;Humidity=60");

        Message result = command.execute(nodeLogic, header);

        assertEquals("Temperature=25;Humidity=60", result.getBody().getTransmission().getTransmissionString());
    }

    @Test
    public void testToProtocolString() {
        assertEquals("GET_SENSOR_DATA", command);
    }
}
