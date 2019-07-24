package com.example.eventbritetest.persistence.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.eventbritetest.BuildConfig;

public class SharedPref {
    private SharedPreferences mSharedPreferences;

    public SharedPref(Context application) {
        mSharedPreferences = application.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
    }

    public int getInt(String key) {
        return mSharedPreferences.getInt(key, -1);
    }

    public void putInt(String key, int value) {
        mSharedPreferences.edit().putInt(key, value).apply();
    }

    public String getString(String key) {
        return mSharedPreferences.getString(key, null);
    }

    public void putString(String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();
    }

    public void putStringSync(String key, String value) {
        mSharedPreferences.edit().putString(key, value).commit();
    }
}
