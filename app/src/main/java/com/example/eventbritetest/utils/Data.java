package com.example.eventbritetest.utils;

public class Data<D> {
    public D data;
    public State state;
    private Data(D data) {
        this.data = data;
        this.state = State.DONE;
    }

    public static <D>Data <D> success(D data) {
        return new Data<>(data);
    }
}
