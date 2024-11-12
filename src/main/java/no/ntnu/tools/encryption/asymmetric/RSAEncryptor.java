package no.ntnu.tools.encryption.asymmetric;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

public class RSAEncryptor {
    private static final int AES_KEY_SIZE = 256;  // or 128, 192 depending on your needs
    private static final int RSA_KEY_SIZE = 2048;

    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        // Initializes key generator with specified key size
        keyGen.init(AES_KEY_SIZE);

        return keyGen.generateKey();
    }

    public static String encryptWithAES(String message, SecretKey aesKey) throws Exception {
        Cipher aesCipher = Cipher.getInstance("AES");
        // Initializes Cipher with AES key
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);

        // Encrypted message as bytes
        byte[] encryptedBytes = aesCipher.doFinal(message.getBytes());

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String encryptAESKeyWithRSA(SecretKey aesKey, PublicKey publicKey) throws Exception {
        Cipher rsaCipher = Cipher.getInstance("RSA");
        // Initializes Cipher with public key
        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);

        // Encrypted message as bytes
        byte[] encryptedKeyBytes = rsaCipher.doFinal(aesKey.getEncoded());

        return Base64.getEncoder().encodeToString(encryptedKeyBytes);
    }

    public static String decryptWithAES(String encryptedMessage, SecretKey aesKey) throws Exception {
        Cipher aesCipher = Cipher.getInstance("AES");
        // Initializes Cipher with AES key
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey);

        // Encrypted message as bytes
        byte[] decryptedBytes = aesCipher.doFinal(Base64.getDecoder().decode(encryptedMessage));

        return new String(decryptedBytes);
    }

    public static SecretKey decryptAESKeyWithRSA(String encryptedAESKey, PrivateKey privateKey) throws Exception {
        Cipher rsaCipher = Cipher.getInstance("RSA");
        // Initializes Cipher with private key
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);

        // Encrypted message as bytes
        byte[] decryptedKeyBytes = rsaCipher.doFinal(Base64.getDecoder().decode(encryptedAESKey));

        return new SecretKeySpec(decryptedKeyBytes, "AES");
    }


    public static KeyPair generateRSAKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        // Initializes key generator with specified key size
        keyGen.initialize(RSA_KEY_SIZE);

        return keyGen.generateKeyPair();
    }
}
