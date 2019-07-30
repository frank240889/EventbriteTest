package com.example.eventbritetest.model.ui;

public class UIEvent {
    private String mId;
    private String mName;
    private String mDescription;
    private String mUrlPreview;
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

    public int getDominantColor() {
        return mDominantColor;
    }

    public void setDominantColor(int dominantColor) {
        this.mDominantColor = dominantColor;
    }
}
