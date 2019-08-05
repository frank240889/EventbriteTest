package com.example.eventbritetest.persistence.room;

public class EventContract {

    public static final String DB_NAME = "events.db";
    public static final int VERSION_ONE = 1;

    public static class Column {
        public static final String ID = "id";
    }

    public static class EventsColumn extends Column {
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String URL_PREVIEW = "url_preview";
        public static final String START_DATE = "start_date";
        public static final String END_DATE = "end_date";
        public static final String CATEGORY = "category";
        public static final String CATEGORY_ID = "category_id";
    }

    public static class EventDetailColumn extends EventsColumn {
        public static final String ADDRESS = "address";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
    }


}
