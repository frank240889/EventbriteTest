package com.example.eventbritetest.UI.settings;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventbritetest.R;
import com.example.eventbritetest.UI.BaseViewModel;
import com.example.eventbritetest.network.DistanceUnit;
import com.example.eventbritetest.network.EventbriteApiService;
import com.example.eventbritetest.persistence.sharedpreferences.SharedPref;
import com.example.eventbritetest.utils.Constants;
import com.example.eventbritetest.utils.ErrorState;

import java.util.List;

import javax.inject.Inject;

public class SettingsViewModel extends BaseViewModel {
    private MutableLiveData<String> mLiveCurrentRange = new MutableLiveData<>();
    private MutableLiveData<List<DistanceUnit.Unit>> mLiveUnits = new MutableLiveData<>();
    private MutableLiveData<Boolean> mLiveHasAnyChange = new MutableLiveData<>();
    private boolean mHasChangeAny;
    private int mCurrentRange;
    private String mNewRange;
    private DistanceUnit.Unit mNewDistanceUnit;
    private SharedPref mSharedPref;
    private DistanceUnit.Unit mCurrentUnit;
    private Location mCurrentLocation;
    private Location mNewLocation;
    private List<DistanceUnit.Unit> mUnits;
    private MutableLiveData<Boolean> booleanMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<String> mLiveOnMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> mLiveLoading = new MutableLiveData<>();

    @Inject
    public SettingsViewModel(@NonNull Application application, SharedPref sharedPref) {
        super(application);
        mSharedPref = sharedPref;
        mNewDistanceUnit = mCurrentUnit = getCurrentUnit();
        mCurrentLocation = getCurrentLocation();
        mCurrentRange = getCurrentRange();
        mUnits = buildUnitList();
        mLiveUnits.setValue(mUnits);
        mLiveCurrentRange.setValue(mCurrentRange+"");
        booleanMutableLiveData.setValue(true);
    }

    public LiveData<String> observeCurrentRange() {
        return mLiveCurrentRange;
    }

    public LiveData<List<DistanceUnit.Unit>> observeUnitList() {
        return mLiveUnits;
    }

    public LiveData<Boolean> observeSettingChanges() {
        return mLiveHasAnyChange;
    }

    public LiveData<String> observeOnLiveMessage() {
        return mLiveOnMessage;
    }

    public void setRange(String range) {
        mNewRange = range;
    }

    public void setLocation(Location location) {
        mNewLocation = location;
    }

    public void saveChanges() {
        boolean hasChangeAny = saveRange() || saveLocation() || saveUnit();
        mLiveHasAnyChange.setValue(hasChangeAny);
    }

    public void checkUnit(int position) {
        mNewDistanceUnit = mUnits.get(position);
        mNewDistanceUnit.setChecked(true);
        uncheckAllExcept(mNewDistanceUnit);
        mLiveUnits.setValue(mUnits);
    }

    private void uncheckAllExcept(DistanceUnit.Unit u) {
        for(DistanceUnit.Unit unit : mUnits) {
            if (!unit.equals(u))
                unit.setChecked(false);
        }
    }

    private boolean saveRange() {
        boolean change;
        try {
            int newRange = Integer.parseInt(mNewRange);

            if((newRange != mCurrentRange) &&(0 < newRange && newRange < 101)) {
                mSharedPref.putInt(EventbriteApiService.LOCATION_WITHIN, newRange);
                change = true;
            }
            else {
                mLiveOnMessage.setValue(getApplication().getString(R.string.invalid_range));
                change = false;
            }
        }
        catch (NumberFormatException e) {
            mLiveOnMessage.setValue(getApplication().getString(R.string.invalid_range));
            change = false;
        }

        return change;
    }

    private boolean saveLocation() {
        boolean change;

        if(mCurrentLocation != null && mNewLocation != null && (mCurrentLocation.getLatitude() != mNewLocation.getLatitude() ||
        mCurrentLocation.getLongitude() != mNewLocation.getLongitude())) {

            mSharedPref.putString(EventbriteApiService.LOCATION_LATITUDE, mNewLocation.getLatitude()+"");
            mSharedPref.putString(EventbriteApiService.LOCATION_LONGITUDE, mNewLocation.getLongitude()+"");
            change = true;
        }
        else {
            change = false;
        }

        return change;
    }

    private boolean saveUnit() {
        boolean change;

        if(!mCurrentUnit.equals(mNewDistanceUnit)) {
            mSharedPref.putString(Constants.DISTANCE_UNIT, mNewDistanceUnit.getValue());
            change = true;
        }
        else {
            change = false;
        }

        return change;
    }


    private Location getCurrentLocation() {
        String lat = getCurrentLatitude();
        String lng = getCurrentLongitude();
        if(lat == null || lng == null)
            return null;

        Location location = new Location("current_location");
        location.setLatitude(Double.valueOf(lat));
        location.setLongitude(Double.valueOf(lng));
        return location;
    }

    private String getCurrentLatitude() {
        return mSharedPref.getString(EventbriteApiService.LOCATION_LATITUDE);
    }

    private String getCurrentLongitude() {
        return mSharedPref.getString(EventbriteApiService.LOCATION_LONGITUDE);
    }

    private DistanceUnit.Unit getCurrentUnit() {
        DistanceUnit distanceUnit = DistanceUnit.getUnit(mSharedPref.getString(Constants.DISTANCE_UNIT));
        return new DistanceUnit.Unit(distanceUnit.ordinal(), distanceUnit.toString(), true);
    }

    private int getCurrentRange() {
        return mSharedPref.getInt(EventbriteApiService.LOCATION_WITHIN) == -1 ?
                1 : mSharedPref.getInt(EventbriteApiService.LOCATION_WITHIN);
    }

    private List<DistanceUnit.Unit> buildUnitList() {
        List<DistanceUnit.Unit> units = DistanceUnit.getUnitList();
        for (DistanceUnit.Unit unit : units) {
            if(unit.getId() == mCurrentUnit.getId()){
                unit.setChecked(true);
            }
        }

        return units;
    }

    @Override
    public LiveData<Boolean> observeLoaderState() {
        return mLiveLoading;
    }

    @Override
    public LiveData<ErrorState> observeErrorState() {
        return mErrorState;
    }

}
