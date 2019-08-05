package com.example.eventbritetest.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventbritetest.model.persistence.Event;
import com.example.eventbritetest.model.ui.UIEvent;
import com.example.eventbritetest.network.DistanceUnit;

import java.util.ArrayList;
import java.util.List;

public class LocalEventUtils {

    @NonNull
    public static List<UIEvent> toUIEvents(@Nullable List<Event> eventList) {
        List<UIEvent> uiEvents = new ArrayList<>();
        if(eventList != null) {
            for(Event event : eventList) {
                UIEvent uiEvent = toUIEvent(event);
                uiEvents.add(uiEvent);
            }
        }
        return uiEvents;
    }

    public static UIEvent toUIEvent(Event event) {
        return new UIEvent(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getUrlPreview(),
                event.getCategory(),
                event.getCategoryId()
        );
    }

    public static String getDistance(int rawDistance, DistanceUnit distanceUnit) {
        return rawDistance + distanceUnit.name();
    }


}
