package com.example.eventbritetest.repository;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

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
import com.example.eventbritetest.utils.Resource;
import com.example.eventbritetest.utils.Status;

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
    private int mCurrentRangeRadius;
    // The observable data.
    private LiveData<List<Event>> mLiveResource;
    // The exposed observable to combine different sources.
    private MediatorLiveData<Resource<List<Event>>> mMediatorLiveResource;
    private SharedPref mSharedPref;
    private HashMap<String, String> mParams = new HashMap<>();
    private MutableLiveData<Status> mLiveStatus;

    @Inject
    public EventRepository(EventbriteApiService apiService,
                           EventRoomDatabase eventRoomDatabase,
                           SharedPref sharedPref) {

        mApiService = apiService;
        mEventRoomDatabase = eventRoomDatabase;
        mLiveResource = eventRoomDatabase.getEventDao().getAllEventsAsync();
        mLiveStatus = new MutableLiveData<>();
        mMediatorLiveResource = new MediatorLiveData<>();
        mLiveStatus.setValue(Status.busy());
        mMediatorLiveResource.addSource(mLiveResource,
                events -> mMediatorLiveResource.setValue(Resource.done(events)));
        mSharedPref = sharedPref;
        int storeRadiusRange = sharedPref.getInt(EventbriteApiService.LOCATION_WITHIN);
        mCurrentRangeRadius = storeRadiusRange == -1 ? 10: storeRadiusRange;
    }

    public LiveData<Resource<List<Event>>> getEvents() {
        return mMediatorLiveResource;
    }

    public LiveData<Status> getStatus() {
        return mLiveStatus;
    }

    public void fetchEvents(Location location) {
        String lat = getCurrentLatitude();
        String lgt = getCurrentLongitude();

        if(lat != null && lgt != null) {
            double latitude = Double.valueOf(lat);
            double longitude = Double.valueOf(lgt);

            Location savedLocation = new Location("saved_location");
            savedLocation.setLatitude(latitude);
            savedLocation.setLongitude(longitude);

            float distanceUserHasMoved = savedLocation.distanceTo(location);
            int lastRangeRadius = getCurrentRangeRadius();
            String unit = getCurrentDistanceUnit();
            String lastRangeRadiusStr = lastRangeRadius + unit.toLowerCase();

            String finalLat = lat;
            String finalLgt = lgt;

            new Async.Count(mEventRoomDatabase.getEventDao()).setCallback(new AsyncCallback<Void, Void, Integer, Void, Void>() {
                @Override
                public void onResult(Integer result) {
                    mParams.put(EventbriteApiService.LOCATION_WITHIN, lastRangeRadiusStr);
                    mParams.put(EventbriteApiService.LOCATION_LATITUDE, finalLat);
                    mParams.put(EventbriteApiService.LOCATION_LONGITUDE, finalLgt);
                    if(result == 0 || distanceUserHasMoved > 1000 || lastRangeRadius != mCurrentRangeRadius) {
                        mCurrentRangeRadius = lastRangeRadius;
                        fetchFromRemote(mParams);
                    }
                    else {
                        fetchFromLocal();
                    }

                }
            }).executeOnExecutor(Executors.newCachedThreadPool());
        }
        else {
            lat = String.valueOf(location.getLatitude());
            lgt = String.valueOf(location.getLongitude());

            mSharedPref.putInt(EventbriteApiService.LOCATION_WITHIN, Constants.DEFAULT_DISTANCE);
            mSharedPref.putString(EventbriteApiService.LOCATION_LATITUDE, lat);
            mSharedPref.putString(EventbriteApiService.LOCATION_LONGITUDE, lgt);

            mParams.put(EventbriteApiService.LOCATION_WITHIN, mCurrentRangeRadius+"km");
            mParams.put(EventbriteApiService.LOCATION_LATITUDE, lat);
            mParams.put(EventbriteApiService.LOCATION_LONGITUDE, lgt);
            fetchFromRemote(mParams);
        }
    }

    private String getCurrentDistanceUnit() {
        return DistanceUnit.getUnit(mSharedPref.getString(Constants.DISTANCE_UNIT)).toString();
    }

    private void fetchFromRemote(HashMap<String, String> params){
        mLiveStatus.setValue(Status.busy());
        Call<EventbriteEvent> eventbriteCall = mApiService.fetchEvents(params);
        eventbriteCall.enqueue(new Callback<EventbriteEvent>() {
            @Override
            public void onResponse(Call<EventbriteEvent> call, Response<EventbriteEvent> response) {
                List<Event> events;
                if(response.body() != null && response.body().getEvents() != null) {
                    events = EventbriteUtils.toPersistenceEvents(response.body());
                    insertEvents(events);
                }
                else {
                    mLiveStatus.setValue(Status.error(new Exception("No data.")));
                }
            }
            @Override
            public void onFailure(Call<EventbriteEvent> call, Throwable t) {
                mLiveStatus.setValue(Status.error(t));
            }
        });
    }

    private void fetchFromLocal() {
        new Async.Read(mEventRoomDatabase.getEventDao()).
                setCallback(new AsyncCallback<Void, Void, List<Event>, Void, Void>() {
                    @Override
                    public void onStart(Void start) {
                        mLiveStatus.setValue(Status.busy());
                    }
                    @Override
                    public void onResult(List<Event> result) {
                        mMediatorLiveResource.setValue(Resource.done(result));
                    }
        }).executeOnExecutor(Executors.newCachedThreadPool());
    }

    private void insertEvents(List<Event> input) {
        new Async.Create(mEventRoomDatabase.getEventDao()).executeOnExecutor(Executors.newCachedThreadPool(), input);
    }

    private int getCurrentRangeRadius() {
        return mSharedPref.getInt(EventbriteApiService.LOCATION_WITHIN);
    }

    private String getCurrentLatitude() {
        return mSharedPref.getString(EventbriteApiService.LOCATION_LATITUDE);
    }

    private String getCurrentLongitude() {
        return mSharedPref.getString(EventbriteApiService.LOCATION_LONGITUDE);
    }
}
