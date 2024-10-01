package no.ntnu.tools.Encryption;

/**
 * This class is used to encrypt and decrypt string messages
 */
public class CeasarCipherEncryptor {

    //Constant for the shift amount
    private static final int SHIFT = 3;

    /**
     * This method takes in a message as a string
     * and gives back an encrypted string using Caesar cipher.
     */
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
     * This method takes in an encrypted string
     * and gives back a decrypted string using Caesar cipher.
     */
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
