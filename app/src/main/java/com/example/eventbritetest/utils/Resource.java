package com.example.eventbritetest.utils;

public class Resource<T> {
    public State state;
    public T data;
    public Throwable error;
    private Resource(State state, T data) {
        this.state = state;
        this.data = data;
    }

    private Resource(State state, T data, Throwable error) {
        this(state, data);
        this.error = error;
    }

    public static <T> Resource<T> loading() {
        return new Resource<>(State.LOADING, null, null);
    }

    public static <T> Resource<T> done(T data) {
        return new Resource<>(State.DONE, data, null);
    }

    public static <T> Resource<T> error(Throwable error) {
        return new Resource<>(State.ERROR, null, error);
    }
}
