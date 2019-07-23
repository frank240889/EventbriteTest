package com.example.eventbritetest.di;

import android.app.Application;

import com.example.eventbritetest.persistence.room.EventDao;
import com.example.eventbritetest.persistence.room.EventRoomDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {

    @Singleton
    @Provides
    public EventRoomDatabase provideDatabase(Application application) {
        return EventRoomDatabase.getInstance(application);
    }

    @Singleton
    @Provides
    public EventDao provideEventDao(EventRoomDatabase eventRoomDatabase) {
        return eventRoomDatabase.getEventDao();
    }
}
