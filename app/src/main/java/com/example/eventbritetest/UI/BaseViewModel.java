package com.example.eventbritetest.UI;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.eventbritetest.utils.ErrorState;

public abstract class BaseViewModel extends AndroidViewModel {
    protected SingleLiveEvent<ErrorState> mErrorState;

    public BaseViewModel(@NonNull Application application) {
        super(application);
    }

    public abstract LiveData<Boolean> observeLoaderState();

    public abstract LiveData<ErrorState> observeErrorState();

}
