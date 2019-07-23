package com.example.eventbritetest.persistence.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.eventbritetest.model.persistence.Event;

import java.util.List;

@Dao
public interface EventDao {
    @Query("SELECT * FROM event")
    LiveData<List<Event>> getAllEventsAsync();

    @Query("SELECT * FROM event")
    List<Event> getAllEvents();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEvents(List<Event> events);

    @Delete
    void deleteAll(List<Event> events);

    @Query("DELETE FROM event")
    void deleteAll();

    @Query("SELECT COUNT(*) FROM event")
    Integer getCount();

}
