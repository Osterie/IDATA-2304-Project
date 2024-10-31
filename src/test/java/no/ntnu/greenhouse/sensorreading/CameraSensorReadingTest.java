package no.ntnu.greenhouse.sensorreading;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CameraSensorReadingTest {

    @TempDir
    Path tempDir;

    private File validImageFile;
    private File invalidImageFile;

    @BeforeEach
    void setUp() throws IOException {
        // Create a valid image file
        validImageFile = tempDir.resolve("validImage.png").toFile();
        Files.write(validImageFile.toPath(), new byte[]{(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n'});

        // Create an invalid image file
        invalidImageFile = tempDir.resolve("invalidImage.txt").toFile();
        Files.write(invalidImageFile.toPath(), "This is not an image".getBytes());
    }

    @Test
    void testValidImagePath() {
        CameraSensorReading reading = new CameraSensorReading("camera", validImageFile.getAbsolutePath());
        assertEquals(validImageFile.getAbsolutePath(), reading.getImage());
    }

    @Test
    void testInvalidImagePath() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new CameraSensorReading("camera", invalidImageFile.getAbsolutePath());
        });
        assertTrue(exception.getMessage().contains("The provided file is not a valid image"));
    }

    @Test
    void testNonExistentImagePath() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new CameraSensorReading("camera", tempDir.resolve("nonExistentImage.png").toString());
        });
        assertTrue(exception.getMessage().contains("The provided path does not exist or is not a file"));
    }

    @Test
    void testReadingAsString() {
        CameraSensorReading reading = new CameraSensorReading("camera", validImageFile.getAbsolutePath());
        String base64String = reading.readingAsString();
        assertNotNull(base64String);
        assertFalse(base64String.isEmpty());
    }
}