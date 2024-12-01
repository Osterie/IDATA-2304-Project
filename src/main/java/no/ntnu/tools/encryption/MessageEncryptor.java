package no.ntnu.tools.encryption;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import javax.crypto.SecretKey;
import no.ntnu.messages.*;
import no.ntnu.tools.Logger;
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
  public static String encryptMessage(Message message, PublicKey recipientPublicKey) {
    String encryptedMessage = message.toString();
    String encryptedAeskey = null;

    try {
      // Generate AES key
      SecretKey aesKey = HybridRSAEncryptor.generateAESKey();

      // Encrypt message
      encryptedMessage = HybridRSAEncryptor.encryptWithAES(encryptedMessage, aesKey);

      // Encrypt the AES key with the recipient's public key
      encryptedAeskey = HybridRSAEncryptor.encryptAESKeyWithRSA(aesKey, recipientPublicKey);
    } catch (Exception e) {
      Logger.error("Error occurred during encryption: " + e.getMessage());
      e.printStackTrace();
    }

    return encryptedMessage + "-" + encryptedAeskey;
  }

  /**
   * Decrypts an encrypted message using the recipient's private key.
   *
   * @param encryptedMessageString the message to be decrypted
   * @param privateKey       the recipient's private key
   * @return the decrypted message
   * @throws Exception if decryption fails
   */
  public static String decryptStringMessage(String encryptedMessageString, PrivateKey privateKey)
          throws Exception {

    String decryptedMessage = null;

    // Split the string by "-"
    String[] elements = encryptedMessageString.split("-");

    // Check if the split results in exactly 3 elements
    if (elements.length == 2) {
      // Access individual elements
      String element1 = elements[0];
      String element2 = elements[1];

      try {
        SecretKey aesKey = HybridRSAEncryptor.decryptAESKeyWithRSA(element2, privateKey);
        decryptedMessage = HybridRSAEncryptor.decryptWithAES(element1, aesKey);

        Base64.getDecoder().decode(element1); // Validate encrypted message
        Base64.getDecoder().decode(element2); // Validate encrypted AES key
      } catch (Exception e) {
        Logger.error("Decryption failed: " + e.getMessage());
        throw e;
      }

    } else {
      Logger.info("The input does not have exactly 2 elements separated by '-'.");
    }

    if (decryptedMessage == null) {
      Logger.error("Problem decrypting message, message ended up: null");
    }

    return decryptedMessage;
  }
}