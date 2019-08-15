package com.example.eventbritetest.utils;

import androidx.annotation.StringRes;

import com.example.eventbritetest.R;

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

    public static SnackBar build(@StringRes int resourceIdMessage, @StringRes Integer resourceIdAction, Action action) {
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

    public static SnackBar create(ErrorState input) {
        switch (input.error) {
            case LOCATION:
                return SnackBar.build(R.string.location_error, R.string.retry, SnackBar.Action.REQUEST_LOCATION);
            case NETWORK:
                return SnackBar.build(R.string.network_error, R.string.retry, SnackBar.Action.REQUEST_FETCH_EVENTS);
            case PARSING:
                return SnackBar.build(R.string.cannot_process_response, R.string.retry, SnackBar.Action.REQUEST_FETCH_EVENTS);
            case EMPTY_DATA:
            case NO_MORE_DATA:
                return SnackBar.build(R.string.no_remain_data, SnackBar.NO_RESOURCE_ID, SnackBar.Action.NONE);
            case NO_MORE_DATA_NETWORK:
                return SnackBar.build(R.string.network_error, R.string.retry, SnackBar.Action.REQUEST_MORE_EVENTS);
            case NO_MORE_DATA_PARSING:
                return SnackBar.build(R.string.cannot_process_response, R.string.retry, SnackBar.Action.REQUEST_MORE_EVENTS);
            default:
                return SnackBar.build(R.string.unknown_error, SnackBar.NO_RESOURCE_ID, SnackBar.Action.NONE);
        }
    }

    public enum Action {
        NONE,
        REQUEST_LOCATION_PERMISSION,
        REQUEST_LOCATION,
        REQUEST_FETCH_EVENTS,
        REQUEST_MORE_EVENTS,
        REQUEST_EVENT_DETAIL
    }

}
