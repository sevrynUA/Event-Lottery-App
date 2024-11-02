package com.example.celery_sticks.ui.myevents;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.celery_sticks.R;

public class EventDetailsViewModel extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details);

        // use getIntent().getStringExtra("field") to get data

        Button backButton = findViewById(R.id.event_details_back);
        backButton.setOnClickListener(view -> {
            finish();
        });
    }
}
