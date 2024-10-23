package com.example.celery_sticks;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class LoginActivity extends AppCompatActivity {
    Boolean entrant = false;
    Boolean organizer = false;

    Drawable unselectedDrawable;
    Drawable selectedDrawable;

    private void toggleButton(Button button) {
        if (button.getId() == findViewById(R.id.entrant_button).getId()) {
            if (entrant == false) {
                entrant = true;
                button.setBackgroundResource(R.drawable.login_user_type_button_selected);
            } else if (entrant == true) {
                entrant = false;
                button.setBackgroundResource(R.drawable.login_user_type_button_unselected);
            }
        } else if (button.getId() == findViewById(R.id.organizer_button).getId()) {
            if (organizer == false) {
                organizer = true;
                button.setBackgroundResource(R.drawable.login_user_type_button_selected);
            } else if (organizer == true) {
                organizer = false;
                button.setBackgroundResource(R.drawable.login_user_type_button_unselected);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        selectedDrawable = ContextCompat.getDrawable(this, R.drawable.login_user_type_button_selected);
        unselectedDrawable = ContextCompat.getDrawable(this, R.drawable.login_user_type_button_unselected);


        Button entrantButton = findViewById(R.id.entrant_button);
        entrantButton.setOnClickListener(view -> {
            toggleButton(entrantButton);
        });
        Button organizerButton = findViewById(R.id.organizer_button);
        organizerButton.setOnClickListener(view -> {
            toggleButton(organizerButton);
        });

        Button finishButton = findViewById(R.id.finish_button);
        finishButton.setOnClickListener(view -> {
            if (entrant == true || organizer == true) {
                finish();
            }
        });
    }
}
