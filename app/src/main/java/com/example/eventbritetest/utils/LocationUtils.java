package com.example.eventbritetest.utils;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventbritetest.network.Constants;

public class LocationUtils {
    /**
     * Check the distance between 2 points.
     * @param location1 The new location.
     * @param location2 The current stored location.
     * @return The number of meters between those two points.
     */
    public static float distanceBetween(@NonNull Location location1, @NonNull Location location2) {
        return location2.distanceTo(location1);
    }

    public static boolean isSameLocation(Location location1, Location location2) {

        return location1.getLatitude() == location2.getLatitude() &&
                location1.getLongitude() == location2.getLongitude();
    }
}
