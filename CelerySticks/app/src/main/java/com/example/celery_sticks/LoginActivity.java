package com.example.celery_sticks;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Button entrantButton = findViewById(R.id.entrant_button);
        Button organizerButton = findViewById(R.id.organizer_button);

        Button finishButton = findViewById(R.id.finish_button);
        finishButton.setOnClickListener(view -> {
            finish();
        });
    }



}
