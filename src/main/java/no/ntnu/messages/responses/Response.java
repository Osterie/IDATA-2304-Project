package no.ntnu.messages.responses;

import no.ntnu.messages.Delimiters;
import no.ntnu.messages.Transmission;

/**
 * Represents an abstract response to a transmission in the messaging system.
 * A response contains a protocol string, the transmission that triggered the
 * response,
 * and any additional response data.
 * Subclasses should define specific types of responses, such as success or
 * failure responses.
 */
public abstract class Response extends Transmission {
  private String responseData;
  private Transmission transmission;

  /**
   * Constructs a {@code Response} with the specified protocol string,
   * transmission, and response data.
   *
   * @param responseProtocolString The protocol string representing the type of
   *                               response (e.g., "SUCCESS", "FAILURE").
   * @param transmission           The {@link Transmission} that triggered this
   *                               response.
   * @param responseData           Additional data describing the response. Can be
   *                               null or empty.
   */
  protected Response(String responseProtocolString, Transmission transmission,
                     String responseData) {
    super(responseProtocolString);
    this.transmission = transmission;
    this.responseData = responseData;
  }

  /**
   * Sets the transmission associated with this response.
   *
   * @param transmission The {@link Transmission} to associate with this response.
   */
  public void setTransmission(Transmission transmission) {
    this.transmission = transmission;
  }

  /**
   * Gets the transmission associated with this response.
   *
   * @return The {@link Transmission} that triggered this response.
   */
  public Transmission getTransmission() {
    return transmission;
  }

  /**
   * Sets the response data.
   *
   * @param responseData The data to associate with this response.
   */
  public void setResponseData(String responseData) {
    this.responseData = responseData;
  }

  /**
   * Gets the response data.
   *
   * @return The data associated with this response.
   */
  public String getResponseData() {
    return responseData;
  }

  /**
   * Converts this response into its protocol string representation.
   * The format is: `TRANSMISSION_STRING | COMMAND | RESPONSE_DATA`.
   *
   * @return The protocol string representation of this response.
   */
  @Override
  public String toString() {
    String protocolString = this.getTransmissionString();
    protocolString += Delimiters.BODY_FIELD_PARAMETERS.getValue();
    protocolString += this.transmission.getTransmissionString();
    protocolString += Delimiters.BODY_FIELD_PARAMETERS.getValue();
    protocolString += this.responseData;
    return protocolString;
  }
}