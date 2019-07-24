package com.example.eventbritetest.utils;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.StringRes;

import com.example.eventbritetest.R;
import com.google.android.material.snackbar.Snackbar;

public class RoundedSnackbar {

    public static Snackbar make(View view, @StringRes int id, int duration) {
        return make(view, view.getContext().getString(id), duration);
    }

    public static Snackbar make(View view, CharSequence text, int duration) {
        Snackbar snackbar = Snackbar.make(view, text, duration);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) snackbar.getView().getLayoutParams();
        params.setMargins(12, 12, 12, 12);
        snackbar.getView().setLayoutParams(params);
        snackbar.getView().setBackground(view.getContext().getDrawable(R.drawable.bg_rounded_snackbar));
        return snackbar;
    }
}
