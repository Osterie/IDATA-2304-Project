package no.ntnu.messages.responses;

public enum FailureReason {

    SERVER_NOT_RUNNING("Server is not running"),

    SERVER_ALREADY_RUNNING("Server is already running"),

    SERVER_NOT_STOPPED("Server is not stopped"),

    FAILED_TO_IDENTIFY_CLIENT("Failed to identify client"),

    INTEGRITY_ERROR("Integrity error");

    private final String reason;

    FailureReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return reason;
    }


    public static FailureReason fromString(String reason) {
        for (FailureReason failureReason : FailureReason.values()) {
            if (failureReason.getReason().equals(reason)) {
                return failureReason;
            }
        }
        return null;
    }

    public boolean equals(FailureReason reason) {
        return this.reason.equals(reason.getReason());
    }
}
