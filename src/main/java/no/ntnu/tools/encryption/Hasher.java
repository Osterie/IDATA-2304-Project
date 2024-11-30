package no.ntnu.tools.encryption;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Provides functionality to hash strings using the SHA-256 algorithm.
 *
 * <p>This class is designed to hash input strings for validation purposes,
 * as hashing is a one-way process. The resulting hash cannot be reversed
 * to obtain the original input.</p>
 */
public class Hasher {

    /**
     * Hashes the given input string using the SHA-256 algorithm and returns
     * the hashed value as a hexadecimal string.
     *
     * <p>Note: Since hashing is one-way, this method should only be used
     * for validating sent information and not for encryption.</p>
     *
     * @param message the input string to be hashed
     * @return the hashed value of the input string as a hexadecimal string
     * @throws RuntimeException if the SHA-256 hashing algorithm is not available
     */
    public static String encryptString(String message) {
        try {
            // Create an instance of SHA-256 MessageDigest
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Compute the hash and obtain the result as a byte array
            byte[] hashBytes = digest.digest(message.getBytes());

            // Convert the byte array into a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0'); // Append leading zero for single-digit hex values
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Wrap the exception in a runtime exception with an informative message
            throw new RuntimeException("Error: SHA-256 algorithm not found.", e);
        }
    }
}