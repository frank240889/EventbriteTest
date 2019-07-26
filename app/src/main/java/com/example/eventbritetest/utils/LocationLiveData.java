package com.example.eventbritetest.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.example.eventbritetest.network.EventbriteApiService;
import com.example.eventbritetest.persistence.sharedpreferences.SharedPref;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationLiveData extends LiveData<Location> implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    public interface OnLocationErrorListener {
        void onErrorLocation(Throwable throwable);
    }

    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private OnLocationErrorListener mLocationErrorListener;
    private SharedPref sharedPref;
    private LocationCallback mLocationCallback;
    private static LocationLiveData INSTANCE;

    private LocationLiveData(Context context, SharedPref sharedPref) {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        mGoogleApiClient = new GoogleApiClient.Builder(context, this, this)
                .addApi(LocationServices.API)
                .build();
        this.sharedPref = sharedPref;
    }

    public static LocationLiveData getInstance(Context context, SharedPref sharedPref) {
        if(INSTANCE == null) {
            INSTANCE = new LocationLiveData(context, sharedPref);
        }
        return INSTANCE;
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onActive() {
        mGoogleApiClient.connect();
    }

    @Override
    protected void onInactive() {
        if(mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();

        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLocation();
        if(hasActiveObservers() &&  mGoogleApiClient.isConnected()) {
            LocationRequest locationRequest = getLocationRequest();

            mLocationCallback = getLocationCallback();
            mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.getMainLooper());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if(mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mLocationErrorListener.onErrorLocation(new Exception(connectionResult.getErrorMessage()));
    }

    @SuppressLint("MissingPermission")
    public void getLocation() {
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                setValue(location);
            }
        });
    }

    @Override
    protected void setValue(Location location) {
        if(location != null) {
            sharedPref.putStringSync(EventbriteApiService.LOCATION_LATITUDE, location.getLatitude()+"");
            sharedPref.putStringSync(EventbriteApiService.LOCATION_LONGITUDE, location.getLongitude()+"");
        }
        Log.d("GPS", "recibiendo locación");
        super.setValue(location);
    }

    private LocationCallback getLocationCallback() {
        return new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                setValue(location);
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                boolean isAvailable = locationAvailability.isLocationAvailable();
            }
        };
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        /*locationRequest.setFastestInterval(60000);
        locationRequest.setInterval(60000);
        locationRequest.setMaxWaitTime(120000);*/
        locationRequest.setSmallestDisplacement(30);
        return locationRequest;
    }
}
