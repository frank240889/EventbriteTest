package com.example.eventbritetest.repository;

import java.util.List;

public interface Repository<T> {
    default void create(T input){}
    default void read(String id){}
    default void update(T input){}
    default void delete(String id){}
    default void create(List<T> inputs){}
}
