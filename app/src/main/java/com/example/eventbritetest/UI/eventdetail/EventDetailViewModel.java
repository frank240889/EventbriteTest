package com.example.eventbritetest.UI.eventdetail;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventbritetest.R;
import com.example.eventbritetest.UI.BaseViewModel;
import com.example.eventbritetest.model.network.eventdetail.EventDetail;
import com.example.eventbritetest.network.EventbriteApiService;
import com.example.eventbritetest.utils.SnackBar;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailViewModel extends BaseViewModel {
    private MutableLiveData<String> mLiveTitle = new MutableLiveData<>();
    private MutableLiveData<String> mLiveOrganizer = new MutableLiveData<>();
    private MutableLiveData<String> mLiveDescription = new MutableLiveData<>();
    private MutableLiveData<String> mLiveDate = new MutableLiveData<>();
    private MutableLiveData<String> mLiveAddress = new MutableLiveData<>();
    private MutableLiveData<String> mLiveUrl = new MutableLiveData<>();
    private MutableLiveData<String> mLiveLogo = new MutableLiveData<>();
    private Call<EventDetail> mEventDetailCall;

    private EventbriteApiService mEventbriteApiService;

    @Inject
    public EventDetailViewModel(@NonNull Application application, EventbriteApiService eventbriteApiService) {
        super(application);
        mEventbriteApiService = eventbriteApiService;
    }

    public LiveData<String> getTitle() {
        return mLiveTitle;
    }

    public LiveData<String> getOrganizer() {
        return mLiveOrganizer;
    }

    public LiveData<String> getDescription() {
        return mLiveDescription;
    }

    public LiveData<String> getDate() {
        return mLiveDate;
    }

    public LiveData<String> getAddress() {
        return mLiveAddress;
    }

    public LiveData<String> getLogo() {
        return mLiveLogo;
    }

    public LiveData<String> getUrl() {
        return mLiveUrl;
    }


    public void fetchEvent(String idEvent) {
        String values = "organizer,venue";
        mLiveLoading.setValue(true);
        mEventDetailCall = mEventbriteApiService.fetchEventDetail(idEvent,values);
        mEventDetailCall.enqueue(new Callback<EventDetail>() {
            @Override
            public void onResponse(@NotNull Call<EventDetail> call, Response<EventDetail> response) {
                if(response.body() != null) {
                    EventDetail eventDetail = response.body();
                    processResponse(eventDetail);
                }
            }

            @Override
            public void onFailure(@NotNull Call<EventDetail> call, Throwable t) {
                processError(t);
            }
        });
    }

    @Override
    protected void onCleared() {
        if(mEventDetailCall != null && !mEventDetailCall.isCanceled())
            mEventDetailCall.cancel();

        mEventDetailCall = null;
    }

    private void processError(Throwable t) {
        mSnackbar.setValue(SnackBar.create(R.string.error_fetching_event,
                R.string.retry,
                SnackBar.Action.REQUEST_EVENT_DETAIL));
        mLiveLoading.setValue(false);
    }

    private void processResponse(EventDetail eventDetail) {
        String title = eventDetail.getName().getText().toUpperCase();
        String urlLogo = eventDetail.getLogo() != null ? eventDetail.getLogo().getUrl() : eventDetail.getUrl() ;
        String description = eventDetail.getDescription().getText().toUpperCase();
        String startDate = eventDetail.getStart().getLocal().toUpperCase();
        String end = eventDetail.getEnd().getLocal().toUpperCase();
        String address = eventDetail.getVenue().getAddress().getLocalizedAddressDisplay().toUpperCase();
        String organizer = eventDetail.getOrganizer().getName().toUpperCase();
        String facebook = eventDetail.getOrganizer().getFacebook();
        String urlDetail = eventDetail.getUrl();
        String by = getApplication().getString(R.string.by_organizer);
        String byOrganizer = String.format(by, organizer);
        mLiveTitle.setValue(title);
        mLiveOrganizer.setValue(byOrganizer);
        mLiveLogo.setValue(urlLogo);
        mLiveDescription.setValue(description);
        mLiveDate.setValue(startDate + " - " + end);
        mLiveAddress.setValue(address);
        mLiveUrl.setValue(urlDetail);
        mLiveLoading.setValue(false);
    }
}
