package com.example.eventbritetest.di;

import com.example.eventbritetest.network.EventbriteApiService;
import com.example.eventbritetest.persistence.room.EventRoomDatabase;
import com.example.eventbritetest.persistence.sharedpreferences.SharedPref;
import com.example.eventbritetest.repository.EventRepository;
import com.example.eventbritetest.utils.ContextUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Franco Castillo
 * A module to provide the repository object.
 */
@Module
public class RepositoryModule {

    @Singleton
    @Provides
    public EventRepository providesEventRepository(EventbriteApiService apiService,
                                                   EventRoomDatabase eventRoomDatabase,
                                                   SharedPref sharedPref,
                                                   ContextUtils contextUtils) {
        return new EventRepository(apiService, eventRoomDatabase, sharedPref, contextUtils);

    }
}
