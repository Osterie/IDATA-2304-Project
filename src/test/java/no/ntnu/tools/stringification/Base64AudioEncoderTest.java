package no.ntnu.tools.stringification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import static org.junit.jupiter.api.Assertions.*;

public class Base64AudioEncoderTest {

    private File tempAudioFile;

    @BeforeEach
    public void setUp() throws IOException {
        // Create a temporary audio file for testing
        tempAudioFile = File.createTempFile("testAudio", ".wav");
        try (FileOutputStream fos = new FileOutputStream(tempAudioFile)) {
            fos.write(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        }
    }

    @AfterEach
    public void tearDown() {
        // Delete the temporary audio file after each test
        if (tempAudioFile.exists()) {
            tempAudioFile.delete();
        }
    }

    @Test
    public void testAudioToString() throws IOException {
        String base64String = Base64AudioEncoder.audioToString(tempAudioFile);
        assertNotNull(base64String);
        assertFalse(base64String.isEmpty());
    }

    @Test
    public void testStringToAudio() throws IOException {
        String base64String = Base64AudioEncoder.audioToString(tempAudioFile);
        File decodedFile = Base64AudioEncoder.stringToAudio(base64String);

        assertNotNull(decodedFile);
        assertTrue(decodedFile.exists());
        assertTrue(decodedFile.length() > 0);

        byte[] originalBytes = Files.readAllBytes(tempAudioFile.toPath());
        byte[] decodedBytes = Files.readAllBytes(decodedFile.toPath());

        assertArrayEquals(originalBytes, decodedBytes);

        // Clean up the decoded file
        decodedFile.delete();
    }

    @Test
    public void testAudioToStringWithNullFile() {
        assertThrows(IllegalArgumentException.class, () -> {
            Base64AudioEncoder.audioToString(null);
        });
    }
}
