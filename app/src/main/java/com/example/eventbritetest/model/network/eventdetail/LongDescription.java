
package com.example.eventbritetest.model.network.eventdetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LongDescription {

    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("html")
    @Expose
    private String html;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

}
