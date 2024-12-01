package no.ntnu.messages.responses;

/**
 * Represents the reason for a failure.
 */
public enum FailureReason {

  SERVER_NOT_RUNNING("Server is not running"),
  FAILED_TO_IDENTIFY_CLIENT("Failed to identify client"),
  INTEGRITY_ERROR("Integrity error");

  private final String reason;

  /**
   * Constructs a new FailureReason with the specified reason.
   *
   * @param reason The reason for the failure.
   */
  FailureReason(String reason) {
    this.reason = reason;
  }

  /**
   * Gets the reason for the failure.
   *
   * @return The reason for the failure.
   */
  public String getReason() {
    return reason;
  }

  /**
   * Retrieves a {@link FailureReason} instance based on its string value.
   *
   * @param reason The string value to match.
   * @return The matching {@link FailureReason} instance, or {@code null} if no
   *         match is found.
   */
  public static FailureReason fromString(String reason) {
    for (FailureReason failureReason : FailureReason.values()) {
      if (failureReason.getReason().equals(reason)) {
        return failureReason;
      }
    }
    return null;
  }

  /**
   * Gets the string representation of the failure reason.
   *
   * @return The string representation of the failure reason.
   */
  @Override
  public String toString() {
    return reason;
  }
}
