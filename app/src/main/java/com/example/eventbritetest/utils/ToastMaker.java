package com.example.eventbritetest.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

public final class ToastMaker {
    public static void show(@NonNull Context context, @StringRes int id) {
        String text = context.getString(id);
        show(context, text);
    }

    public static void show(@NonNull Context context, String text) {
        Toast toast = getToast(context, text);
        toast.show();
    }

    @SuppressLint("ShowToast")
    private static Toast getToast(@NonNull Context context, String message) {
        Toast toast =  Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        return toast;
    }
}
