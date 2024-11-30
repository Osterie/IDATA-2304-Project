package no.ntnu.tools.encryption;

import no.ntnu.messages.Message;

/**
 * Provides functionality to hash message content and store the hash
 * in the message header for validation purposes.
 */
public class MessageHasher {

  /**
   * Takes a message as input and returns the same message, but with a hashed
   * version of its transmission content stored in the header.
   *
   * @param message the message to which the hashed content will be added
   * @return the updated message with the hashed content added to its header
   */
  public static Message addHashedContentToMessage(Message message) {
    // Make a copy of the input message
    Message hashedMessage = message;

    // Retrieve the transmission content from the message body
    String transmissionString = message.getBody().getTransmission().toString();

    // Hash the transmission content
    String hashedTransmissionString = Hasher.encryptString(transmissionString);

    // Store the hashed content in the message header
    hashedMessage.getHeader().setHashedContent(hashedTransmissionString);

    // Return the updated message
    return hashedMessage;
  }
}