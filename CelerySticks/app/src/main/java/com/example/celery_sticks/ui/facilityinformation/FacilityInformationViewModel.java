package com.example.celery_sticks.ui.facilityinformation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FacilityInformationViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public FacilityInformationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Facility information here");
    }

    public LiveData<String> getText() {
        return mText;
    }
}