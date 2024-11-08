package com.example.celery_sticks.ui.eventfinder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * This is the view model for the event finder fragment (sets the live data to be displayed)
 */
public class EventFinderViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    /**
     * sets the value for the liveData model
     */
    public EventFinderViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Point the camera at a QR code");
    }

    /**
     * Getter fot the live data attribute mText
     * @return the attribute mText
     */
    public LiveData<String> getText() {
        return mText;
    }
}