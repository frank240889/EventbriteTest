package com.example.eventbritetest.utils;

public class Resource<T> {
    public T data;
    public int idResourceMessage = -1;
    private Resource(T data) {
        this.data = data;
    }
    private Resource(T data, int idResourceMessage) {
        this.data = data;
        this.idResourceMessage = idResourceMessage;
    }
    public static <T> Resource<T> ready(T data) {
        return new Resource<>(data);
    }

    public static <T> Resource<T> ready(T data, int idResourceMessage) {
        return new Resource<>(data, idResourceMessage);
    }
}
