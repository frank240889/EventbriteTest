package com.example.eventbritetest.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationLiveData extends LiveData<Location> implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public interface OnLocationErrorListener {
        void onErrorLocation(Throwable throwable);
    }

    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient mLocationManager;
    private OnLocationErrorListener mLocationErrorListener;
    private static LocationLiveData INSTANCE;

    private LocationLiveData(Context context) {
        mLocationManager = LocationServices.getFusedLocationProviderClient(context);
        mGoogleApiClient = new GoogleApiClient.Builder(context, this, this)
                .addApi(LocationServices.API)
                .build();
    }

    public static LocationLiveData getInstance(Context context) {
        if(INSTANCE == null) {
            INSTANCE = new LocationLiveData(context);
        }
        return INSTANCE;
    }

    public LocationLiveData addLocationErrorListener(OnLocationErrorListener locationChangeListener) {
        mLocationErrorListener = locationChangeListener;
        return this;

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
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLocation();
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

    @Override
    public void onLocationChanged(Location location) {
        setValue(location);
    }

    @SuppressLint("MissingPermission")
    public void getLocation() {
        mLocationManager.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                setValue(location);
            }
        });
    }

}
