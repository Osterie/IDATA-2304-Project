package no.ntnu.tools.messages;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import no.ntnu.messages.MessageBody;

public class MessageBodyTest {

    // TODO: Add exception handler in MessageBody.

    // @Test
    // public void testToProtocolString() {
    //     MessageBody message = new MessageBody("command", "data");
    //     assertEquals("command;" + "data", message);

    //     MessageBody noDataMessage = new MessageBody("command");
    //     assertEquals("command", noDataMessage);
    // }

    @Test
    public void testfromString() {
        MessageBody message = MessageBody.fromString("command;data");
        assertEquals("command", message.getTransmission());
        assertEquals("data", message.getTransmissionString());

        MessageBody noDataMessage = MessageBody.fromString("command");
        assertEquals("command", noDataMessage.getTransmission());
        assertEquals("", noDataMessage.getTransmissionString());
    }

    @Test
    public void testInvalidProtocolString() {
        assertThrows(IllegalArgumentException.class, () -> {
            MessageBody.fromString("");
        });
    }
}