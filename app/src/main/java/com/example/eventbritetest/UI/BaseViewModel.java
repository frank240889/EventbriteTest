package com.example.eventbritetest.UI;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventbritetest.utils.ErrorState;
import com.example.eventbritetest.utils.SnackBar;

public abstract class BaseViewModel extends AndroidViewModel {
    protected LiveData<Boolean> mLiveLoading;
    protected MutableLiveData<Integer> mLiveOnMessage = new MutableLiveData<>();
    protected LiveData<SnackBar> mSnackbar;

    public BaseViewModel(@NonNull Application application) {
        super(application);
        mLiveLoading = new MutableLiveData<>();
    }

    public LiveData<Integer> observeNotificationMessage() {
        return mLiveOnMessage;
    }

    protected LiveData<Boolean> observeLoaderState() {
        return mLiveLoading;
    }

    protected LiveData<SnackBar> observeMessageState() {
        return mSnackbar;
    }

    protected SnackBar getSnackBar(ErrorState input){
        return null;
    }
}
