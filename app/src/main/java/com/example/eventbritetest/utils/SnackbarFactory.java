package com.example.eventbritetest.utils;

import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;

public class SnackbarFactory {
    private SnackbarFactory() {}

    public static Snackbar create(@NonNull SnackBar snackBar, @NonNull View view) {
        return createSnackbar(snackBar, view);
    }


    private static Snackbar createSnackbar(SnackBar snackBar, View view) {
        return RoundedSnackbar.make(view, snackBar.getMessageResourceId(), Snackbar.LENGTH_SHORT);
    }


}
