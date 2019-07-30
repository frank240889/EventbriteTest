package com.example.eventbritetest.utils;

import androidx.annotation.ColorInt;

public class ColorUtils {

    public static int invertColor(@ColorInt int color) {
        return  0xff000000 | ~color;
    }

}
