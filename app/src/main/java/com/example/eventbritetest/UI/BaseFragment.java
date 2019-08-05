package com.example.eventbritetest.UI;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventbritetest.utils.SnackBar;
import com.example.eventbritetest.viewmodel.AndroidViewModelFactory;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Base fragment that provides the default funcionality for all fragments, including dependency injection
 * of its view model
 * @param <V> The type of view model.
 */
public abstract class BaseFragment<V> extends Fragment implements HasSupportFragmentInjector {
    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;
    protected V mViewModel;
    @Inject
    protected AndroidViewModelFactory androidViewModelFactory;
    public BaseFragment() { }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = getViewModel();
    }

    public abstract V getViewModel();

    protected abstract void onLoading(Boolean isLoading);

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    protected abstract void onSnackbarMessage(SnackBar snackBar);

    protected abstract void createSnackbarWithAction(SnackBar snackBar);

    protected abstract View.OnClickListener getOnClickListener(SnackBar snackBar);

    protected void onNotificationMessage(String message) {}
}
