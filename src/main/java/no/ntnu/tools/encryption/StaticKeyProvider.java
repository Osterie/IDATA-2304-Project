package no.ntnu.tools.encryption;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import no.ntnu.tools.Logger;

/**
 * Provides a static RSA key pair for use in encryption and decryption.
 *
 * <p>This class generates a single RSA key pair when first accessed
 * and provides static methods to retrieve the public and private keys.</p>
 */
public class StaticKeyProvider {

  private static KeyPair keyPair;

  // Static block to initialize the key pair
  static {
    try {
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
      keyGen.initialize(2048);
      keyPair = keyGen.generateKeyPair();
      Logger.info("Static RSA key pair generated successfully.");
    } catch (Exception e) {
      Logger.error("Failed to generate RSA key pair: " + e.getMessage());
      throw new RuntimeException("Static RSA key pair generation failed.", e);
    }
  }

  /**
   * Retrieves the public key from the static key pair.
   *
   * @return The public key.
   */
  public static PublicKey getStaticPublicKey() {
    return keyPair.getPublic();
  }

  /**
   * Retrieves the private key from the static key pair.
   *
   * @return The private key.
   */
  public static PrivateKey getStaticPrivateKey() {
    return keyPair.getPrivate();
  }
}