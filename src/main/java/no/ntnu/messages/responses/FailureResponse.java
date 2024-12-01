package no.ntnu.messages.responses;

import no.ntnu.messages.Transmission;

/**
 * Represents a failure response to a transmission.
 * This response is used to indicate that the execution of a transmission has
 * failed.
 */
public class FailureResponse extends Response {

  /**
   * Constructs a {@code FailureResponse} with the specified transmission and
   * response data.
   *
   * @param transmission  The {@link Transmission} associated with this failure
   *                      response.
   * @param failureReason The data describing why failure occured
   */
  public FailureResponse(Transmission transmission, FailureReason failureReason) {
    super("FAILURE", transmission, failureReason.toString());
  }

  /**
   * Constructs a {@code FailureResponse} without a transmission or failure
   * reason.
   */
  public FailureResponse() {
    super("FAILURE", null, null);
  }

  /**
   * Gets the reason for the failure.
   *
   * @return The reason for the failure.
   */
  public FailureReason getFailureReason() {
    return FailureReason.fromString(this.getResponseData());
  }

}