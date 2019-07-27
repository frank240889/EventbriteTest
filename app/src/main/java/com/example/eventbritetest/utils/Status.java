package com.example.eventbritetest.utils;

/**
 * A wrapper to hold the state of a task
 */
public class Status {
    public Throwable throwable;
    public State status;

    private Status(State status, Throwable throwable) {
        this.status = status;
        this.throwable = throwable;
    }

    public static Status error(Throwable throwable) {
        return new Status(State.ERROR, throwable);
    }

    public static Status busy() {
        return new Status(State.LOADING, null);
    }

    public static Status done() {
        return new Status(State.DONE, null);
    }

    public static Status done(String message) {
        return new Status(State.DONE, new Exception(message));
    }
}
