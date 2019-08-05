package com.example.eventbritetest.UI.main;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.eventbritetest.R;
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
import com.example.eventbritetest.utils.SnackBar;

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
        LiveData<Resource<List<Event>>> resource =
                mRepository.getEvents();
        mLiveEvents = Transformations.map(resource, new Function<Resource<List<Event>>, List<UIEvent>>() {
            @Override
            public List<UIEvent> apply(Resource<List<Event>> input) {
                mUiEvents = getUIEvents(input.data);
                return mUiEvents;
            }
        });

        return mLiveEvents;
    }

    @Override
    protected LiveData<Boolean> observeLoaderState() {
        LiveData<LoaderState> status = mRepository.observeLoaderState();
        mLiveLoading = Transformations.map(status, new Function<LoaderState, Boolean>() {
            @Override
            public Boolean apply(LoaderState input) {
                mLoadingMore = input.loading;
                /*String message;
                if(mUiEvents.size() == 1) {
                    message = getApplication().getString(R.string.event_number_message);
                }
                else {
                    message = getApplication().getString(R.string.events_number_message);
                }
                String title = String.format(message,
                        mUiEvents.size(), currentSeekRangeRadius());

                mLiveOnMessage.setValue(title);*/
                return input.loading;
            }
        });
        return mLiveLoading;
    }

    @Override
    protected LiveData<SnackBar> observeSnackbarMessage() {
        LiveData<ErrorState> error = mRepository.observeErrorState();
        mSnackbar = Transformations.map(error, input -> getSnackBar(input));
        return mSnackbar;
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

    @Override
    protected SnackBar getSnackBar(ErrorState input) {
        switch (input.error) {
            case LOCATION:
                return SnackBar.create(R.string.location_error, R.string.retry, SnackBar.Action.REQUEST_LOCATION);
            case NETWORK:
                return SnackBar.create(R.string.network_error, R.string.retry, SnackBar.Action.REQUEST_FETCH_EVENTS);
            case PARSING:
                return SnackBar.create(R.string.cannot_process_response, R.string.retry, SnackBar.Action.REQUEST_FETCH_EVENTS);
            case EMPTY_DATA:
            case NO_MORE_DATA:
                return SnackBar.create(R.string.no_remain_data, SnackBar.NO_RESOURCE_ID, SnackBar.Action.NONE);
            case NO_MORE_DATA_NETWORK:
                return SnackBar.create(R.string.network_error, R.string.retry, SnackBar.Action.REQUEST_MORE_EVENTS);
            case NO_MORE_DATA_PARSING:
                return SnackBar.create(R.string.cannot_process_response, R.string.retry, SnackBar.Action.REQUEST_MORE_EVENTS);
            default:
                return SnackBar.create(R.string.unknown_error, SnackBar.NO_RESOURCE_ID, SnackBar.Action.NONE);
        }
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
