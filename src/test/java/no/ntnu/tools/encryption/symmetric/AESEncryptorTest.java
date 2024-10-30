package no.ntnu.tools.encryption.symmetric;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.crypto.SecretKey;
import static org.junit.jupiter.api.Assertions.*;

public class AESEncryptorTest {

    private SecretKey secretKey;
    private String originalMessage;

    @BeforeEach
    public void setUp() throws Exception {
        // Initialize a secret key and a sample message before each test
        secretKey = AESEncryptor.generateKey();
        originalMessage = "This is a test message";
    }

    @Test
    public void testGenerateKey() throws Exception {
        // Ensure that the key is not null and has the correct algorithm
        assertNotNull(secretKey, "Secret key should not be null");
        assertEquals("AES", secretKey.getAlgorithm(), "Key algorithm should be AES");
    }

    @Test
    public void testEncryptAndDecrypt() throws Exception {
        // Encrypt the original message
        String encryptedMessage = AESEncryptor.encryptString(originalMessage, secretKey);
        assertNotNull(encryptedMessage, "Encrypted message should not be null");
        assertNotEquals(originalMessage, encryptedMessage, "Encrypted message should be different from original");

        // Decrypt the encrypted message
        String decryptedMessage = AESEncryptor.decryptString(encryptedMessage, secretKey);
        assertNotNull(decryptedMessage, "Decrypted message should not be null");
        assertEquals(originalMessage, decryptedMessage, "Decrypted message should match the original message");
    }

    @Test
    public void testKeyToStringAndStringToKey() throws Exception {
        // Convert the key to a string
        String keyString = AESEncryptor.keyToString(secretKey);
        assertNotNull(keyString, "Key string should not be null");

        // Convert the string back to a key
        SecretKey decodedKey = AESEncryptor.stringToKey(keyString);
        assertNotNull(decodedKey, "Decoded key should not be null");
        assertEquals(secretKey, decodedKey, "Decoded key should match the original key");
    }

    @Test
    public void testDecryptWithDifferentKey() throws Exception {
        // Generate a different key
        SecretKey differentKey = AESEncryptor.generateKey();

        // Encrypt the message using the original key
        String encryptedMessage = AESEncryptor.encryptString(originalMessage, secretKey);

        // Try to decrypt using a different key
        // This should fail
        assertThrows(Exception.class, () -> {
            AESEncryptor.decryptString(encryptedMessage, differentKey);
        }, "Decryption with a different key should throw an exception");
    }
}