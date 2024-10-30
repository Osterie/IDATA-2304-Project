package no.ntnu.tools.encryption;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class is used to hash strings.
 */
public class HashEncryptor {

    // TODO: Fill methods with hashing method

    /**
     * This method takes in a message as a string
     * and gives back an encrypted string.
     *
     * Important! Since hashing is one-way,
     * it should only be used for validating
     * sent information
     */
    public static String encryptString(String message) {
        try {
            // Create SHA-256 MessageDigest instance
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(message.getBytes());

            // Convert byte array into a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error: SHA-256 algorithm not found.", e);
        }
    }
}
