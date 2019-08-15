package com.example.eventbritetest.UI.main;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.eventbritetest.UI.BaseViewModel;
import com.example.eventbritetest.model.persistence.Event;
import com.example.eventbritetest.model.ui.UIEvent;
import com.example.eventbritetest.network.DistanceUnit;
import com.example.eventbritetest.network.EventbriteApiService;
import com.example.eventbritetest.persistence.sharedpreferences.SharedPref;
import com.example.eventbritetest.repository.EventRepository;
import com.example.eventbritetest.utils.Constants;
import com.example.eventbritetest.utils.ErrorState;
import com.example.eventbritetest.utils.LoaderState;
import com.example.eventbritetest.utils.LocalEventUtils;
import com.example.eventbritetest.utils.Resource;

import java.util.List;

import javax.inject.Inject;

/**
 * @author Franco Castillo
 * The view model
 */
public class MainViewModel extends BaseViewModel {
    private LiveData<Boolean> mLiveLoading;
    private LiveData<List<UIEvent>> mLiveEvents;
    private EventRepository mRepository;
    private List<UIEvent> mUiEvents;
    private boolean mLoadingMore;
    private SharedPref mSharedPref;

    //Inject the required dependencies by constructor
    @Inject
    public MainViewModel(@NonNull Application application,
                         EventRepository eventRepository,
                         SharedPref sharedPref) {
        super(application);
        mRepository = eventRepository;
        mSharedPref = sharedPref;
    }

    public LiveData<List<UIEvent>> observeEvents() {
        LiveData<Resource<List<Event>>> resource = mRepository.getEvents();
        mLiveEvents = Transformations.map(resource, input -> {
            mUiEvents = getUIEvents(input.data);
            return mUiEvents;
        });
        return mLiveEvents;
    }

    @Override
    public LiveData<Boolean> observeLoaderState() {
        LiveData<LoaderState> status = mRepository.observeLoaderState();
        mLiveLoading = Transformations.map(status, new Function<LoaderState, Boolean>() {
            @Override
            public Boolean apply(LoaderState input) {
                mLoadingMore = input.loading;
                return input.loading;
            }
        });
        return mLiveLoading;
    }

    @Override
    public LiveData<ErrorState> observeErrorState() {
        return mRepository.observeErrorState();
    }

    public UIEvent getEvent(int position) {
        return mUiEvents.get(position);
    }

    public void fetchEvents(Location location) {
        if(!mLoadingMore)
            mRepository.fetchEvents(location);
    }

    public void fetchMoreEvents() {
        if(!mLoadingMore)
            mRepository.fetchMoreEvents();
    }

    private List<UIEvent> getUIEvents(List<Event> events) {
        return LocalEventUtils.toUIEvents(events);
    }

    private DistanceUnit getCurrentDistanceUnit() {
        return DistanceUnit.getUnit(mSharedPref.getString(Constants.DISTANCE_UNIT));
    }

    private int getCurrentSeekingRangeRadius() {
        int storeRadiusRange = mSharedPref.getInt(EventbriteApiService.LOCATION_WITHIN);
        return storeRadiusRange == -1 ? 10: storeRadiusRange;
    }
    public String currentSeekRangeRadius() {
        return getCurrentSeekingRangeRadius() + " " +  getCurrentDistanceUnit().toString().toLowerCase();
    }

    @Override
    protected void onCleared() {
        mRepository.cancelFetching();
        mRepository = null;
        mSharedPref = null;
    }
}
