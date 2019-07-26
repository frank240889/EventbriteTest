package com.example.eventbritetest.utils;

import com.example.eventbritetest.model.network.search.EventbriteEvent;
import com.example.eventbritetest.model.persistence.Event;

import java.util.ArrayList;
import java.util.List;

public class EventbriteUtils {

    public static List<Event> toPersistenceEvents(EventbriteEvent eventbriteEvent) {
        List<Event> events = new ArrayList<>();
        for(com.example.eventbritetest.model.network.search.Event event : eventbriteEvent.getEvents()) {
            Event e = toPersistenceEvent(event);
            events.add(e);
        }
        return events;
    }

    public static Event toPersistenceEvent(com.example.eventbritetest.model.network.search.Event event) {
        Event e = new Event();
        e.setId(event.getId());
        e.setDescription(event.getSummary() == null || event.getSummary().isEmpty() ? "":event.getSummary().toUpperCase().trim());
        e.setName(event.getName() != null && event.getName().getText() != null ? event.getName().getText() : "");
        e.setStartDate(event.getStart().getLocal());
        e.setUrlPreview(event.getLogo() != null ? event.getLogo().getUrl() == null ? "" : event.getLogo().getUrl():"");
        return e;
    }
}
