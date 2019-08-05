package com.example.eventbritetest.model.persistence;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.eventbritetest.persistence.room.EventContract;

@Entity
public class EventDetail {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = EventContract.EventsColumn.ID)
    private String id;
    @ColumnInfo(name = EventContract.EventsColumn.NAME)
    private String name;
    @ColumnInfo(name = EventContract.EventsColumn.DESCRIPTION)
    private String description;
    @ColumnInfo(name = EventContract.EventsColumn.URL_PREVIEW)
    private String urlPreview;
    @ColumnInfo(name = EventContract.EventsColumn.START_DATE)
    private String startDate;
    @ColumnInfo(name = EventContract.EventsColumn.END_DATE)
    private String endDate;
    @ColumnInfo(name = EventContract.EventsColumn.CATEGORY)
    private String category = "";
    @ColumnInfo(name = EventContract.EventsColumn.CATEGORY_ID)
    private String categoryId = "";

    public EventDetail() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlPreview() {
        return urlPreview;
    }

    public void setUrlPreview(String urlPreview) {
        this.urlPreview = urlPreview;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
