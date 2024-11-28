package no.ntnu.tools.encryption;

import no.ntnu.messages.Message;
import no.ntnu.tools.encryption.asymmetric.HybridRSAEncryptor;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.PrivateKey;

public class MessageEncryptor {

    /**
     * Takes message in and returns the encrypted version back.
     *
     * @param message the message to be encrypted.
     *
     * @return message that is encrypted.
     */
    public static Message encryptMessage(Message message) {

        try {
            // Generate RSA key pair for recipient
            KeyPair recipientKeyPair = HybridRSAEncryptor.generateRSAKeyPair();
            System.out.println("Generated recipient's RSA key pair.");

            // Generate AES key
            SecretKey aesKey = HybridRSAEncryptor.generateAESKey();
            System.out.println("Generated AES key.");

            // Encrypt the message
            String originalMessage = "Hello, this is a confidential message!";
            String encryptedMessage = HybridRSAEncryptor.encryptWithAES(originalMessage, aesKey);
            System.out.println("Encrypted Message: " + encryptedMessage);

            // Encrypt the AES key with the recipient's public key
            String encryptedAESKey = HybridRSAEncryptor.encryptAESKeyWithRSA(aesKey, recipientKeyPair.getPublic());
            System.out.println("Encrypted AES Key: " + encryptedAESKey);

        } catch (Exception e) {
            System.err.println("Error occurred during encryption or decryption: " + e.getMessage());
            e.printStackTrace();
        }

        return message;
    }

    /**
     * Takes in encrypted message string with keys and returns the decrypted version back.
     *
     * @param encryptedMessage messsage to be decrypted.
     * @param encryptedAESKey
     * @param privateKey
     *
     * @return message that is decrypted.
     */
    public static String decryptStringMessage(String encryptedMessage, String encryptedAESKey, PrivateKey privateKey) throws Exception {
        // Decrypt the AES key using the private key
        SecretKey decryptedAESKey = HybridRSAEncryptor.decryptAESKeyWithRSA(encryptedAESKey, privateKey);
        System.out.println("Decrypted AES Key.");

        // Decrypt the message using the AES key
        String decryptedMessage = HybridRSAEncryptor.decryptWithAES(encryptedMessage, decryptedAESKey);
        System.out.println("Decrypted Message: " + decryptedMessage);

        return decryptedMessage;
    };
}
