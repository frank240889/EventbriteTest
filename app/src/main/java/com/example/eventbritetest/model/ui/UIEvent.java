package com.example.eventbritetest.model.ui;

public class UIEvent {
    private String mId;
    private String mName;
    private String mDescription;
    private String mUrlPreview;
    private String category;
    private String categoryId;
    private int mDominantColor = 0;

    public UIEvent() {
    }

    public UIEvent(String mId, String mName, String mDescription, String mUrlPreview) {
        this();
        this.mId = mId;
        this.mName = mName;
        this.mDescription = mDescription;
        this.mUrlPreview = mUrlPreview;
    }

    public UIEvent(String mId, String mName, String mDescription, String mUrlPreview, String category, String categoryId) {
        this.mId = mId;
        this.mName = mName;
        this.mDescription = mDescription;
        this.mUrlPreview = mUrlPreview;
        this.category = category;
        this.categoryId = categoryId;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public String getUrlPreview() {
        return mUrlPreview;
    }

    public void setUrlPreview(String urlPreview) {
        this.mUrlPreview = urlPreview;
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

    public int getDominantColor() {
        return mDominantColor;
    }

    public void setDominantColor(int dominantColor) {
        this.mDominantColor = dominantColor;
    }
}
