package com.example.celery_sticks.ui.myevents;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.example.celery_sticks.R;

/**
 * Represents the warning fragment presented to users when trying to register for an event that uses geolocation
 */
public class GeolocationWarningFragment extends DialogFragment {
    /**
     * Represents the listener for geolocation fragment
     */
    private int PERMISSION_ID = 44;
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
            if(!checkPermissions()) { requestPermissions(); }
            listener = (GeolocationDialogueListener) context;
        } else {
            throw new RuntimeException(context + "must implement GeolocationDialogueListener");
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
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
