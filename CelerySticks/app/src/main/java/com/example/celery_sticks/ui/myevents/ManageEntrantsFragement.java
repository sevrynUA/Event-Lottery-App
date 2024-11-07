package com.example.celery_sticks.ui.myevents;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.celery_sticks.R;
import com.google.firebase.firestore.FirebaseFirestore;


public class ManageEntrantsFragement extends AppCompatActivity {
    private Button mapButton;
    private Button selectedButton;
    private Button notifyButton;
    private Button lotteryButton;
    private TextView lotteryStatusText;
    private FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_event_entrants);

        mapButton = findViewById(R.id.map_button);
        selectedButton = findViewById(R.id.selected_button);
        notifyButton = findViewById(R.id.notify_button);
        lotteryButton = findViewById(R.id.lottery_button);
        lotteryStatusText = findViewById(R.id.lottery_status);

        lotteryButton.setOnClickListener(view -> {
            // lottery stuff?
        });

        selectedButton.setOnClickListener(view -> {
            // selected entrants fragment

        });

    }
}
