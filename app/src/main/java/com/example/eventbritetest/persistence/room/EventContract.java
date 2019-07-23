package com.example.eventbritetest.persistence.room;

public class EventContract {

    public static final String DB_NAME = "events.db";
    public static final int VERSION_ONE = 1;

    public static final class Column {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String START_DATE = "start_date";
        public static final String DISTANCE = "distance";


    }

}
