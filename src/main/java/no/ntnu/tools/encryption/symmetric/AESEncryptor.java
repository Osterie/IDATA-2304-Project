package no.ntnu.tools.encryption.symmetric;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Utility class for encrypting and decrypting string messages using the AES encryption algorithm.
 *
 * <p>AES (Advanced Encryption Standard) is a symmetric
 * encryption algorithm widely used for securing data.</p>
 *
 * <h3>Features:</h3>
 * <ul>
 *     <li>Generate AES secret keys</li>
 *     <li>Encrypt and decrypt strings</li>
 *     <li>Convert secret keys to/from Base64 strings for storage or transmission</li>
 * </ul>
 */
public class AESEncryptor {

  /**
   * Algorithm name used for AES encryption and decryption.
   */
  private static final String ALGORITHM = "AES";

  /**
   * Key size for AES encryption, in bits.
   */
  private static final int KEY_SIZE = 128;

  /**
   * Generates a new AES secret key.
   *
   * <p>This method creates a random 128-bit AES secret
   * key that can be used for encryption and decryption.</p>
   *
   * @return A newly generated AES {@link SecretKey}.
   * @throws Exception if an error occurs during key generation.
   */
  public static SecretKey generateKey() throws Exception {
    KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
    keyGenerator.init(KEY_SIZE);
    return keyGenerator.generateKey();
  }

  /**
   * Encrypts a string message using AES encryption.
   *
   * <p>This method takes a plain text message and encrypts it using the provided AES secret key.
   * The result is returned as a Base64-encoded string for easy storage or transmission.</p>
   *
   * @param message   The plain text message to be encrypted.
   * @param secretKey The AES {@link SecretKey} used for encryption.
   * @return The encrypted message as a Base64-encoded string.
   * @throws Exception if an error occurs during encryption.
   */
  public static String encryptString(String message, SecretKey secretKey) throws Exception {
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, secretKey);

    byte[] encryptedBytes = cipher.doFinal(message.getBytes());
    return Base64.getEncoder().encodeToString(encryptedBytes);
  }

  /**
   * Decrypts a Base64-encoded AES-encrypted string.
   *
   * <p>This method takes an encrypted message in
   * Base64 format and decrypts it using the provided AES secret key.
   * The result is the original plain text message.</p>
   *
   * @param encryptedMessage The encrypted message in Base64 format.
   * @param secretKey        The AES {@link SecretKey} used for decryption.
   * @return The decrypted plain text message.
   * @throws Exception if an error occurs during decryption.
   */
  public static String decryptString(String encryptedMessage,
                                     SecretKey secretKey) throws Exception {
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, secretKey);

    byte[] decodedBytes = Base64.getDecoder().decode(encryptedMessage);
    byte[] decryptedBytes = cipher.doFinal(decodedBytes);

    return new String(decryptedBytes);
  }

  /**
   * Converts an AES secret key to a Base64-encoded string.
   *
   * <p>This method is useful for storing or sharing the key securely.</p>
   *
   * @param secretKey The AES {@link SecretKey} to be converted.
   * @return The Base64-encoded string representation of the secret key.
   */
  public static String keyToString(SecretKey secretKey) {
    return Base64.getEncoder().encodeToString(secretKey.getEncoded());
  }

  /**
   * Converts a Base64-encoded string back to an AES secret key.
   *
   * <p>This method is useful for retrieving a stored secret key.</p>
   *
   * @param keyString The Base64-encoded string representation of the AES secret key.
   * @return The reconstructed AES {@link SecretKey}.
   */
  public static SecretKey stringToKey(String keyString) {
    byte[] decodedKey = Base64.getDecoder().decode(keyString);
    return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
  }
}