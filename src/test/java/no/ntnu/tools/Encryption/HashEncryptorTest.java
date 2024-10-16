package no.ntnu.tools.Encryption;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class HashEncryptorTest {

    @Test
    public void testEncryptString() {
        // Test data
        String message = "testMessage";
        String sameMessage = "testMessage";
        String differentMessage = "differentMessage";

        // Encrypt the message
        String hash1 = HashEncryptor.encryptString(message);
        String hash2 = HashEncryptor.encryptString(sameMessage);
        String hash3 = HashEncryptor.encryptString(differentMessage);

        // Check that the hash is not null and not empty
        assertNotNull(hash1, "Hash should not be null");
        assertFalse(hash1.isEmpty(), "Hash should not be empty");

        // Hashes for the same message should be identical
        assertEquals(hash1, hash2, "Hash for the same message should be the same");

        // Hashes for different messages should be different
        assertNotEquals(hash1, hash3, "Hash for different messages should not be the same");
    }

    @Test
    public void testHashConsistency() {
        // Test to ensure that the same input always produces the same hash
        String message = "consistentMessage";

        String hash1 = HashEncryptor.encryptString(message);
        String hash2 = HashEncryptor.encryptString(message);

        // Hashes should be identical for the same input
        assertEquals(hash1, hash2, "Hash should be consistent for the same input");
    }

    @Test
    public void testHashIrreversibility() {
        // Test to ensure that hashes cannot be reversed to the original message
        String message = "irreversibleMessage";
        String hash = HashEncryptor.encryptString(message);

        // Since hashing is one-way, it is impossible to reverse it,
        // so we assert that the hash is not equal to the original message
        assertNotEquals(message, hash, "Hash should not be the same as the original message");
    }

    @Test
    public void testEmptyStringHash() {
        // Test hashing of an empty string
        String emptyMessage = "";
        String emptyHash = HashEncryptor.encryptString(emptyMessage);

        // Hash of an empty string should not be null or empty
        assertNotNull(emptyHash, "Hash of an empty string should not be null");
        assertFalse(emptyHash.isEmpty(), "Hash of an empty string should not be empty");
    }

    @Test
    public void testNullInput() {
        // Test handling of null input
        // This should send an exception
        assertThrows(NullPointerException.class, () -> {
            HashEncryptor.encryptString(null);
        }, "Hashing null input should throw a NullPointerException");
    }
}
