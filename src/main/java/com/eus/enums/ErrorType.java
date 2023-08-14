package com.eus.enums;

public enum ErrorType {
    UNDEFINED,
    FRAMEWORK_VALIDATION_ERROR,
    REGULAR;

    public static ErrorType getDefault() {
        return UNDEFINED;
    }
}
