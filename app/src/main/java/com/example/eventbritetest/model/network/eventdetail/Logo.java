
package com.example.eventbritetest.model.network.eventdetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Logo {

    @SerializedName("crop_mask")
    @Expose
    private Object cropMask;
    @SerializedName("original")
    @Expose
    private Object original;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("aspect_ratio")
    @Expose
    private Object aspectRatio;
    @SerializedName("edge_color")
    @Expose
    private Object edgeColor;
    @SerializedName("edge_color_set")
    @Expose
    private Object edgeColorSet;

    public Object getCropMask() {
        return cropMask;
    }

    public void setCropMask(Object cropMask) {
        this.cropMask = cropMask;
    }

    public Object getOriginal() {
        return original;
    }

    public void setOriginal(Object original) {
        this.original = original;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(Object aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public Object getEdgeColor() {
        return edgeColor;
    }

    public void setEdgeColor(Object edgeColor) {
        this.edgeColor = edgeColor;
    }

    public Object getEdgeColorSet() {
        return edgeColorSet;
    }

    public void setEdgeColorSet(Object edgeColorSet) {
        this.edgeColorSet = edgeColorSet;
    }

}
