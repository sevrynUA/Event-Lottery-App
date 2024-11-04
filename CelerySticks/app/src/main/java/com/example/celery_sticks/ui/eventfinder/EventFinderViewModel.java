package com.example.celery_sticks.ui.eventfinder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EventFinderViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public EventFinderViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Point the camera at a QR code");
    }

    public LiveData<String> getText() {
        return mText;
    }
}