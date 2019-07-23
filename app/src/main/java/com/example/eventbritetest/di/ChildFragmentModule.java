package com.example.eventbritetest.di;

import com.example.eventbritetest.UI.eventdetail.EventDetailFragment;
import com.example.eventbritetest.UI.settings.SettingsFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ChildFragmentModule {

    @ContributesAndroidInjector(modules = FragmentModule.class)
    abstract SettingsFragment contributeSettingsFragment();

    @ContributesAndroidInjector(modules = FragmentModule.class)
    abstract EventDetailFragment contributeEventDetailFragment();
}
