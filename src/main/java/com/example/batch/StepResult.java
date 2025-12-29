package com.example.batch;

public class StepResult {
    public enum Status {
        SUCCESS, FAILURE
    }

    private final Status status;
    private final String message;
    private final int recordCount;

    public StepResult(Status status, String message, int recordCount) {
        this.status = status;
        this.message = message;
        this.recordCount = recordCount;
    }

    public static StepResult success(String message, int recordCount) {
        return new StepResult(Status.SUCCESS, message, recordCount);
    }

    public static StepResult failure(String message, int recordCount) {
        return new StepResult(Status.FAILURE, message, recordCount);
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public int getRecordCount() {
        return recordCount;
    }
}
