package no.ntnu.tools.encryption;

import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;

import no.ntnu.messages.*;
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
    Message encryptedMessage = message;
    String encryptedAeskey = null;
    String encryptedBody = null;
    String encryptedHeader = null;
    String encryptedMessageTest = null;

    try {
      // Generate AES key
      SecretKey aesKey = HybridRSAEncryptor.generateAESKey();

      // Original content
      MessageHeader originalHeader = encryptedMessage.getHeader();
      MessageBody originalBody = encryptedMessage.getBody();

      // Encrypt message
      encryptedMessageTest = HybridRSAEncryptor.encryptWithAES(message.toString(), aesKey);

      // Encrypted content
      encryptedHeader = HybridRSAEncryptor.encryptWithAES(originalHeader.toString(), aesKey);
      encryptedBody = HybridRSAEncryptor.encryptWithAES(originalBody.toString(), aesKey);

      // Encrypt the AES key with the recipient's public key
      encryptedAeskey = HybridRSAEncryptor.encryptAESKeyWithRSA(aesKey, recipientPublicKey);

      // Add encrypted content to header
      // encryptedMessage.getHeader().setId(encryptedId);

    } catch (Exception e) {
      System.err.println("Error occurred during encryption: " + e.getMessage());
      e.printStackTrace();
    }

    return encryptedMessageTest + Delimiters.HEADER_BODY.getValue() + encryptedAeskey;
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

    String decryptedHeaderString = null;
    String decryptedBodyString = null;
    String decryptedMessage = null;

    // Split the string by "-"
    String[] elements = encryptedMessageString.split("-");

    // Check if the split results in exactly 3 elements
    if (elements.length == 2) {
      // Access individual elements
      String element1 = elements[0];
      String element2 = elements[1];

      // AES key
      SecretKey aesKey = HybridRSAEncryptor.decryptAESKeyWithRSA(element2, privateKey);

      // Decrypt elements
      decryptedMessage = HybridRSAEncryptor.decryptWithAES(element1, aesKey);

    } else {
      System.out.println(encryptedMessageString);
      System.out.println("The input does not have exactly 3 elements separated by '-'.");
    }

    return decryptedMessage;
  }
}