package no.ntnu.tools.encryption;

import no.ntnu.messages.Message;

public class MessageHasher {

    /**
     * Takes a message in and returns the same message, but with the hashed
     * transmission stored in the header.
     *
     * @param message message that will be added hash to.
     * @return hashedMessage the new message with hashed element.
     */
    public static Message addHashedContentToMessage(Message message) {
        Message hashedMessage = message;

        String transmissionString = message.getBody().getTransmission().toString();
        String hashedTransmissionString = Hasher.encryptString(transmissionString);
        hashedMessage.getHeader().setHashedContent(hashedTransmissionString);

        return hashedMessage;
    }
}
