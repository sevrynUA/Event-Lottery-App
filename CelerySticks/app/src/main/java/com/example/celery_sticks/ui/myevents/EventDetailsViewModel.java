package com.example.celery_sticks.ui.myevents;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.celery_sticks.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents the event details activity displayed by clicking on events in the MyEvents menu
 */
public class EventDetailsViewModel extends AppCompatActivity implements GeolocationWarningFragment.GeolocationDialogueListener {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public String userID = null;
    public String eventID = null;

    public Boolean geolocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        eventID = intent.getStringExtra("eventID");

        Button registerButton = findViewById(R.id.register_button);
        Button manageEntrantsButton = findViewById(R.id.manage_entrants_button);
        String eventCategory = intent.getStringExtra("category");
        if (Objects.equals(eventCategory, "created")) {
            registerButton.setVisibility(View.GONE);
            manageEntrantsButton.setVisibility(View.VISIBLE);
        } else {
            registerButton.setVisibility(View.VISIBLE);
            manageEntrantsButton.setVisibility(View.GONE);
            if (Objects.equals(eventCategory, "registered")) {
                registerButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.unSelectedRed)));
                registerButton.setText("Unregister");
            } else {
                registerButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.vomitGreen)));
                registerButton.setText("Register");
            }
        }


        TextView eventTitleText = findViewById(R.id.event_title_text);
        TextView eventDescriptionText = findViewById(R.id.event_description_text);
        TextView eventTimeText = findViewById(R.id.event_time_text);
        TextView eventLocationText = findViewById(R.id.event_location_text);
        TextView eventAvailabilityText = findViewById(R.id.event_availability_text);
        TextView eventPriceText = findViewById(R.id.event_price_text);
        ImageView eventImageView = findViewById(R.id.event_image_view);

        eventTitleText.setText(intent.getStringExtra("name"));
        eventDescriptionText.setText(intent.getStringExtra("description"));
        eventTimeText.setText(intent.getStringExtra("date"));
        eventLocationText.setText(intent.getStringExtra("location"));
        if (intent.getStringExtra("availability") == null || Objects.equals(intent.getStringExtra("availability"), "")) {
            eventAvailabilityText.setText("Availability - No Limit");
        } else {
            eventAvailabilityText.setText(String.format("Availability - %s", intent.getStringExtra("availability")));
        }
        eventPriceText.setText(intent.getStringExtra("price"));
        // change event_image_view to the image passed with intent.getStringExtra("image") here

        Button backButton = findViewById(R.id.event_details_back);
        backButton.setOnClickListener(view -> {
            finish();
        });

        Button qrButton = findViewById(R.id.button3);
        qrButton.setOnClickListener(view -> {
            Intent qrView = new Intent(EventDetailsViewModel.this, EventQRCodeView.class);
            qrView.putExtra("eventID", eventID);
            startActivity(qrView);
        });

        registerButton.setOnClickListener(view -> {
            checkIfUserRegistered(isUserRegistered -> {
                if (isUserRegistered) { // user is unregistering
                    unregister();
                } else { // user is registering
                    if (geolocation) {
                        new GeolocationWarningFragment().show(getSupportFragmentManager(), "Warning");
                    } else {
                        register();
                    }
                }
            });
        });

        manageEntrantsButton.setOnClickListener(view -> {
            Intent manageEntrantsIntent = new Intent(EventDetailsViewModel.this, ManageEntrantsFragment.class);
            manageEntrantsIntent.putExtra("eventID", eventID);
            manageEntrantsIntent.putExtra("availability", intent.getStringExtra("availability"));
            startActivity(manageEntrantsIntent);
        });
    }

    /**
     * Interface used for asynchronously accessing data for event details
     */
    public interface EventDetailsCallback {
        /**
         * Function is run when asynchronous access of data has been completed
         * @param eventData is the data accessed asynchronously
         */
        void onDataRecieved(ArrayList<String> eventData);
    }

    /**
     * Interface used for asynchronously returning data for registration
     */
    public interface RegistrationWaitCallback {
        /**
         * Function is run when asynchronous access of data has been completed
         * @param isRegistered is a boolean indicating whether the user is registered
         */
        void onDataReturned(Boolean isRegistered);
    }

    /**
     * Registers the user in the current event
     */
    public void register() {
        db.collection("events").document(eventID)
                .update("registrants", FieldValue.arrayUnion(userID))
                .addOnSuccessListener(success -> {
                    Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                });
        Intent completedIntent = new Intent();
        setResult(RESULT_OK, completedIntent);
        finish();
    }

    /**
     * Unregisters the user from the current event
     */
    public void unregister() {
        db.collection("events").document(eventID)
                .update("registrants", FieldValue.arrayRemove(userID))
                .addOnSuccessListener(success -> {
                    Toast.makeText(this, "Unregistration Successful!", Toast.LENGTH_SHORT).show();
                });
        Intent completedIntent = new Intent() ;
        setResult(RESULT_OK, completedIntent);
        finish();
    }

    /**
     * Checks if user is already registered in the current event
     * @param callback is used to get result of asynchronous data access
     */
    public Boolean checkIfUserRegistered(RegistrationWaitCallback callback) {
        getRegistrants(eventID, new EventDetailsCallback() {
            @Override
            public void onDataRecieved(ArrayList<String> eventData) {
                Boolean isUserRegistered = eventData.contains(userID);
                callback.onDataReturned(isUserRegistered);
            }
        });
        return null;
    }

    /**
     * Gets the userIDs of the entrants that have registered in the current event
     * @param eventID is the ID of the current event
     * @param callback is used to get results of asynchronous data access
     * @return an ArrayList containing userIDs of the entrants registered in the current event
     */
    public ArrayList<String> getRegistrants(String eventID, EventDetailsCallback callback) {
        DocumentReference ref = db.collection("events").document(eventID);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<String> registrants = null;
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        geolocation = (Boolean) document.getBoolean("geolocation"); // get geolocation while checking db
                        registrants = (ArrayList<String>) document.get("registrants");
                    }
                    callback.onDataRecieved(registrants);
                }
            }
        });
        return null;
    }
}
