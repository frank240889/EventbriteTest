
package com.example.eventbritetest.model.network.eventdetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Organizer {

    @SerializedName("description")
    @Expose
    private Description_ description;
    @SerializedName("long_description")
    @Expose
    private LongDescription longDescription;
    @SerializedName("logo")
    @Expose
    private Logo logo;
    @SerializedName("resource_uri")
    @Expose
    private String resourceUri;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("num_past_events")
    @Expose
    private Integer numPastEvents;
    @SerializedName("num_future_events")
    @Expose
    private Integer numFutureEvents;
    @SerializedName("facebook")
    @Expose
    private String facebook;
    @SerializedName("organization_id")
    @Expose
    private String organizationId;
    @SerializedName("disable_marketing_opt_in")
    @Expose
    private Boolean disableMarketingOptIn;
    @SerializedName("logo_id")
    @Expose
    private String logoId;

    public Description_ getDescription() {
        return description;
    }

    public void setDescription(Description_ description) {
        this.description = description;
    }

    public LongDescription getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(LongDescription longDescription) {
        this.longDescription = longDescription;
    }

    public Logo getLogo() {
        return logo;
    }

    public void setLogo(Logo logo) {
        this.logo = logo;
    }

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getNumPastEvents() {
        return numPastEvents;
    }

    public void setNumPastEvents(Integer numPastEvents) {
        this.numPastEvents = numPastEvents;
    }

    public Integer getNumFutureEvents() {
        return numFutureEvents;
    }

    public void setNumFutureEvents(Integer numFutureEvents) {
        this.numFutureEvents = numFutureEvents;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public Boolean getDisableMarketingOptIn() {
        return disableMarketingOptIn;
    }

    public void setDisableMarketingOptIn(Boolean disableMarketingOptIn) {
        this.disableMarketingOptIn = disableMarketingOptIn;
    }

    public String getLogoId() {
        return logoId;
    }

    public void setLogoId(String logoId) {
        this.logoId = logoId;
    }

}
