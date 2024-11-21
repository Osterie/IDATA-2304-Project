package no.ntnu.tools.messages;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import no.ntnu.messages.Delimiters;

public class DelimitersTest {

    @Test
    public void testDelimiterValues() {
        assertEquals("-", Delimiters.HEADER_BODY.getValue());
        assertEquals(";", Delimiters.HEADER_FIELD.getValue());
        assertEquals(";", Delimiters.BODY_FIELD.getValue());
    }

    @Test
    public void testFromString() {
        assertEquals(Delimiters.HEADER_FIELD, Delimiters.fromString(";"));
        assertNull(Delimiters.fromString("invalid"));
    }
}