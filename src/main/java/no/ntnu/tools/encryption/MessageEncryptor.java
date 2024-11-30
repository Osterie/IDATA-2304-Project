package no.ntnu.tools.encryption;

import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import no.ntnu.messages.Message;
import no.ntnu.messages.Transmission;
import no.ntnu.tools.encryption.asymmetric.HybridRSAEncryptor;


/**
 * Provides methods for encrypting and decrypting messages.
 */
public class MessageEncryptor {

  /**
   * Encrypts a given message using the recipient's public key.
   *
   * @param message            the message to be encrypted
   * @param recipientPublicKey the recipient's public key
   * @return the encrypted message
   */
  public static Message encryptMessage(Message message, PublicKey recipientPublicKey) {
    Message encryptedMessage = message;

    try {
      // Generate AES key
      SecretKey aesKey = HybridRSAEncryptor.generateAESKey();

      // Original content
      String originalTransmission = encryptedMessage.getBody().getTransmission().toString();
      String originalId = encryptedMessage.getHeader().getId();

      // Encrypted content
      String encryptedTransmission = HybridRSAEncryptor.encryptWithAES(originalTransmission, aesKey);
      String encryptedId = HybridRSAEncryptor.encryptWithAES(originalId, aesKey);

      // Encrypt the AES key with the recipient's public key
      String encryptedAesKey = HybridRSAEncryptor.encryptAESKeyWithRSA(aesKey, recipientPublicKey);

      // Store encrypted AES key in header
      encryptedMessage.getHeader().setEncryptedAES(encryptedAesKey);

      // Add encrypted content to body
      System.out.println("HEEEEERE: " + encryptedTransmission);
      encryptedMessage.getBody().getTransmission().setTransmission(encryptedTransmission);
      System.out.println("HEEEEERE: " + encryptedMessage.getBody().getTransmission().toString());
      // Add encrypted content to header
      encryptedMessage.getHeader().setId(encryptedId);

    } catch (Exception e) {
      System.err.println("Error occurred during encryption: " + e.getMessage());
      e.printStackTrace();
    }

    return encryptedMessage;
  }

  /**
   * Decrypts an encrypted message using the recipient's private key.
   *
   * @param encryptedMessage the message to be decrypted
   * @param privateKey       the recipient's private key
   * @return the decrypted message
   * @throws Exception if decryption fails
   */
  public static Message decryptStringMessage(Message encryptedMessage, PrivateKey privateKey)
          throws Exception {
    // Decrypt the AES key using the private key
    SecretKey decryptedAesKey = HybridRSAEncryptor.decryptAESKeyWithRSA(
            encryptedMessage.getHeader().getEncryptedAES(), privateKey);

    // Decrypt the message using the AES key
    String decryptedTransmission = HybridRSAEncryptor.decryptWithAES(
            encryptedMessage.getBody().getTransmission().getTransmissionString(),
            decryptedAesKey);
    String decryptedId = HybridRSAEncryptor.decryptWithAES(
            encryptedMessage.getHeader().getId(),
            decryptedAesKey);

    Message decryptedMessage = encryptedMessage;
    decryptedMessage.getBody().getTransmission().setTransmission(decryptedTransmission);
    decryptedMessage.getHeader().setId(decryptedId);

    return decryptedMessage;
  }
}