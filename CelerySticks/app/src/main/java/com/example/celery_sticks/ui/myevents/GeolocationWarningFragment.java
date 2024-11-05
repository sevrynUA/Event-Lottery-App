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

public class GeolocationWarningFragment extends DialogFragment {
    interface GeolocationDialogueListener {
        void register();
    }
    private GeolocationDialogueListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof GeolocationDialogueListener) {
            listener = (GeolocationDialogueListener) context;
        } else {
            throw new RuntimeException(context + "must implement GeolocationDialogueListener");
        }
    }

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
