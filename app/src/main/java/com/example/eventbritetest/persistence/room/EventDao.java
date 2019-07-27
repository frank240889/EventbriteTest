package com.example.eventbritetest.persistence.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.eventbritetest.model.persistence.Event;

import java.util.List;

@Dao
public abstract class EventDao {
    @Query("SELECT * FROM event")
    public abstract LiveData<List<Event>> getAllEventsAsync();

    @Query("SELECT * FROM event")
    public abstract List<Event> getAllEvents();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertEvents(List<Event> events);

    @Delete
    public abstract void deleteAll(List<Event> events);

    @Query("DELETE FROM event")
    public abstract void deleteAll();

    @Query("SELECT COUNT(*) FROM event")
    public abstract Integer getCount();

    @Transaction
    public void deleteAndInsert(List<Event> events){
        deleteAll();
        insertEvents(events);
    }

}
