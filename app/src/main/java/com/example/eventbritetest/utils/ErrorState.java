package com.example.eventbritetest.utils;

public class ErrorState {
    public Error error;
    private ErrorState(Error error){
        this.error = error;
    }

    public static ErrorState create(Error error){
        return new ErrorState(error);
    }

    public enum Error {
        NETWORK,
        NO_MORE_DATA_NETWORK,
        LOCATION,
        PARSING,
        NO_MORE_DATA_PARSING,
        EMPTY_DATA,
        NO_MORE_DATA,
        UNKNOWN
    }
}
