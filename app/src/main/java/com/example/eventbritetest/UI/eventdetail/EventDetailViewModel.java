package com.example.eventbritetest.UI.eventdetail;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.eventbritetest.R;
import com.example.eventbritetest.UI.BaseViewModel;
import com.example.eventbritetest.model.network.eventdetail.EventDetail;
import com.example.eventbritetest.network.EventbriteApiService;
import com.example.eventbritetest.utils.ErrorState;
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
    private MutableLiveData<Boolean> booleanMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<ErrorState> errorStateMutableLiveData = new MutableLiveData<>();

    private EventbriteApiService mEventbriteApiService;

    @Inject
    public EventDetailViewModel(@NonNull Application application, EventbriteApiService eventbriteApiService) {
        super(application);
        mEventbriteApiService = eventbriteApiService;
    }

    public LiveData<String> observeTitle() {
        return mLiveTitle;
    }

    public LiveData<String> observeOrganizer() {
        return mLiveOrganizer;
    }

    public LiveData<String> observeDescription() {
        return mLiveDescription;
    }

    public LiveData<String> observeDate() {
        return mLiveDate;
    }

    public LiveData<String> observeAddress() {
        return mLiveAddress;
    }

    public LiveData<String> observeLogo() {
        return mLiveLogo;
    }

    public LiveData<String> observeUrl() {
        return mLiveUrl;
    }


    public void fetchEvent(String idEvent) {
        String values = "organizer,venue";
        booleanMutableLiveData.setValue(true);
        mEventDetailCall = mEventbriteApiService.fetchEventDetail(idEvent,values);
        mEventDetailCall.enqueue(new Callback<EventDetail>() {
            @Override
            public void onResponse(@NotNull Call<EventDetail> call, Response<EventDetail> response) {
                if(response.body() != null) {
                    EventDetail eventDetail = response.body();
                    processResponse(eventDetail);
                    booleanMutableLiveData.setValue(false);
                }
                else {
                    booleanMutableLiveData.setValue(false);
                    errorStateMutableLiveData.setValue(ErrorState.create(ErrorState.Error.PARSING));
                }
            }

            @Override
            public void onFailure(@NotNull Call<EventDetail> call, Throwable t) {
                booleanMutableLiveData.setValue(false);
                errorStateMutableLiveData.setValue(ErrorState.create(ErrorState.Error.NETWORK));
            }
        });
    }

    @Override
    protected void onCleared() {
        if(mEventDetailCall != null && !mEventDetailCall.isCanceled())
            mEventDetailCall.cancel();

        mEventDetailCall = null;
    }

    private void processResponse(EventDetail eventDetail) {
        String title = eventDetail.getName().getText().toUpperCase();

        String urlLogo = eventDetail.getLogo() != null && eventDetail.getLogo().getUrl() != null ?
                eventDetail.getLogo().getUrl() : "";

        String description = eventDetail.getDescription() != null && eventDetail.getDescription().getText() != null ?
                eventDetail.getDescription().getText().toUpperCase() : getApplication().getString(R.string.description_not_available);

        String startDate = eventDetail.getStart().getLocal().toUpperCase();

        String end = eventDetail.getEnd().getLocal().toUpperCase();

        String address = eventDetail.getVenue().getAddress().getLocalizedAddressDisplay() != null ?
                eventDetail.getVenue().getAddress().getLocalizedAddressDisplay().toUpperCase():"No disponible.";

        String organizer = eventDetail.getOrganizer() != null && eventDetail.getOrganizer().getName() != null ?
                eventDetail.getOrganizer().getName()
                : null;

        String facebook = eventDetail.getOrganizer().getFacebook();
        String urlDetail = eventDetail.getUrl();
        String by = getApplication().getString(R.string.by_organizer);
        String byOrganizer = organizer == null ? "" : String.format(by, organizer);
        mLiveTitle.setValue(title);
        mLiveOrganizer.setValue(byOrganizer);
        mLiveDate.setValue(startDate + " - " + end);
        mLiveAddress.setValue(address);
        mLiveDescription.setValue(description);
        mLiveUrl.setValue(urlDetail);
        mLiveLogo.setValue(urlLogo);
    }

    @Override
    protected LiveData<Boolean> observeLoaderState() {
        mLiveLoading = Transformations.map(booleanMutableLiveData, input -> input);
        return mLiveLoading;
    }

    @Override
    protected LiveData<SnackBar> observeSnackbarMessage() {
        mSnackbar = Transformations.map(errorStateMutableLiveData, new Function<ErrorState, SnackBar>() {
            @Override
            public SnackBar apply(ErrorState input) {
                return EventDetailViewModel.this.getSnackBar(input);
            }
        });
        return mSnackbar;
    }

    @Override
    protected SnackBar getSnackBar(ErrorState input) {
        switch (input.error) {
            case NETWORK:
                return SnackBar.create(R.string.network_error, R.string.retry, SnackBar.Action.REQUEST_EVENT_DETAIL);
            case PARSING:
                return SnackBar.create(R.string.cannot_process_response, R.string.retry, SnackBar.Action.REQUEST_EVENT_DETAIL);
            default:
                return SnackBar.create(R.string.unknown_error, SnackBar.NO_RESOURCE_ID, SnackBar.Action.NONE);
        }
    }
}
