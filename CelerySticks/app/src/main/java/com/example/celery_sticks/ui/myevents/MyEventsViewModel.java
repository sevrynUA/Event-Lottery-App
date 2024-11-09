package com.example.celery_sticks.ui.myevents;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Represents the MyEvents activity (homepage); mostly default AndroidStudio implementation from sidebar
 */
public class MyEventsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MyEventsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is MyEvents (home page)");
    }

    public LiveData<String> getText() {
        return mText;
    }
}