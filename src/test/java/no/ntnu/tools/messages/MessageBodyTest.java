package no.ntnu.tools.messages;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import no.ntnu.messages.MessageBody;

public class MessageBodyTest {

    // TODO: Add exception handler in MessageBody.

    // @Test
    // public void testToProtocolString() {
    //     MessageBody message = new MessageBody("command", "data");
    //     assertEquals("command;" + "data", message.toProtocolString());

    //     MessageBody noDataMessage = new MessageBody("command");
    //     assertEquals("command", noDataMessage.toProtocolString());
    // }

    @Test
    public void testFromProtocolString() {
        MessageBody message = MessageBody.fromProtocolString("command;data");
        assertEquals("command", message.getCommand());
        assertEquals("data", message.getData());

        MessageBody noDataMessage = MessageBody.fromProtocolString("command");
        assertEquals("command", noDataMessage.getCommand());
        assertEquals("", noDataMessage.getData());
    }

    @Test
    public void testInvalidProtocolString() {
        assertThrows(IllegalArgumentException.class, () -> {
            MessageBody.fromProtocolString("");
        });
    }
}