package com.example.eventbritetest.di;

import android.app.Application;

import com.example.eventbritetest.utils.ContextUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {

    @Singleton
    @Provides
    ContextUtils provideContextUtils(Application application) {
        return ContextUtils.init(application);
    }
}
