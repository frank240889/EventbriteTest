package com.example.eventbritetest.utils;

import android.os.AsyncTask;

import com.example.eventbritetest.interfaces.AsyncCallback;

public abstract class AsyncTaskEventbrite<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    protected abstract <S, P, R, C, E> AsyncCallback getCallback();
    protected abstract void clear();
}
