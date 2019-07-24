package com.example.eventbritetest.utils;

import androidx.annotation.StringRes;

public class SnackBar {
    public static int NO_RESOURCE_ID = -1;
    private Action mAction;
    private @StringRes Integer mResourceIdAction;
    private @StringRes int mResourceIdMessage;

    private SnackBar(@StringRes int resourceIdMessage, @StringRes Integer resourceIdAction, Action action) {
        mAction = action;
        mResourceIdAction = resourceIdAction;
        mResourceIdMessage = resourceIdMessage;

    }

    public static SnackBar create(@StringRes int resourceIdMessage, @StringRes Integer resourceIdAction, Action action) {
        return new SnackBar(resourceIdMessage, resourceIdAction, action);
    }

    public Action getAction() {
        return mAction;
    }

    public Integer getActionResourceId() {
        return mResourceIdAction;
    }

    public int getMessageResourceId() {
        return mResourceIdMessage;
    }

    public enum Action {
        NONE,
        REQUEST_LOCATION_PERMISSION,
        REQUEST_LOCATION,
        REQUEST_FETCH_EVENTS,
        REQUEST_EVENT_DETAIL,
        ERROR
    }

}
