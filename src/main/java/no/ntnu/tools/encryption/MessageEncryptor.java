package no.ntnu.tools.encryption;

import no.ntnu.messages.Message;
import no.ntnu.messages.Transmission;
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

        Message encryptedMessage = message;

        try {
            // Generate RSA key pair for recipient
            KeyPair recipientKeyPair = HybridRSAEncryptor.generateRSAKeyPair();

            // Generate AES key
            SecretKey aesKey = HybridRSAEncryptor.generateAESKey();

            // Encrypt the message
            String originalContent = encryptedMessage.getBody().toString();
            String encryptedContent = HybridRSAEncryptor.encryptWithAES(originalContent, aesKey);

            // Encrypt the AES key with the recipient's public key
            String encryptedAESKey = HybridRSAEncryptor.encryptAESKeyWithRSA(aesKey, recipientKeyPair.getPublic());

            // Stores encrypted AES key in header.
            encryptedMessage.getHeader().setEncryptedAES(encryptedAESKey);

            // Transmission with encrypted content.
            Transmission encryptedTransmission = encryptedMessage.getBody().getTransmission();
            encryptedTransmission.setTransmission(encryptedContent);

            // Add encrypted content to body.
            encryptedMessage.getBody().setTransmission(encryptedTransmission);

        } catch (Exception e) {
            System.err.println("Error occurred during encryption: " + e.getMessage());
            e.printStackTrace();
        }

        return encryptedMessage;
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
