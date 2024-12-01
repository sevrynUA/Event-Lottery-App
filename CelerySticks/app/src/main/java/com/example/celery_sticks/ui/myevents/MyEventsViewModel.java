package com.example.celery_sticks.ui.myevents;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Represents the MyEvents activity (homepage); mostly default AndroidStudio implementation from sidebar
 */
public class MyEventsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    /**
     * Default construction for the MyEvents page
     */
    public MyEventsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is MyEvents (home page)");
    }

    /**
     * Returns default object construction text
     * @return text set in default construction function
     */
    public LiveData<String> getText() {
        return mText;
    }
}