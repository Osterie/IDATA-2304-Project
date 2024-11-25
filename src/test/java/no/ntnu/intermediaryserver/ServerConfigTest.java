package no.ntnu.intermediaryserver;

import org.junit.jupiter.api.*;

import no.ntnu.intermediaryserver.server.PortNumberOutOfRangeException;
import no.ntnu.intermediaryserver.server.ServerConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ServerConfig class.
 * 
 * This test class verifies the behavior of the ServerConfig class, including:
 * - Backing up and restoring the original configuration file before and after tests.
 * - Ensuring the default port is used when no valid configuration file exists.
 * - Setting and retrieving a valid port number.
 * - Incrementing the port number when the specified port is already in use.
 * - Handling invalid port numbers gracefully.
 * 
 * The configuration file path is specified as "config/server_config.txt".
 * 
 * Tests:
 * - testDefaultPort: Verifies that the default port (50500) is used when no valid configuration file exists.
 * - testSetPort: Verifies that a valid port number (50510) can be set and retrieved.
 * - testIncrementPort: Verifies that the port number is incremented when the specified port (50510) is in use.
 * - testInvalidPortHandling: Verifies that an invalid port number (70000) is handled gracefully, throwing a PortNumberOutOfRangeException.
 * 
 * Helper Methods:
 * - backupOriginalConfig: Backs up the original configuration file contents before all tests.
 * - restoreOriginalConfig: Restores the original configuration file contents after all tests.
 * - deleteConfigFile: Deletes the configuration file if it exists.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ServerConfigTest {

    private static final String CONFIG_FILE = "config/server_config.txt";
    private static String originalConfigContents;

    /**
     * Backs up the original configuration file contents before any tests are run.
     * If the configuration file exists, its contents are read and stored in the 
     * originalConfigContents variable. If the file does not exist, 
     * originalConfigContents is set to null.
     * 
     * If an IOException occurs during the process, the test will fail with an 
     * appropriate error message.
     */
    @BeforeAll
    void backupOriginalConfig() {
        try {
            Path configPath = Paths.get(CONFIG_FILE);
            if (Files.exists(configPath)) {
                originalConfigContents = Files.readString(configPath); // Backup original contents
            } else {
                originalConfigContents = null; // No original config file
            }
        } catch (IOException e) {
            fail("Failed to back up original config file: " + e.getMessage());
        }
    }

    /**
     * Restores the original configuration file after all tests have been executed.
     * If the original configuration contents were saved, it writes them back to the configuration file.
     * If the original configuration file did not exist, it deletes the configuration file.
     * 
     * This method is annotated with @AfterAll, meaning it will be executed once after all tests in the class have run.
     * 
     * @throws IOException if an I/O error occurs while writing to or deleting the configuration file.
     */
    @AfterAll
    void restoreOriginalConfig() {
        try {
            Path configPath = Paths.get(CONFIG_FILE);
            if (originalConfigContents != null) {
                Files.writeString(configPath, originalConfigContents); // Restore original contents
            } else {
                Files.deleteIfExists(configPath); // Remove the file if it didn't originally exist
            }
        } catch (IOException e) {
            System.err.println("Failed to restore original config file: " + e.getMessage());
        }
    }

    /**
     * Tests that the default port number is used when no valid configuration file exists.
     * This test deletes the configuration file and then retrieves the port number from
     * the ServerConfig class. It asserts that the port number is the expected default value of 50500.
     */
    @Test
    @DisplayName("Default port is used when no valid file exists")
    void testDefaultPort() {
        deleteConfigFile();
        int port = ServerConfig.getPortNumber();
        assertEquals(50500, port, "Expected default port 50500");
    }

    /**
     * Test case for setting and retrieving a valid port number.
     * 
     * This test sets the port number to 50510 using the ServerConfig.setPortNumber method,
     * and then retrieves the port number using the ServerConfig.getPortNumber method.
     * It asserts that the retrieved port number is equal to the expected value of 50510.
     */
    @Test
    @DisplayName("Set and retrieve a valid port number")
    void testSetPort() {
        ServerConfig.setPortNumber(50510);
        int port = ServerConfig.getPortNumber();
        assertEquals(50510, port, "Expected port 50510");
    }

    /**
     * Test to verify that the port number is incremented when the specified port is already in use.
     * 
     * This test creates a server socket on port 50510 to simulate the port being in use.
     * It then sets the port number in the ServerConfig to 50510, which should cause the port number
     * to increment to 50511. The test asserts that the port number is correctly incremented.
     * 
     * @throws IOException if the test socket cannot be created
     */
    @Test
    @DisplayName("Increment port when the specified port is in use")
    void testIncrementPort() {
        try (var socket = new java.net.ServerSocket(50510)) {
            ServerConfig.setPortNumber(50510); // Should increment to 50511
            int port = ServerConfig.getPortNumber();
            assertEquals(50511, port, "Expected port 50511 when 50510 is in use");
        } catch (IOException e) {
            fail("Failed to create test socket: " + e.getMessage());
        }
    }

    /**
     * Tests the handling of an invalid port number in the ServerConfig class.
     * 
     * This test verifies that attempting to set an invalid port number (e.g., 70000)
     * throws a PortNumberOutOfRangeException and that the exception message contains
     * the invalid port number. Additionally, it checks that the port number remains
     * within the valid range (0-65535) after the invalid set attempt.
     * 
     * @throws PortNumberOutOfRangeException if the port number is out of the valid range
     */
    @Test
    @DisplayName("Handle invalid port gracefully")
    void testInvalidPortHandling() {
        Exception exception = assertThrows(PortNumberOutOfRangeException.class, () -> {
            ServerConfig.setPortNumber(70000); // Invalid port
        });

        assertTrue(exception.getMessage().contains("70000"), "Expected exception message to contain the invalid port number");

        int port = ServerConfig.getPortNumber();
        assertTrue(port >= 0 && port <= 65535, "Port should remain valid after an invalid port set attempt");
    }

    /**
     * Deletes the configuration file if it exists.
     * If an IOException occurs during the deletion process,
     * the test will fail with an appropriate error message.
     */
    private void deleteConfigFile() {
        try {
            Files.deleteIfExists(Paths.get(CONFIG_FILE));
        } catch (IOException e) {
            fail("Failed to delete config file: " + e.getMessage());
        }
    }
}
