package com.example.eventbritetest.repository;

import android.location.Location;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.eventbritetest.interfaces.AsyncCallback;
import com.example.eventbritetest.model.network.search.EventbriteEvent;
import com.example.eventbritetest.model.persistence.Event;
import com.example.eventbritetest.network.Constants;
import com.example.eventbritetest.network.DistanceUnit;
import com.example.eventbritetest.network.EventbriteApiService;
import com.example.eventbritetest.persistence.room.Async;
import com.example.eventbritetest.persistence.room.EventRoomDatabase;
import com.example.eventbritetest.persistence.sharedpreferences.SharedPref;
import com.example.eventbritetest.utils.EventbriteUtils;
import com.example.eventbritetest.utils.LocationUtils;
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

    @Inject
    public EventRepository(EventbriteApiService apiService,
                           EventRoomDatabase eventRoomDatabase,
                           SharedPref sharedPref) {

        mApiService = apiService;
        mEventRoomDatabase = eventRoomDatabase;
        mLiveResource = eventRoomDatabase.getEventDao().getAllEventsAsync();
        mSharedPref = sharedPref;
        mMediatorLiveResource.addSource(mLiveResource,
                new Observer<List<Event>>() {
                    @Override
                    public void onChanged(List<Event> events) {
                        mMediatorLiveResource.setValue(Resource.done(events));
                    }
                });

        mSeekRangeRadius = getCurrentSeekingRangeRadius();
        mCurrentLocation = getCurrentLocation();
        mCurrentDistanceUnit = getCurrentDistanceUnit();
        mRangeToUpdate = getMaximumMetersRangeToUpdate();
    }

    public LiveData<Resource<List<Event>>> getEvents() {
        return mMediatorLiveResource;
    }

    public LiveData<Status> getStatus() {
        return mLiveStatus;
    }

    public void fetchEvents(Location newLocation) {
        if(mCurrentLocation == null);
            mCurrentLocation = newLocation;

        float userOffset = LocationUtils.distanceBetween(newLocation, mCurrentLocation);
        boolean userHasMovedEnoughToUpdate = userHasMovedEnough(userOffset);
        //Will be activated when setting to search event from user location or custom location is implemented.
        //boolean isSameLocation = isSameLocation(newLocation);
        Log.d(EventRepository.class.getName(),
                "user has moved: " + userOffset + "so user has moved enough:" + userHasMovedEnoughToUpdate);
        DistanceUnit distanceUnit = getCurrentDistanceUnit();
        boolean distanceUnitHasChanged = distanceUnitHasChanged(distanceUnit);

        int seekRange = getCurrentSeekingRangeRadius();
        boolean seekRangeHasChanged = seekRangeRadiusHasChanged(seekRange);

        boolean hasChangedMaximumMetersRangeToUpdate = hasChangedMaximumMetersRangeToUpdate();

        new Async.Count(mEventRoomDatabase.getEventDao()).setCallback(new AsyncCallback<Void, Void, Integer, Void, Void>() {
            @Override
            public void onResult(Integer result) {
                if(result == 0 || userHasMovedEnoughToUpdate ||
                        distanceUnitHasChanged || seekRangeHasChanged || hasChangedMaximumMetersRangeToUpdate) {

                    mParams.put(EventbriteApiService.LOCATION_WITHIN, currentSeekRangeRadius());
                    mParams.put(EventbriteApiService.LOCATION_LATITUDE, String.valueOf(mCurrentLocation.getLatitude()));
                    mParams.put(EventbriteApiService.LOCATION_LONGITUDE, String.valueOf(mCurrentLocation.getLongitude()));
                    fetchFromRemote(mParams);
                }
                else {
                    mLiveStatus.setValue(Status.done());
                }
            }
        }).executeOnExecutor(Executors.newCachedThreadPool());
    }

    private void fetchFromRemote(HashMap<String, String> params) {
        if(mEventbriteCall != null && !mEventbriteCall.isExecuted())
            mEventbriteCall.cancel();

        mEventbriteCall = mApiService.fetchEvents(params);
        mLiveStatus.setValue(Status.busy());
        mEventbriteCall.enqueue(new Callback<EventbriteEvent>() {
            @Override
            public void onResponse(@NotNull Call<EventbriteEvent> call, @NotNull Response<EventbriteEvent> response) {
                List<Event> events;
                if(response.body() != null && response.body().getEvents() != null) {
                    events = EventbriteUtils.toPersistenceEvents(response.body());
                    new Async.Create(mEventRoomDatabase.getEventDao()).executeOnExecutor(Executors.newCachedThreadPool(), events);
                }
                else {
                    mLiveStatus.setValue(Status.error(new Exception("No data.")));
                }
            }
            @Override
            public void onFailure(@NotNull Call<EventbriteEvent> call, @NotNull Throwable t) {
                mLiveStatus.setValue(Status.error(t));
            }
        });
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

    private boolean hasChangedMaximumMetersRangeToUpdate() {
        if(getMaximumMetersRangeToUpdate() != mRangeToUpdate) {
            mRangeToUpdate = getMaximumMetersRangeToUpdate();
            return true;
        }

        return false;
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
