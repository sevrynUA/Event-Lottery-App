package com.example.celery_sticks.ui.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Represents the Settings activity; mostly default AndroidStudio implementation from sidebar
 */
public class SettingsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SettingsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Settings page");
    }

    public LiveData<String> getText() {
        return mText;
    }
}