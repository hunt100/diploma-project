package com.example.demo.data.enums;

public enum PointStatus {
    CREATED(100, false),
    VALIDATED(101, false),
    ACTIVE(200, false),
    RESOLVED(300, true),
    CANCEL(301, true),
    REOPEN(302, false);

    private int statusCode;
    private boolean isFinished;

    PointStatus(int statusCode, boolean isFinished) {
        this.statusCode = statusCode;
        this.isFinished = isFinished;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public boolean isFinished() {
        return isFinished;
    }
}
