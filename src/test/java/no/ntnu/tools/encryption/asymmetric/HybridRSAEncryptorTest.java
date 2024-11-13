package no.ntnu.tools.encryption.asymmetric;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class HybridRSAEncryptorTest {

    private static KeyPair rsaKeyPair;
    private static SecretKey aesKey;
    private static final String MESSAGE = "This is a secure message";

    @BeforeAll
    static void setup() throws Exception {
        // Generate RSA key pair and AES key for testing
        rsaKeyPair = HybridRSAEncryptor.generateRSAKeyPair();
        aesKey = HybridRSAEncryptor.generateAESKey();
    }

    @Test
    void testGenerateAESKey() {
        assertNotNull(aesKey, "AES key should not be null");
    }

    @Test
    void testGenerateRSAKeyPair() {
        assertNotNull(rsaKeyPair, "RSA key pair should not be null");
        assertNotNull(rsaKeyPair.getPublic(), "RSA public key should not be null");
        assertNotNull(rsaKeyPair.getPrivate(), "RSA private key should not be null");
    }

    @Test
    void testEncryptWithAES() throws Exception {
        String encryptedMessage = HybridRSAEncryptor.encryptWithAES(MESSAGE, aesKey);
        assertNotNull(encryptedMessage, "Encrypted message should not be null");
        assertNotEquals(MESSAGE, encryptedMessage, "Encrypted message should differ from original message");
    }

    @Test
    void testDecryptWithAES() throws Exception {
        String encryptedMessage = HybridRSAEncryptor.encryptWithAES(MESSAGE, aesKey);
        String decryptedMessage = HybridRSAEncryptor.decryptWithAES(encryptedMessage, aesKey);
        assertEquals(MESSAGE, decryptedMessage, "Decrypted message should match the original message");
    }

    @Test
    void testEncryptAESKeyWithRSA() throws Exception {
        String encryptedAESKey = HybridRSAEncryptor.encryptAESKeyWithRSA(aesKey, rsaKeyPair.getPublic());
        assertNotNull(encryptedAESKey, "Encrypted AES key should not be null");
        assertNotEquals(Base64.getEncoder().encodeToString(aesKey.getEncoded()), encryptedAESKey,
                "Encrypted AES key should differ from original AES key");
    }

    @Test
    void testDecryptAESKeyWithRSA() throws Exception {
        // Encrypt and then decrypt the AES key with RSA
        String encryptedAESKey = HybridRSAEncryptor.encryptAESKeyWithRSA(aesKey, rsaKeyPair.getPublic());
        SecretKey decryptedAESKey = HybridRSAEncryptor.decryptAESKeyWithRSA(encryptedAESKey, rsaKeyPair.getPrivate());

        assertArrayEquals(aesKey.getEncoded(), decryptedAESKey.getEncoded(),
                "Decrypted AES key should match the original AES key");
    }

    @Test
    void testFullEncryptionDecryptionCycle() throws Exception {
        // Encrypt the message with AES
        String encryptedMessage = HybridRSAEncryptor.encryptWithAES(MESSAGE, aesKey);

        // Encrypt the AES key with RSA
        String encryptedAESKey = HybridRSAEncryptor.encryptAESKeyWithRSA(aesKey, rsaKeyPair.getPublic());

        // Decrypt the AES key
        SecretKey decryptedAESKey = HybridRSAEncryptor.decryptAESKeyWithRSA(encryptedAESKey, rsaKeyPair.getPrivate());

        // Decrypt the message with AES
        String decryptedMessage = HybridRSAEncryptor.decryptWithAES(encryptedMessage, decryptedAESKey);

        assertEquals(MESSAGE, decryptedMessage, "Decrypted message should match the original message");
    }
}