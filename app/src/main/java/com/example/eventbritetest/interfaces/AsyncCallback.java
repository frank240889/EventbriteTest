package com.example.eventbritetest.interfaces;

/**
 * An interface for async callbacks.
 * @param <S> The type of Start params.
 * @param <P> The type of output Progress.
 * @param <R> The type of output Results.
 * @param <C> The type of Cancellation output.
 * @param <E> The type of Error if any.
 */
public interface AsyncCallback<S, P, R, C, E> {
    default void onStart(S start){}
    default void onProgress(P progress){}
    default void onResult(R result){}
    default void onCancelled(C canceled){}
    default void onError(E error){}
}
