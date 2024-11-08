package com.example.celery_sticks.ui.myevents;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.celery_sticks.R;

/**
 * Represents the warning fragment presented to users when trying to register for an event that uses geolocation
 */
public class GeolocationWarningFragment extends DialogFragment {
    /**
     * Represents the listener for geolocation fragment
     */
    interface GeolocationDialogueListener {
        /**
         * register() is called when an user proceeds past the warning and still wishes to register in an event
         */
        void register();
    }
    private GeolocationDialogueListener listener;

    /**
     * Provides context for the listener
     * @param context for the listener
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof GeolocationDialogueListener) {
            listener = (GeolocationDialogueListener) context;
        } else {
            throw new RuntimeException(context + "must implement GeolocationDialogueListener");
        }
    }

    /**
     * Returns the builder for creating the fragment to be displayed
     * @param savedInstanceState
     * @return the builder for creating the fragment to be displayed
     */
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.geolocation_warning_fragment, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Warning")
                .setNegativeButton("Go back", null)
                .setPositiveButton("Register", (dialog, which) -> {
                    listener.register();
                })
                .create();
    }
}
