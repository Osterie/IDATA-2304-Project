package no.ntnu.tools.messages;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import no.ntnu.messages.MessageHeader;

public class MessageHeaderTest {

    @Test
    public void testInvalidProtocolString() {
        assertThrows(IllegalArgumentException.class, () -> {
            MessageHeader.fromProtocolString("CLIENT_ONLY");
        });
    }
}