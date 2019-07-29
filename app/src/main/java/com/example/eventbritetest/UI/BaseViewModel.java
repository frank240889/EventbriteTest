package com.example.eventbritetest.UI;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventbritetest.utils.SnackBar;
import com.example.eventbritetest.utils.Status;

public abstract class BaseViewModel extends AndroidViewModel {
    protected MutableLiveData<Boolean> mLiveLoading;
    protected MutableLiveData<Integer> mLiveOnMessage = new MutableLiveData<>();
    protected MutableLiveData<SnackBar> mSnackbar = new MutableLiveData<>();
    protected LiveData<Status> mLiveStatus = new MutableLiveData<>();

    public BaseViewModel(@NonNull Application application) {
        super(application);
        mLiveLoading = new MutableLiveData<>();
    }

    public LiveData<Boolean> observeLoadingState() {
        return mLiveLoading;
    }

    public LiveData<Integer> observeNotificationMessage() {
        return mLiveOnMessage;
    }

    public LiveData<SnackBar> observeSnackbarMessage() {
        return mSnackbar;
    }
    public LiveData<Status> observeStatus() {
        return mLiveStatus;
    }
}
