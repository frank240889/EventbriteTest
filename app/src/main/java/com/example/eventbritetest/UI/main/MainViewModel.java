package com.example.eventbritetest.UI.main;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.eventbritetest.R;
import com.example.eventbritetest.UI.BaseViewModel;
import com.example.eventbritetest.exception.EventdroidException;
import com.example.eventbritetest.model.persistence.Event;
import com.example.eventbritetest.model.ui.UIEvent;
import com.example.eventbritetest.repository.EventRepository;
import com.example.eventbritetest.utils.AndroidUtils;
import com.example.eventbritetest.utils.LocalEventUtils;
import com.example.eventbritetest.utils.Resource;
import com.example.eventbritetest.utils.SnackBar;
import com.example.eventbritetest.utils.State;
import com.example.eventbritetest.utils.Status;

import java.util.List;

import javax.inject.Inject;

/**
 * @author Franco Castillo
 * The view model
 */
public class MainViewModel extends BaseViewModel {
    private LiveData<List<UIEvent>> mLiveEvents;
    private EventRepository mRepository;
    private List<UIEvent> mUiEvents;

    //Inject the required dependencies by constructor
    @Inject
    public MainViewModel(@NonNull Application application,
                         EventRepository eventRepository) {
        super(application);
        mRepository = eventRepository;
    }

    public LiveData<List<UIEvent>> observeEvents() {
        LiveData<Resource<List<Event>>> resource =
                mRepository.getEvents();
        mLiveEvents = Transformations.map(resource, input -> {
            mLiveLoading.setValue(false);
            mUiEvents = getUIEvents(input.data);
            return mUiEvents;
        });

        return mLiveEvents;
    }

    public LiveData<Status> observeStatus() {
        LiveData<Status> status = mRepository.getStatus();
        return Transformations.map(status, input -> {
            if(input.status == State.LOADING) {
                mLiveLoading.setValue(true);
            } else {
                mLiveLoading.setValue(false);
                if(input.throwable != null) {
                    input.idResource = AndroidUtils.
                            stringResourceFactory(input.throwable, getApplication());
                    SnackBar snackBar = getSnackBar(input);
                    mSnackbar.setValue(snackBar);
                }
            }
            return input;
        });
    }

    private SnackBar getSnackBar(Status input) {
        if(((EventdroidException)input.throwable).getExceptionType() ==
                EventdroidException.ExceptionType.EMPTY_DATA ||
                ((EventdroidException)input.throwable).getExceptionType() ==
                        EventdroidException.ExceptionType.NO_REMAIN_DATA) {
            return SnackBar.create(input.idResource, SnackBar.NO_RESOURCE_ID, SnackBar.Action.NONE);
        }
        else {
            SnackBar.Action action = getAction(input);
            return SnackBar.create(input.idResource, R.string.retry,action);
        }

    }

    private SnackBar.Action getAction(Status input) {
        EventdroidException.ExceptionType exceptionType =
                ((EventdroidException)input.throwable).getExceptionType();

        switch (exceptionType) {
            case NO_NETWORK:
                return SnackBar.Action.NETWORK_ERROR;
            case TIME_OUT:
                return SnackBar.Action.REQUEST_FETCH_EVENTS;
            case NO_LOCATION_PROVIDED:
                return SnackBar.Action.REQUEST_LOCATION;
            case PARSING:
                return SnackBar.Action.REQUEST_FETCH_EVENTS;
                default:
                    return SnackBar.Action.ERROR;
        }
    }

    public UIEvent getEvent(int position) {
        return mUiEvents.get(position);
    }

    public void fetchEvents(Location location, boolean loadMore) {
        mRepository.fetchEvents(location, loadMore);
    }

    private List<UIEvent> getUIEvents(List<Event> events) {
        return LocalEventUtils.toUIEvents(events);
    }
}
