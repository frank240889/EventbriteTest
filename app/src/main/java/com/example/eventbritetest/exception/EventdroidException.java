package com.example.eventbritetest.exception;

public class EventdroidException extends Exception {
    protected ExceptionType exceptionType;
    public EventdroidException(String message, ExceptionType exceptionType) {
        super(message);
        this.exceptionType = exceptionType;
    }

    public ExceptionType getExceptionType() {
        return exceptionType;
    }

    public enum ExceptionType {
        INVALID_LOCATION,
        NO_LOCATION_PROVIDED,
        NO_NETWORK,
        TIME_OUT,
        PARSING,
        NO_REMAIN_DATA,
        EMPTY_DATA
    }
}
