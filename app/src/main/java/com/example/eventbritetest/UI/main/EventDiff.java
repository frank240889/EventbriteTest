package com.example.eventbritetest.UI.main;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.example.eventbritetest.model.ui.UIEvent;

import java.util.List;

public class EventDiff extends DiffUtil.Callback {
    private List<UIEvent> mCurrentList;
    private List<UIEvent> mNewList;

    public EventDiff(List<UIEvent> currentList, List<UIEvent> newList) {
        mCurrentList = currentList;
        mNewList = newList;
    }


    @Override
    public int getOldListSize() {
        return mCurrentList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        UIEvent currentUIEvent = mCurrentList.get(oldItemPosition);
        UIEvent newUIEvent = mNewList.get(newItemPosition);
        if(newUIEvent == null)
            return false;

        return currentUIEvent.getId().equals(newUIEvent.getId());

    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        UIEvent currentUIEvent = mCurrentList.get(oldItemPosition);
        UIEvent newUIEvent = mNewList.get(newItemPosition);

        if(newUIEvent == null)
            return false;

        return currentUIEvent.getId().equals(newUIEvent.getId());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        Bundle bundle = new Bundle();
        UIEvent currentUIEvent = mCurrentList.get(oldItemPosition);
        UIEvent newUIEvent = mNewList.get(newItemPosition);

        if(currentUIEvent.getId().equals(newUIEvent.getId())) {
            bundle.putString("event_name", newUIEvent.getName());
            bundle.putString("url_logo", newUIEvent.getUrlPreview());
        }

        return bundle;

    }
}
