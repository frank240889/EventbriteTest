package com.example.eventbritetest.di;

import android.content.Context;

import com.example.eventbritetest.persistence.sharedpreferences.SharedPref;
import com.example.eventbritetest.utils.LocationLiveData;

import dagger.Module;
import dagger.Provides;

@Module
public class LocationModule {

    @Provides
    public LocationLiveData provideLocationLiveData(Context context, SharedPref sharedPref) {
        return LocationLiveData.getInstance(context, sharedPref);
    }

}
