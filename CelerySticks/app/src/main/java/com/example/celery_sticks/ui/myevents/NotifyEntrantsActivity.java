package com.example.celery_sticks.ui.myevents;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.celery_sticks.Notification;
import com.example.celery_sticks.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


/**
 * Represents the Notify screen that the organizer can open through the "Manage Entrants" screen on their event
 */
public class NotifyEntrantsActivity extends AppCompatActivity {
    private Button backButton;
    private Button clearButton;
    private Button sendButton;
    private RadioGroup notifyOptionsGroup;
    private FirebaseFirestore db;

    private String eventID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_notify);

        backButton = findViewById(R.id.notify_back_button);
        clearButton = findViewById(R.id.clear_button);
        sendButton = findViewById(R.id.send_button);
        notifyOptionsGroup = findViewById(R.id.notify_options_group);

        db = FirebaseFirestore.getInstance();

        eventID = getIntent().getStringExtra("eventID");

        backButton.setOnClickListener(view -> finish());
        clearButton.setOnClickListener(view -> clearInputs());
        sendButton.setOnClickListener(view -> {
            int selectedOptionId = notifyOptionsGroup.getCheckedRadioButtonId();
            if (selectedOptionId == -1) {
                Toast.makeText(this, "Please select a recipient group", Toast.LENGTH_SHORT).show();
            } else {
                RadioButton selectedOption = findViewById(selectedOptionId);
                String selectedGroup = selectedOption.getText().toString();
                createNotificationForGroup(selectedGroup);
            }
        });
    }

    /**
     * Create a notification for a given group
     * @param group group to create the notification for
     */
    private void createNotificationForGroup(String group) {
        if (eventID == null || eventID.isEmpty()) {
            Toast.makeText(this, "Invalid event ID", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("events").document(eventID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                ArrayList<String> recipients = new ArrayList<>();
                if ("Waitlist Only".equals(group)) {
                    recipients = (ArrayList<String>) documentSnapshot.get("registrants");
                } else if ("Selected Only".equals(group)) {
                    recipients = (ArrayList<String>) documentSnapshot.get("selected");
                } else if ("Chosen Only".equals(group)) {
                    recipients = (ArrayList<String>) documentSnapshot.get("accepted");
                } else if ("Cancelled Only".equals(group)) {
                    recipients = (ArrayList<String>) documentSnapshot.get("cancelled");
                }

                if (recipients != null && !recipients.isEmpty()) {
                    String title = ((com.google.android.material.textfield.TextInputEditText) findViewById(R.id.title_input)).getText().toString();
                    String message = ((com.google.android.material.textfield.TextInputEditText) findViewById(R.id.description_input)).getText().toString();

                    if (title.isEmpty()) {
                        Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (message.isEmpty()) {
                        Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Notification notification = new Notification(title, message, recipients);
                    notification.newNotification();
                    Toast.makeText(this, "Notification sent successfully", Toast.LENGTH_SHORT).show();
                    clearInputs();
                } else {
                    Toast.makeText(this, "No recipients in the notified group", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to fetch event details", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Clears the input fields
     */
    private void clearInputs() {
        ((com.google.android.material.textfield.TextInputEditText) findViewById(R.id.title_input)).setText("");
        ((com.google.android.material.textfield.TextInputEditText) findViewById(R.id.description_input)).setText("");

        notifyOptionsGroup.clearCheck();
    }

}
