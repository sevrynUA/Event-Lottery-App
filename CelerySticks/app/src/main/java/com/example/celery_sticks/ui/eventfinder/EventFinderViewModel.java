package com.example.celery_sticks.ui.eventfinder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EventFinderViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public EventFinderViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is for the QR code scanner (Event Finder)");
    }

    public LiveData<String> getText() {
        return mText;
    }
}