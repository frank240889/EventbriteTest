package com.example.eventbritetest.utils;

public class Resource<T> {
    public T data;
    private Resource(T data) {
        this.data = data;
    }

    public static <T> Resource<T> ready(T data) {
        return new Resource<>(data);
    }
}
