package com.example.eventbritetest.UI.main;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.eventbritetest.R;
import com.example.eventbritetest.UI.AbstractViewModel;
import com.example.eventbritetest.model.persistence.Event;
import com.example.eventbritetest.model.ui.UIEvent;
import com.example.eventbritetest.repository.EventRepository;
import com.example.eventbritetest.utils.LocalEventUtils;
import com.example.eventbritetest.utils.Resource;
import com.example.eventbritetest.utils.State;
import com.example.eventbritetest.utils.Status;

import java.util.List;

import javax.inject.Inject;

/**
 * @author Franco Castillo
 */
public class MainViewModel extends AbstractViewModel {
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

    public LiveData<List<UIEvent>> getEvents() {
        LiveData<Resource<List<Event>>> resource =
                mRepository.getEvents();
        mLiveEvents = Transformations.map(resource, input -> {
            mLiveLoading.setValue(false);
            mUiEvents = getUIEvents(input.data);
            return mUiEvents;
        });

        return mLiveEvents;
    }

    public LiveData<Status> getStatus() {
        LiveData<Status> status = mRepository.getStatus();
        return Transformations.map(status, input -> {
            if(input.status == State.LOADING) {
                mLiveLoading.setValue(true);
            } else {
                mLiveLoading.setValue(false);
                if(input.throwable != null) {
                    mLiveOnMessage.setValue(R.string.network_error);
                }
            }
            return input;
        });
    }

    public UIEvent getEvent(int position) {
        return mUiEvents.get(position);
    }

    public void fetchEvents(Location location) {
        mRepository.fetchEvents(location);
    }

    private List<UIEvent> getUIEvents(List<Event> events) {
        return LocalEventUtils.toUIEvents(events);
    }
}
