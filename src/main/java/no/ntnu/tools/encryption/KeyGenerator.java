package no.ntnu.tools.encryption;

import no.ntnu.tools.Logger;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

/**
 * Utility class for generating RSA key pairs.
 *
 * <p>This class provides a method for generating a
 * public-private key pair using the RSA encryption algorithm.
 * It is used in asymmetric encryption processes, where
 * the public key is used for encryption and the private key
 * is used for decryption.</p>
 */
public class KeyGenerator {

    /**
     * Generates a new RSA key pair.
     *
     * <p>This method uses the RSA algorithm with a key size
     * of 2048 bits to generate a public-private key pair.
     * The public key can be used to encrypt data, and
     * the private key can be used to decrypt it.</p>
     *
     * @return A {@link KeyPair} object containing the public
     * and private keys, or {@code null} if key generation fails.
     */
    public static KeyPair generateRSAKeyPair() {
        try {
            // Create a KeyPairGenerator for RSA encryption
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048); // Set key size to 2048 bits

            // Generate and return the RSA key pair
            return keyGen.generateKeyPair();
        } catch (Exception e) {
            // Log the error and return null if key generation fails
            Logger.error("Error generating RSA key pair: " + e.getMessage());
            return null; // Indicate failure
        }
    }
}