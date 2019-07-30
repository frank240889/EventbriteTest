package com.example.eventbritetest.utils;

/**
 * A wrapper to hold the state of a task
 */
public class Status {
    public Throwable throwable;
    public State status;
    public int idResource;

    private Status(State status, Throwable throwable) {
        this.status = status;
        this.throwable = throwable;
    }

    private Status(State status, Throwable throwable, int idResource) {
        this.status = status;
        this.throwable = throwable;
        this.idResource = idResource;
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

    public void setIdResource(int idResource) {
        this.idResource = idResource;
    }
}
