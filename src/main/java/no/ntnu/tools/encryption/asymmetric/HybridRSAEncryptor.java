package no.ntnu.tools.encryption.asymmetric;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

/**
 * The RSAEncryptor class provides a secure hybrid encryption approach combining both
 * symmetric AES encryption and asymmetric RSA encryption. This design allows for
 * efficient encryption and decryption of large data while securely exchanging
 * symmetric keys using RSA.
 *
 * <p>This class includes methods for:
 * - Generating AES and RSA keys
 * - Encrypting and decrypting messages using AES
 * - Encrypting and decrypting AES keys using RSA
 *
 * <p>Usage flow:
 * 1. Generate an AES key for encrypting a message.
 * 2. Encrypt the message using AES encryption.
 * 3. Encrypt the AES key using RSA with the recipient's public key.
 * 4. Send both the encrypted message and the encrypted AES key to the recipient.
 * 5. The recipient decrypts the AES key using their private RSA key.
 * 6. The recipient decrypts the message using the decrypted AES key.
 *
 * <p>This hybrid method provides the speed of AES encryption for large messages
 * and the security of RSA for key exchange.
 *
 * <p>Note: This class uses 256-bit AES encryption and 2048-bit RSA keys.
 * Ensure your environment has JCE Unlimited Strength installed for AES-256.
 */
public class HybridRSAEncryptor {
    private static final int AES_KEY_SIZE = 256;  // or 128, 192 depending on your needs
    private static final int RSA_KEY_SIZE = 2048;

    /**
     * Generates a new AES key for symmetric encryption.
     *
     * @return A SecretKey for AES encryption.
     * @throws Exception if an error occurs during key generation.
     */
    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        // Initializes key generator with specified key size
        keyGen.init(AES_KEY_SIZE);

        return keyGen.generateKey();
    }

    /**
     * Encrypts a message with AES using the provided AES key.
     *
     * @param message The plaintext message to encrypt.
     * @param aesKey The AES key for encryption.
     * @return The encrypted message as a Base64 encoded string.
     * @throws Exception if encryption fails.
     */
    public static String encryptWithAES(String message, SecretKey aesKey) throws Exception {
        Cipher aesCipher = Cipher.getInstance("AES");
        // Initializes Cipher with AES key
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);

        // Encrypted message as bytes
        byte[] encryptedBytes = aesCipher.doFinal(message.getBytes());

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Encrypts the AES key using RSA with the recipient's public key.
     *
     * @param aesKey The AES key to encrypt.
     * @param publicKey The RSA public key for encryption.
     * @return The encrypted AES key as a Base64 encoded string.
     * @throws Exception if encryption fails.
     */
    public static String encryptAESKeyWithRSA(SecretKey aesKey, PublicKey publicKey) throws Exception {
        Cipher rsaCipher = Cipher.getInstance("RSA");
        // Initializes Cipher with public key
        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);

        // Encrypted message as bytes
        byte[] encryptedKeyBytes = rsaCipher.doFinal(aesKey.getEncoded());

        return Base64.getEncoder().encodeToString(encryptedKeyBytes);
    }

    /**
     * Decrypts an AES encrypted message using the provided AES key.
     *
     * @param encryptedMessage The AES encrypted message as a Base64 string.
     * @param aesKey The AES key for decryption.
     * @return The decrypted plaintext message.
     * @throws Exception if decryption fails.
     */
    public static String decryptWithAES(String encryptedMessage, SecretKey aesKey) throws Exception {
        Cipher aesCipher = Cipher.getInstance("AES");
        // Initializes Cipher with AES key
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey);

        // Encrypted message as bytes
        byte[] decryptedBytes = aesCipher.doFinal(Base64.getDecoder().decode(encryptedMessage));

        return new String(decryptedBytes);
    }

    /**
     * Decrypts the AES key using RSA with the recipient's private key.
     *
     * @param encryptedAESKey The encrypted AES key as a Base64 string.
     * @param privateKey The RSA private key for decryption.
     * @return The decrypted AES SecretKey.
     * @throws Exception if decryption fails.
     */
    public static SecretKey decryptAESKeyWithRSA(String encryptedAESKey, PrivateKey privateKey) throws Exception {
        Cipher rsaCipher = Cipher.getInstance("RSA");
        // Initializes Cipher with private key
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);

        // Encrypted message as bytes
        byte[] decryptedKeyBytes = rsaCipher.doFinal(Base64.getDecoder().decode(encryptedAESKey));

        return new SecretKeySpec(decryptedKeyBytes, "AES");
    }

    /**
     * Generates a new RSA key pair for asymmetric encryption.
     *
     * @return A KeyPair with a public and private RSA key.
     * @throws Exception if key pair generation fails.
     */
    public static KeyPair generateRSAKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        // Initializes key generator with specified key size
        keyGen.initialize(RSA_KEY_SIZE);

        return keyGen.generateKeyPair();
    }
}
