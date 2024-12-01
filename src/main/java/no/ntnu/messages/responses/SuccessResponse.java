package no.ntnu.messages.responses;

import no.ntnu.messages.Transmission;

/**
 * Represents a successful response to a transmission in the messaging system.
 * This response indicates that the associated transmission was executed
 * successfully.
 */
public class SuccessResponse extends Response {

  /**
   * Constructs a {@code SuccessResponse} with the specified transmission and
   * response data.
   *
   * @param transmission The {@link Transmission} associated with this success
   *                     response.
   * @param responseData The data describing the success or additional details.
   *                     Can be null or empty.
   */
  public SuccessResponse(Transmission transmission, String responseData) {
    super("SUCCESS", transmission, responseData);
  }

  /**
   * Constructs a {@code SuccessResponse} without a transmission or response data.
   */
  public SuccessResponse() {
    super("SUCCESS", null, null);
  }
}