package com.example.celery_sticks.ui.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Represents the Settings activity; mostly default AndroidStudio implementation from sidebar
 */
public class SettingsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    /**
     * Default construction for the Settings page
     */
    public SettingsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Settings page");
    }

    /**
     * Returns default object construction text
     * @return text set in default construction function
     */
    public LiveData<String> getText() {
        return mText;
    }
}