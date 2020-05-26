package com.example.demo.data.enums;

public enum ValueKey {
    VALIDATE_EVENT_NOTIFICATION("point.status.validated"),
    FINISH_EVENT_NOTIFICATION("point.status.finished"),
    CANCEL_EVENT_NOTIFICATION("point.status.cancelled"),
    EMAIL_RESET_PASSWORD_NOTIFICATION("email.reset.password"),
    REGISTRATION_CONFIRM("email.registration.confirm");

    private String keyBdValue;

    ValueKey(String keyBdValue) {
        this.keyBdValue = keyBdValue;
    }

    public String getKeyBdValue() {
        return keyBdValue;
    }
}
