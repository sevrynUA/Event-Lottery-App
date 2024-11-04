package com.example.celery_sticks.ui.myevents;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.celery_sticks.R;

public class EventDetailsViewModel extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details);

        Intent intent = getIntent();

        TextView event_title_text = findViewById(R.id.event_title_text);
        TextView event_description_text = findViewById(R.id.event_description_text);
        TextView event_time_text = findViewById(R.id.event_time_text);
        TextView event_location_text = findViewById(R.id.event_location_text);
        TextView event_availability_text = findViewById(R.id.event_availability_text);
        TextView event_price_text = findViewById(R.id.event_price_text);
        ImageView event_image_view = findViewById(R.id.event_image_view);

        event_title_text.setText(intent.getStringExtra("name"));
        event_description_text.setText(intent.getStringExtra("description"));
        event_time_text.setText(intent.getStringExtra("date"));
        event_location_text.setText(intent.getStringExtra("location"));
        event_availability_text.setText(intent.getStringExtra("availability"));
        event_price_text.setText(intent.getStringExtra("price"));
        // change event_image_view to the image passed with intent.getStringExtra("image") here

        Button backButton = findViewById(R.id.event_details_back);
        backButton.setOnClickListener(view -> {
            finish();
        });
    }
}
