package com.example.eventbritetest.di;

import android.content.Context;

import com.example.eventbritetest.persistence.sharedpreferences.SharedPref;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SharedPreferencesModule {

    @Singleton
    @Provides
    public SharedPref provideSharedPreferences(Context application) {
        return new SharedPref(application);
    }
}
