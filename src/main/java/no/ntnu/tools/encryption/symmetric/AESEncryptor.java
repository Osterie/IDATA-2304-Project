package no.ntnu.tools.encryption.symmetric;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * This class is used for encrypting and decrypting
 * string messages with AES.
 * 
 * Methods:
 * generateKey()
 * encryptString()
 * decryptString()
 * stringToKey()
 * keyToString()
 */
public class AESEncryptor {

    // String used to define algorithm type
    private static final String ALGORITHM = "AES";
    // Size of encryption key
    private static final int KEY_SIZE = 128;

    /**
     * Generates a new AES secret key.
     *
     * This key is used when encrypting and
     * decrypting messages.
     *
     * @return key.
     */
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(KEY_SIZE);

        return keyGenerator.generateKey();
    }

    /**
     * Encrypts a string message using AES and
     * returns the encrypted string in Base64 format.
     *
     * @return encrypted string.
     */
    public static String encryptString(String message, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedBytes = cipher.doFinal(message.getBytes());

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Decrypts an AES-encrypted string (in Base64 format)
     * and returns the original message.
     *
     * @return decrypted message.
     */
    public static String decryptString(String encryptedMessage, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] decodedBytes = Base64.getDecoder().decode(encryptedMessage);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);

        return new String(decryptedBytes);
    }

    /**
     * Converts a secret key into a Base64 encoded string.
     * Used for storing or sharing the key.
     *
     * @return key as String.
     */
    public static String keyToString(SecretKey secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * Converts a Base64 encoded string back to a secret key.
     * Used for retrieving a stored key.
     *
     * @return key as SecretKey.
     */
    public static SecretKey stringToKey(String keyString) {
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
    }
}
