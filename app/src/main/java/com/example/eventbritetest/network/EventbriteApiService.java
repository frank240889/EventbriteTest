package com.example.eventbritetest.network;

import com.example.eventbritetest.BuildConfig;
import com.example.eventbritetest.model.network.eventdetail.EventDetail;
import com.example.eventbritetest.model.network.search.EventbriteEvent;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface EventbriteApiService {
    String LOCATION_WITHIN = "location.within";
    String LOCATION_LATITUDE = "location.latitude";
    String LOCATION_LONGITUDE = "location.longitude";
    String EVENT_ID = "event_id";
    double DEFAULT_LATITUDE = 19.432608;
    double DEFAULT_LONGITUDE = -99.133209;


    @GET("v3/events/search/")
    @Headers(BuildConfig.O_AUTH_TOKEN)
    Call<EventbriteEvent> fetchEvents(@QueryMap HashMap<String, String> params);

    @GET("v3/events/{event_id}")
    @Headers(BuildConfig.O_AUTH_TOKEN)
    Call<EventDetail> fetchEventDetail(@Path("event_id") String eventId,
                                       @Query("expand") String value);
}
