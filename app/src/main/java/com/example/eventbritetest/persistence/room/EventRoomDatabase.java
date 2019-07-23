package com.example.eventbritetest.persistence.room;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.eventbritetest.model.persistence.Event;

@Database(entities = {Event.class}, version = EventContract.VERSION_ONE, exportSchema = false)
public abstract class EventRoomDatabase extends RoomDatabase {
    private static EventRoomDatabase INSTANCE;

    public static synchronized EventRoomDatabase getInstance(@NonNull Application application) {
        if(INSTANCE == null) {
            INSTANCE = Room.
                    databaseBuilder(application, EventRoomDatabase.class, EventContract.DB_NAME).
                    fallbackToDestructiveMigration().
                    build();
        }
        return INSTANCE;
    }
    public abstract EventDao getEventDao();

}
