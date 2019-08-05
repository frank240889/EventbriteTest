package com.example.eventbritetest.di;


import android.app.Application;

import com.example.eventbritetest.EventbriteTestApplication;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Component(modules = {
        AndroidSupportInjectionModule.class,
        ApplicationModule.class,
        ContextModule.class,
        ActivityModule.class,
        FragmentModule.class,
        ChildFragmentModule.class,
        DatabaseModule.class,
        NetworkModule.class,
        SharedPreferencesModule.class,
        LocationModule.class,
        RepositoryModule.class,
        AndroidViewModelModule.class,})
@Singleton
public interface ApplicationComponent extends AndroidInjector<EventbriteTestApplication> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        ApplicationComponent build();
    }

    void inject(EventbriteTestApplication eventbriteTestApplication);
}
