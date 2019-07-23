package com.example.eventbritetest.di;

import com.example.eventbritetest.UI.main.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * The module that injects the dependencies into fragments.
 */
@Module
public abstract class ActivityModule {
    @ContributesAndroidInjector(modules = FragmentModule.class)
    public abstract MainActivity contributeMainActivity();
}
