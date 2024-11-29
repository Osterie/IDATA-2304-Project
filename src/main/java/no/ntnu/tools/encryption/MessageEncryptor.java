package no.ntnu.tools.encryption;

import no.ntnu.messages.Message;
import no.ntnu.messages.Transmission;
import no.ntnu.tools.encryption.asymmetric.HybridRSAEncryptor;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class MessageEncryptor {

    /**
     * Takes message in and returns the encrypted version back.
     *
     * @param message the message to be encrypted.
     *
     * @return message that is encrypted.
     */
    public static Message encryptMessage(Message message, PublicKey recipientPublicKey) {

        Message encryptedMessage = message;

        try {
            // Generate AES key
            SecretKey aesKey = HybridRSAEncryptor.generateAESKey();

            // Encrypt the message
            String originalContent = encryptedMessage.getBody().toString();
            String encryptedContent = HybridRSAEncryptor.encryptWithAES(originalContent, aesKey);

            // Encrypt the AES key with the recipient's public key
            String encryptedAESKey = HybridRSAEncryptor.encryptAESKeyWithRSA(aesKey, recipientPublicKey);

            // Stores encrypted AES key in header
            encryptedMessage.getHeader().setEncryptedAES(encryptedAESKey);

            // Transmission with encrypted content
            Transmission encryptedTransmission = encryptedMessage.getBody().getTransmission();
            encryptedTransmission.setTransmission(encryptedContent);

            // Add encrypted content to body
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
     * @param privateKey
     *
     * @return message that is decrypted.
     */
    public static Message decryptStringMessage(Message encryptedMessage, PrivateKey privateKey) throws Exception {
        // Decrypt the AES key using the private key
        SecretKey decryptedAESKey = HybridRSAEncryptor.decryptAESKeyWithRSA(encryptedMessage.getHeader().getEncryptedAES(), privateKey);
        System.out.println("Decrypted AES Key.");

        // Decrypt the message using the AES key
        String decryptedTransmission = HybridRSAEncryptor.decryptWithAES(encryptedMessage.getBody().getTransmission().getTransmissionString(), decryptedAESKey);

        Message decryptedMessage = encryptedMessage;
        decryptedMessage.getBody().getTransmission().setTransmission(decryptedTransmission);

        return decryptedMessage;
    };
}
