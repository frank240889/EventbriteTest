package com.example.eventbritetest.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

public class AndroidUtils {

    public static Drawable getRoundedCornersDrawable(int backgroundColor, Context context) {
        float pxs = dpToPx(16,context);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(backgroundColor);
        shape.setCornerRadii(new float[]{pxs,pxs,pxs,pxs,0,0,0,0});
        return shape;
    }

    public static float dpToPx(int dp, Context context) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}
