package com.example.eventbritetest.utils;

import android.graphics.Color;

import androidx.annotation.ColorInt;

public class ColorUtils {

    public static int invertColor(@ColorInt int color) {
        String hex = Integer.toHexString(color);
        int n = (int) Long.parseLong(hex, 16);
        int red = Color.red(n);
        int green = Color.green(n);
        int blue = Color.blue(n);

        int negativeRed = 255 - red;
        int negativeGreen = 255 - green;
        int negativeBlue = 255 - blue;

        return Color.rgb(negativeRed, negativeGreen, negativeBlue);


    }

}
