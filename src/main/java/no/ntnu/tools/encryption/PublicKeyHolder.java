package no.ntnu.tools.encryption;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * A utility class that generates and holds a single
 * static RSA key pair for shared use.
 * This class ensures that the keys are generated
 * only once and remain accessible throughout the application.
 */
public class PublicKeyHolder {

  private static final KeyPair keyPair;

  // Static block to generate the RSA key pair once
  static {
    KeyPair tempKeyPair = null;
    try {
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
      keyGen.initialize(2048);
      tempKeyPair = keyGen.generateKeyPair();
    } catch (Exception e) {
      System.err.println("Error generating RSA key pair: " + e.getMessage());
    }
    keyPair = tempKeyPair;
  }

  /**
   * Retrieves the public key from the static key pair.
   *
   * @return The public key for encryption.
   */
  public static PublicKey getPublicKey() {
    if (keyPair == null) {
      throw new IllegalStateException("Key pair generation failed.");
    }
    return keyPair.getPublic();
  }

  /**
   * Retrieves the private key from the static key pair.
   *
   * @return The private key for decryption.
   */
  public static PrivateKey getPrivateKey() {
    if (keyPair == null) {
      throw new IllegalStateException("Key pair generation failed.");
    }
    return keyPair.getPrivate();
  }
}