package com.example.eventbritetest.repository;

import android.location.Location;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventbritetest.exception.EventdroidException;
import com.example.eventbritetest.interfaces.AsyncCallback;
import com.example.eventbritetest.model.network.search.EventbriteEvent;
import com.example.eventbritetest.model.persistence.Event;
import com.example.eventbritetest.network.DistanceUnit;
import com.example.eventbritetest.network.EventbriteApiService;
import com.example.eventbritetest.persistence.room.Async;
import com.example.eventbritetest.persistence.room.EventRoomDatabase;
import com.example.eventbritetest.persistence.sharedpreferences.SharedPref;
import com.example.eventbritetest.utils.Constants;
import com.example.eventbritetest.utils.EventbriteUtils;
import com.example.eventbritetest.utils.Resource;
import com.example.eventbritetest.utils.Status;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.eventbritetest.exception.EventdroidException.ExceptionType.NO_LOCATION_PROVIDED;
import static com.example.eventbritetest.exception.EventdroidException.ExceptionType.NO_REMAIN_DATA;
import static com.example.eventbritetest.exception.EventdroidException.ExceptionType.PARSING;

public class EventRepository {

    // The API to consume.
    private EventbriteApiService mApiService;
    // The database where data is stored; this database works  as a trusted storage.
    private EventRoomDatabase mEventRoomDatabase;
    // The observable data.
    private LiveData<List<Event>> mLiveResource;
    // The exposed observable to combine different sources.
    private MediatorLiveData<Resource<List<Event>>> mMediatorLiveResource = new MediatorLiveData<>();
    private SharedPref mSharedPref;
    private HashMap<String, String> mParams = new HashMap<>();
    private MutableLiveData<Status> mLiveStatus = new MutableLiveData<>();

    private Location mCurrentLocation;
    private DistanceUnit mCurrentDistanceUnit;
    private float mRangeToUpdate;
    private int mSeekRangeRadius;
    Call<EventbriteEvent> mEventbriteCall;
    private int mCurrentPage;
    private int mTotalPages;
    private boolean mLoadingMore = false;
    private boolean mFirstRequestLocation;

    @Inject
    public EventRepository(EventbriteApiService apiService,
                           EventRoomDatabase eventRoomDatabase,
                           SharedPref sharedPref) {

        mApiService = apiService;
        mEventRoomDatabase = eventRoomDatabase;
        mSharedPref = sharedPref;
        mLiveResource = mEventRoomDatabase.getEventDao().getAllEventsAsync();
        mMediatorLiveResource.addSource(mLiveResource,
                events -> mMediatorLiveResource.setValue(Resource.done(events)));

        mSeekRangeRadius = getCurrentSeekingRangeRadius();
        mCurrentLocation = getCurrentLocation();
        mCurrentDistanceUnit = getCurrentDistanceUnit();
        mRangeToUpdate = getMaximumMetersRangeToUpdate();
        mCurrentPage = mSharedPref.getInt(EventbriteApiService.CURRENT_PAGE) == -1 ? 1 :
                mSharedPref.getInt(EventbriteApiService.CURRENT_PAGE);

        mTotalPages = mSharedPref.getInt(EventbriteApiService.TOTAL_PAGES) == -1 ? 1 :
                mSharedPref.getInt(EventbriteApiService.TOTAL_PAGES);
        mFirstRequestLocation = !mSharedPref.getBoolean(Constants.FIRST_REQUEST_LOCATION);
    }

    public LiveData<Resource<List<Event>>> getEvents() {
        return mMediatorLiveResource;
    }

    public LiveData<Status> getStatus() {
        return mLiveStatus;
    }

