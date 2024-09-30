package no.ntnu.listeners.common;

/**
 * A listener who will get notified about events happening on the communication channel.
 * The channel can be a TCP socket, or other type of channel.
 */
public interface CommunicationChannelListener {
  /**
   * This event is fired when the communication channel is closed.
   */
  void onCommunicationChannelClosed();
}
