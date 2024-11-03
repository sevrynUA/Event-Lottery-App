package com.example.celery_sticks.ui.eventadd;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.celery_sticks.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;



public class AddEventFragment extends AppCompatActivity {

    private EditText title;
    private EditText date;
    private EditText participationLimit;
    private EditText cost;
    private EditText description;
    private Button geolocationButton;
    private EditText openDate;
    private EditText closeDate;
    private EditText location;
    private Button createEventButton;
    private Button cancelButton;
    private RelativeLayout geolocationButtonBackground;
    private RelativeLayout geolocationButtonIcon;

    private FirebaseFirestore db;

    private boolean geolocationStatus;

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        title = findViewById(R.id.event_title_input);
        date = findViewById(R.id.event_date_input);
        participationLimit = findViewById(R.id.event_participation_limit_input);
        cost = findViewById(R.id.event_cost_input);
        description = findViewById(R.id.event_description_input);
        openDate = findViewById(R.id.event_date_open_input);
        closeDate = findViewById(R.id.event_date_close_input);
        location = findViewById(R.id.event_location_input);
        geolocationButton = findViewById(R.id.geolocation_button_event_create);
        createEventButton = findViewById(R.id.create_event_confirm_button);
        cancelButton = findViewById(R.id.create_event_cancel_button);

        geolocationButtonBackground = findViewById(R.id.geolocation_button_event_create_background);
        geolocationButtonIcon = findViewById(R.id.geolocation_button_event_create_icon);

        db = FirebaseFirestore.getInstance();

        geolocationButton.setOnClickListener(view -> {
            if (geolocationStatus) {
                geolocationStatus = false;
                geolocationButtonBackground.setBackgroundResource(R.drawable.login_user_type_button_unselected);
                geolocationButtonIcon.setBackgroundResource(R.drawable.x_mark);
            }
            else {
                geolocationStatus = true;
                geolocationButtonBackground.setBackgroundResource(R.drawable.login_user_type_button_selected);
                geolocationButtonIcon.setBackgroundResource(R.drawable.checkmark);
            }
        });

        createEventButton.setOnClickListener(view -> {
            saveEventData();
        });

        cancelButton.setOnClickListener(view -> {
            finish();
        });


    }

    public Date getDateFromString(String stringDate){

        try {
            Date date = format.parse(stringDate);
            return date ;
        } catch (ParseException e){
            return null ;
        }

    }

    private void saveEventData() {
        String title = this.title.getText().toString();
        String date = this.date.getText().toString();
        String participationLimit = this.participationLimit.getText().toString();
        String cost = this.cost.getText().toString();
        String description = this.description.getText().toString();
        String openDate = this.openDate.getText().toString();
        String closeDate = this.closeDate.getText().toString();
        String location = this.location.getText().toString();

        Date formatedDate = getDateFromString(date);
        Date formatedOpen = getDateFromString(openDate);
        Date formatedClose = getDateFromString(closeDate);


        // Input validation for empty required fields
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(date) || TextUtils.isEmpty(location) || formatedOpen == null || formatedDate == null || formatedClose == null) {
            Toast.makeText(this, "Please fill in all required information", Toast.LENGTH_SHORT).show();
            return;
        }

        // add QR code generation

        //get organizer id
        String organizerID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        HashMap<String, Object> eventData = new HashMap<>();
        HashMap<String, Object> eventWaitList = new HashMap<>();
        eventData.put("accepted", false);
        eventData.put("close", closeDate);
        eventData.put("date", date);
        eventData.put("description", description);
        eventData.put("image", false);
        eventData.put("location", location);
        eventData.put("name", title);
        eventData.put("open", openDate);
        eventData.put("qrcode", false);
        eventData.put("registered", false);
        eventData.put("signupqrcode", false);
        eventData.put("geolocation", geolocationStatus);
        eventData.put("pariticpation limit", participationLimit);
        eventData.put("cost", cost);
        eventData.put("organizerID", organizerID);



        db.collection("events").document(title).set(eventData)
                .addOnSuccessListener(aVoid -> {
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save event data", Toast.LENGTH_SHORT).show();
                });
    }
}
