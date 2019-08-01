package com.example.eventbritetest.utils;

public class LoaderState {
    public boolean loading;
    private LoaderState(boolean loading){
        this.loading = loading;
    }

    public static LoaderState loading(){
        return new LoaderState(true);
    }

    public static LoaderState done(){
        return new LoaderState(false);
    }
}
