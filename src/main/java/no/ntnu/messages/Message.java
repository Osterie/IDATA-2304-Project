package no.ntnu.messages;

import no.ntnu.tools.Logger;

/**
 * Represents a message with a header and body, used in communication protocols.
 */
public class Message {

  // The message header containing metadata
  private MessageHeader header;
  // The message body containing the actual content
  private MessageBody body;

  /**
   * Constructs a new message with the specified header and body.
   *
   * @param header The header of the message. Must not be null.
   * @param body   The body of the message. Must not be null.
   * @throws IllegalArgumentException if the header or body is null.
   */
  public Message(MessageHeader header, MessageBody body) throws IllegalArgumentException {
    if (header == null || body == null) {
      Logger.error("Header and body cannot be null");
      throw new IllegalArgumentException("Header and body cannot be null");
    }
    this.header = header;
    this.body = body;
  }

  /**
   * Returns the header of the message.
   *
   * @return The message header.
   */
  public MessageHeader getHeader() {
    return header;
  }

  /**
   * Sets the header of the message.
   *
   * @param header The new header to set. Must not be null.
   */
  public void setHeader(MessageHeader header) {
    this.header = header;
  }

  /**
   * Gets the body of the message.
   *
   * @return The message body.
   */
  public MessageBody getBody() {
    return body;
  }

  /**
   * Sets the body of the message.
   *
   * @param body The new body to set. Must not be null.
   */
  public void setBody(MessageBody body) {
    this.body = body;
  }

  /**
   * Parses a message from its protocol string representation.
   *
   * @param protocolString The protocol string representing a message.
   * @return The parsed message.
   * @throws IllegalArgumentException if the protocol string is invalid.
   */
  public static Message fromString(String protocolString) {

    // Split the string into header and body using the delimiter
    String[] parts = protocolString.split(Delimiters.HEADER_BODY.getValue(), 2);
    if (parts.length < 2) {
      Logger.error("Invalid message format: " + protocolString);
      throw new IllegalArgumentException("Invalid message format");
    }

    // Parse the header and body separately
    MessageHeader header = MessageHeader.fromString(parts[0]);
    MessageBody body = MessageBody.fromString(parts[1]);
    return new Message(header, body);
  }

  /**
   * Converts the message to its protocol string representation.
   *
   * @return The protocol string representing this message.
   * @throws IllegalArgumentException if either the header or body is null.
   */
  @Override
  public String toString() throws IllegalArgumentException {
    if (header == null || body == null) {
      throw new IllegalArgumentException("Header and body cannot be null");
    }
    return header + Delimiters.HEADER_BODY.getValue() + body;
  }
}