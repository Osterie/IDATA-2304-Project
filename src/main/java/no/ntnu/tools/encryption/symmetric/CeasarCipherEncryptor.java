package no.ntnu.tools.encryption.symmetric;

/**
 * This class is used to encrypt and decrypt string messages using the Caesar cipher.
 *
 * <p><strong>Deprecated:</strong> The Caesar cipher is considered insecure due to its simplicity
 * and lack of cryptographic strength. It is not suitable for real-world encryption purposes.
 * Use a modern encryption algorithm such as AES instead.</p>
 */
@Deprecated
public class CeasarCipherEncryptor {

    // Constant for the shift amount
    private static final int SHIFT = 3;

    /**
     * Encrypts the given string using the Caesar cipher.
     *
     * <p><strong>Deprecated:</strong> The Caesar cipher is insecure and should not be used
     * for encrypting sensitive data. Consider using a secure encryption library like Java's
     * built-in `javax.crypto` package for encryption.</p>
     *
     * @param message the string to be encrypted
     * @return the encrypted string
     */
    @Deprecated
    public static String encryptString(String message) {
        StringBuilder encryptedMessage = new StringBuilder();

        for (char ch : message.toCharArray()) {
            if (Character.isLetter(ch)) {
                char base = Character.isUpperCase(ch) ? 'A' : 'a';
                // Shift character and wrap around using modulo operation
                char encryptedChar = (char) ((ch - base + SHIFT) % 26 + base);
                encryptedMessage.append(encryptedChar);
            } else {
                // Non-alphabetic characters are unchanged
                encryptedMessage.append(ch);
            }
        }

        return encryptedMessage.toString();
    }

    /**
     * Decrypts the given string using the Caesar cipher.
     *
     * <p><strong>Deprecated:</strong> The Caesar cipher is insecure and should not be used
     * for decrypting sensitive data. Consider using a secure decryption library instead.</p>
     *
     * @param encryptedMessage the encrypted string to be decrypted
     * @return the decrypted string
     */
    @Deprecated
    public static String decryptString(String encryptedMessage) {
        StringBuilder decryptedMessage = new StringBuilder();

        for (char ch : encryptedMessage.toCharArray()) {
            if (Character.isLetter(ch)) {
                char base = Character.isUpperCase(ch) ? 'A' : 'a';
                // Reverse shift character and wrap around using modulo operation
                char decryptedChar = (char) ((ch - base - SHIFT + 26) % 26 + base);
                decryptedMessage.append(decryptedChar);
            } else {
                // Non-alphabetic characters are unchanged
                decryptedMessage.append(ch);
            }
        }

        return decryptedMessage.toString();
    }
}