    public void fetchEvents(Location newLocation, boolean loadMore) {
        if(newLocation == null) {
            if(loadMore) {
                mParams.put(EventbriteApiService.CURRENT_PAGE, String.valueOf(mCurrentPage + 1));
                loadMore(mParams);
            }
            else {
                mLiveStatus.setValue(Status.error(new EventdroidException("Location is null.", NO_LOCATION_PROVIDED)));
            }
        }
        else {
            if(mFirstRequestLocation) {
                mSharedPref.putBooleanSync(Constants.FIRST_REQUEST_LOCATION, false);
            }

            mCurrentLocation = newLocation;

            DistanceUnit distanceUnit = getCurrentDistanceUnit();
            boolean distanceUnitHasChanged = distanceUnitHasChanged(distanceUnit);

            int seekRange = getCurrentSeekingRangeRadius();
            boolean seekRangeHasChanged = seekRangeRadiusHasChanged(seekRange);

            mParams.put(EventbriteApiService.LOCATION_WITHIN, currentSeekRangeRadius());
            mParams.put(EventbriteApiService.LOCATION_LATITUDE, String.valueOf(mCurrentLocation.getLatitude()));
            mParams.put(EventbriteApiService.LOCATION_LONGITUDE, String.valueOf(mCurrentLocation.getLongitude()));

            new Async.Count(mEventRoomDatabase.getEventDao()).setCallback(new AsyncCallback<Void, Void, Integer, Void, Void>() {
                @Override
                public void onResult(Integer result) {
                    if(result == 0 || distanceUnitHasChanged || seekRangeHasChanged) {
                        mParams.put(EventbriteApiService.CURRENT_PAGE, 1+"");
                        fetchFromRemote(mParams, false);
                    }
                    else {
                        mLiveStatus.setValue(Status.done());
                    }
                }
            }).executeOnExecutor(Executors.newCachedThreadPool());
        }
    }

    private void loadMore(HashMap<String, String> params) {
        if(mLoadingMore)
            return;

        mLoadingMore = true;
        if(mCurrentPage < (mTotalPages + 1)) {
          fetchFromRemote(params, true);
        }
        else {
          mLiveStatus.setValue(Status.error(new EventdroidException("No more data.", NO_REMAIN_DATA)));
          mLoadingMore = false;
        }
    }

    private void fetchFromRemote(HashMap<String, String> params, boolean loadMore) {
        mLiveStatus.setValue(Status.busy());
        mEventbriteCall = mApiService.fetchEvents(params);
        mEventbriteCall.enqueue(getCallback(loadMore));
    }

    private Callback<EventbriteEvent> getCallback(boolean loadMore) {
        if(loadMore)
            return new Callback<EventbriteEvent>() {
                @Override
                public void onResponse(@NotNull Call<EventbriteEvent> call, @NotNull Response<EventbriteEvent> response) {
                    List<Event> events;
                    if(response.isSuccessful()) {
                        if(response.body() != null && response.body().getEvents() != null) {
                            events = EventbriteUtils.toPersistenceEvents(response.body());
                            insertMore(events);
                        }
                        else {
                            mLiveStatus.setValue(Status.error(new EventdroidException("No data.", PARSING)));
                        }
                    }

                    mLoadingMore = false;
                }
                @Override
                public void onFailure(@NotNull Call<EventbriteEvent> call, @NotNull Throwable t) {
                    mLiveStatus.setValue(Status.error(new EventdroidException("Network error.", EventdroidException.ExceptionType.NO_NETWORK)));
                    mLoadingMore = false;
                }
            };

        else
            return new Callback<EventbriteEvent>() {
                @Override
                public void onResponse(@NotNull Call<EventbriteEvent> call, @NotNull Response<EventbriteEvent> response) {
                    List<Event> events;
                    if(response.body() != null && response.body().getEvents() != null) {
                        events = EventbriteUtils.toPersistenceEvents(response.body());
                        mTotalPages = response.body().getPagination().getPageCount();
                        mSharedPref.putIntSync(EventbriteApiService.TOTAL_PAGES, mTotalPages);
                        insertNews(events);
                    }
                    else {
                        mLiveStatus.setValue(Status.error(new EventdroidException("No data.", PARSING)));
                    }
                }
                @Override
                public void onFailure(@NotNull Call<EventbriteEvent> call, @NotNull Throwable t) {
                    mLiveStatus.setValue(Status.error(new EventdroidException("Network error.", EventdroidException.ExceptionType.NO_NETWORK)));
                }
            };
    }


    private void insertMore(List<Event> events) {
        new Async.Create(mEventRoomDatabase.getEventDao()).setCallback(new AsyncCallback<Void, Void, Void, Void, Void>() {
            @Override
            public void onStart(Void start) {
                LiveData<List<Event>> resource = mEventRoomDatabase.getEventDao().getAllEventsAsync();
                mMediatorLiveResource.addSource(resource,
                        e -> {
                            mMediatorLiveResource.setValue(Resource.done(e));
                            mMediatorLiveResource.removeSource(resource);
                        });
            }
            @Override
            public void onResult(Void result) {
                mCurrentPage++;
                mSharedPref.putIntSync(EventbriteApiService.CURRENT_PAGE, mCurrentPage);
            }
        }).executeOnExecutor(Executors.newCachedThreadPool(), events);
    }

