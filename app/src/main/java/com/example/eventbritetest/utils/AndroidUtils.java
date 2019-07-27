package com.example.eventbritetest.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

public class AndroidUtils {
    public static Drawable getRoundedDrawable(int backgroundColor, Context context) {
        float pxs = 16 * context.getResources().getDisplayMetrics().density;
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(backgroundColor);
        shape.setCornerRadii(new float[]{pxs,pxs,pxs,pxs,pxs,pxs,pxs,pxs});
        return shape;
    }
}
