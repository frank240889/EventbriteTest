package com.example.eventbritetest.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventbritetest.UI.eventdetail.EventDetailViewModel;
import com.example.eventbritetest.UI.main.MainViewModel;
import com.example.eventbritetest.UI.settings.SettingsViewModel;
import com.example.eventbritetest.viewmodel.AndroidViewModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;


/**
 * The module that provides the ViewModels.
 */
@Module
public abstract class AndroidViewModelModule {
    @Binds
    abstract ViewModelProvider.Factory bindAndroidViewModelFactory(AndroidViewModelFactory androidViewModelFactory);

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel.class)
    abstract ViewModel provideMainViewModel(MainViewModel mainViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel.class)
    abstract ViewModel provideSettingsViewModel(SettingsViewModel settingsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(EventDetailViewModel.class)
    abstract ViewModel provideEventDetailViewModeL(EventDetailViewModel eventDetailViewModel);

}
