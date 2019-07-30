package com.example.eventbritetest.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import androidx.annotation.IntegerRes;

import com.example.eventbritetest.R;
import com.example.eventbritetest.exception.EventdroidException;

import java.net.UnknownHostException;

public class AndroidUtils {

    public static Drawable getRoundedCornersDrawable(int backgroundColor, Context context) {
        float pxs = 16 * context.getResources().getDisplayMetrics().density;
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(backgroundColor);
        shape.setCornerRadii(new float[]{pxs,pxs,pxs,pxs,pxs,pxs,pxs,pxs});
        return shape;
    }

    @IntegerRes
    public static int stringResourceFactory(Throwable throwable) {
        @IntegerRes int resourceId;
        if(throwable instanceof EventdroidException) {
            EventdroidException eventdroidException = (EventdroidException) throwable;
            resourceId = getMessage(eventdroidException);
        }
        else if(throwable instanceof UnknownHostException) {
            resourceId = R.string.network_error;
        }
        else {
            resourceId = R.string.unknown_error;
        }

        return resourceId;
    }

    private static int getMessage(EventdroidException eventdroidException) {
        EventdroidException.ExceptionType exceptionType = eventdroidException.getExceptionType();

        switch (exceptionType) {
            case PARSING:
                return R.string.cannot_process_response;
            case TIME_OUT:
                return R.string.timeout;
            case EMPTY_DATA:
                return R.string.no_data_available;
            case NO_REMAIN_DATA:
                return R.string.no_remain_data;
            case NO_NETWORK:
                return R.string.network_error;
            case INVALID_LOCATION:
                return R.string.invalid_location;
            case NO_LOCATION_PROVIDED:
                return R.string.location_error;
                default:
                    return R.string.unknown_error;
        }



    }
}
