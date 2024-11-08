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

public class LotteryFragment extends DialogFragment {
    interface LotteryDialogueListener {
        void startLottery(Editable input);
    }
    private LotteryDialogueListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof LotteryDialogueListener) {
            listener = (LotteryDialogueListener) context;
        } else {
            throw new RuntimeException(context + "must implement LotteryDialogueListener");
        }
    }

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