    private void insertNews(List<Event> events) {
        new Async.Recreate(mEventRoomDatabase.getEventDao()).setCallback(new AsyncCallback<Void, Void, Void, Void, Void>() {
            @Override
            public void onStart(Void start) {
                LiveData<List<Event>> resource = mEventRoomDatabase.getEventDao().getAllEventsAsync();
                mMediatorLiveResource.addSource(resource,
                        e -> {
                            mMediatorLiveResource.setValue(Resource.done(e));
                            mMediatorLiveResource.removeSource(resource);
                        });
            }
            @Override
            public void onResult(Void result) {
                mCurrentPage = 1;
                mSharedPref.putIntSync(EventbriteApiService.CURRENT_PAGE, mCurrentPage);
                mSharedPref.putIntSync(EventbriteApiService.TOTAL_PAGES, mTotalPages);
            }
        }).executeOnExecutor(Executors.newCachedThreadPool(), events);
    }

    private DistanceUnit getCurrentDistanceUnit() {
        return DistanceUnit.getUnit(mSharedPref.getString(Constants.DISTANCE_UNIT));
    }

    private int getCurrentSeekingRangeRadius() {
        int storeRadiusRange = mSharedPref.getInt(EventbriteApiService.LOCATION_WITHIN);
        return storeRadiusRange == -1 ? 10: storeRadiusRange;
    }

    @Nullable
    private String getCurrentLatitude() {
        return mSharedPref.getString(EventbriteApiService.LOCATION_LATITUDE);
    }

    @Nullable
    private String getCurrentLongitude() {
        return mSharedPref.getString(EventbriteApiService.LOCATION_LONGITUDE);
    }

    private float getMaximumMetersRangeToUpdate() {
        return mSharedPref.getInt(Constants.MAXIMUM_METERS_RANGE_TO_UPDATE) <= 500 ?
                Constants.DEFAULT_RANGE_IN_METERS_TO_UPDATE : mSharedPref.getInt(Constants.MAXIMUM_METERS_RANGE_TO_UPDATE);
    }

    @Nullable
    private Location getCurrentLocation() {
        String lat = getCurrentLatitude();
        String lgt = getCurrentLongitude();

        if(lat == null || lgt == null)
            return null;

        double latitude = Double.valueOf(lat);
        double longitude = Double.valueOf(lgt);

        Location savedLocation = new Location("saved_location");
        savedLocation.setLatitude(latitude);
        savedLocation.setLongitude(longitude);
        return savedLocation;
    }

    private String currentSeekRangeRadius() {
        return mSeekRangeRadius + getCurrentDistanceUnit().toString().toLowerCase();
    }

    private boolean seekRangeRadiusHasChanged(int lastRangeRadius) {
        boolean seekRangeRadiusHasChanged = lastRangeRadius != mSeekRangeRadius;
        if(lastRangeRadius != mSeekRangeRadius)
            mSeekRangeRadius = lastRangeRadius;

        return seekRangeRadiusHasChanged;
    }

    /**
     * Check the distance between 2 points.
     * @param newLocation The new location.
     * @param savedLocation The current stored location.
     * @return The number of meters between those two points.
     */
    private float positionOffset(Location newLocation, @Nullable Location savedLocation) {
        if(savedLocation == null)
            return Constants.NO_LOCATION_SAVED;
        return savedLocation.distanceTo(newLocation);
    }

    private boolean userHasMovedEnough(float offset) {
        if(offset == Constants.NO_LOCATION_SAVED)
            return true;
        return offset > getMaximumMetersRangeToUpdate();
    }

    private boolean isSameLocation(Location newLocation) {
        boolean isSameLocation = true;
        if(mCurrentLocation == null) {
            mCurrentLocation = newLocation;
            return false;
        }

        if(newLocation.getLatitude() == mCurrentLocation.getLatitude() &&
                newLocation.getLongitude() == mCurrentLocation.getLongitude()) {
            mCurrentLocation = newLocation;
        }
        else isSameLocation = false;

        return isSameLocation;
    }

    private boolean distanceUnitHasChanged(DistanceUnit newDistanceUnit) {
        boolean distanceUnitHasChanged = mCurrentDistanceUnit != newDistanceUnit;
        mCurrentDistanceUnit = newDistanceUnit;
        return distanceUnitHasChanged;
    }

}
