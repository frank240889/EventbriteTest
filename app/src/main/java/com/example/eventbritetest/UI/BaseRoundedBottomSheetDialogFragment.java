package com.example.eventbritetest.UI;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.eventbritetest.R;
import com.example.eventbritetest.viewmodel.AndroidViewModelFactory;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;


/**
 * @author Franco Castillo
 * A simple bottom sheet dialog fragment with rounded corners.
 */
public abstract class BaseRoundedBottomSheetDialogFragment<V> extends BottomSheetDialogFragment {
    @Inject
    protected AndroidViewModelFactory androidViewModelFactory;

    protected V mViewModel;
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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(getContext(), getTheme());
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

    protected void onLoading(Boolean isLoading){}

    public abstract V getViewModel();
}
