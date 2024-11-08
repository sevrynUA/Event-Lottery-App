package com.example.celery_sticks.ui.myevents;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.celery_sticks.R;

/**
 * Represents the fragment shown to organizers who want to draw names for their event's lottery; requests number of entrants to draw
 */
public class LotteryFragment extends DialogFragment {
    /**
     * Represents the listener for lottery fragment
     */
    interface LotteryDialogueListener {
        /**
         * Begins lottery when organizer proceeds past the fragment
         */
        void startLottery(Editable input);
    }
    private LotteryDialogueListener listener;

    /**
     * Provides context for the listener
     * @param context for the listener
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof LotteryDialogueListener) {
            listener = (LotteryDialogueListener) context;
        } else {
            throw new RuntimeException(context + "must implement LotteryDialogueListener");
        }
    }

    /**
     * Returns the builder for creating the fragment to be displayed
     * @param savedInstanceState
     * @return the builder for creating the fragment to be displayed
     */
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.lottery_fragment, null);
        EditText input = view.findViewById(R.id.lottery_quantity_input);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Lottery")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Proceed", (dialog, which) -> {
                    listener.startLottery(input.getText());
                })
                .create();
    }
}
