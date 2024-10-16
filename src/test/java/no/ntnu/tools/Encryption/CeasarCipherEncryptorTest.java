package no.ntnu.tools.Encryption;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

public class CeasarCipherEncryptorTest {

    // All tests assume SHIFT = 3
    @Test
    public void testEncryptStringBasic() {
        String message = "abc";
        String expected = "def"; // Assuming SHIFT = 3
        assertEquals(expected, CeasarCipherEncryptor.encryptString(message));
    }

    @Test
    public void testEncryptStringMixedCase() {
        String message = "Hello World";
        String expected = "Khoor Zruog";
        assertEquals(expected, CeasarCipherEncryptor.encryptString(message));
    }

    @Test
    public void testEncryptStringWithSpecialCharacters() {
        String message = "Hello, World!";
        String expected = "Khoor, Zruog!";
        assertEquals(expected, CeasarCipherEncryptor.encryptString(message));
    }

    @Test
    public void testEncryptStringEmptyString() {
        String message = "";
        String expected = "";
        assertEquals(expected, CeasarCipherEncryptor.encryptString(message));
    }

    @Test
    public void testDecryptStringBasic() {
        String encryptedMessage = "def";
        String expected = "abc"; // Reversing the shift
        assertEquals(expected, CeasarCipherEncryptor.decryptString(encryptedMessage));
    }

    @Test
    public void testDecryptStrinMixedCase() {
        String encryptedMessage = "Khoor Zruog";
        String expected = "Hello World"; // Reversing the shift
        assertEquals(expected, CeasarCipherEncryptor.decryptString(encryptedMessage));
    }

    @Test
    public void testDecryptStringWithSpecialCharacters() {
        String encryptedMessage = "Khoor, Zruog!";
        String expected = "Hello, World!"; // Reversing the shift
        assertEquals(expected, CeasarCipherEncryptor.decryptString(encryptedMessage));
    }

    @Test
    public void testDecryptStringEmptyString() {
        String encryptedMessage = "";
        String expected = "";
        assertEquals(expected, CeasarCipherEncryptor.decryptString(encryptedMessage));
    }
}