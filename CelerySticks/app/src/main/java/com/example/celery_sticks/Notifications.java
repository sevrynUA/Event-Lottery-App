package com.example.celery_sticks;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * This is a class that sends notifications to selected and non selected users for an event.
 */
public class Notifications {

    private Context context;

    /**
     * Constructs a Notifications object with the provided application context.
     * @param context Context of the application
     */
    public Notifications(Context context) {
        this.context = context;
    }

    /**
     * Sends notifications to users participating in an event lottery.
     * Selected users receive a "winner" notification.
     * Non-selected users are informed they are on the waitlist.
     * @param eventID The eventID for event which entrants are notified from
     */
    public void sendNotifications(String eventID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference eventDoc = db.collection("events").document(eventID);

        eventDoc.get().addOnSuccessListener(event -> {
            if (event.exists()) {
                List<String> registrants = (List<String>) event.get("registrants");
                List<String> selectedUserIDs = (List<String>) event.get("selected");

                // Test to see if organizer gets notification when lottery drawn
                String organizerID = event.getString("organizerID");

                NotificationHelper notificationHelper = new NotificationHelper(context);

                // Notify winners
                if (selectedUserIDs != null) {
                    for (String userID : selectedUserIDs) {
                        String selectedMessage = "Congratulations! You've been selected for the event! Please sign up to confirm your attendance.";
                        notificationHelper.sendNotification(selectedMessage, userID);
                    }
                }

                // Notify non-winners
                if (registrants != null) {
                    for (String userID : registrants) {
                        if (!selectedUserIDs.contains(userID)) {
                            String nonselectedMessage = "Sorry, you didn’t win this time, but you're still on the waitlist!";
                            notificationHelper.sendNotification(nonselectedMessage, userID);
                        }
                    }
                }

                // Test to see if organizer gets notification when lottery drawn
                if (organizerID != null) {
                    String organizerMessage = "Lottery notifications have been sent!";
                    notificationHelper.sendNotification(organizerMessage, organizerID);
                } else {
                    Log.e("Notifications", "Organizer ID is null for event: " + eventID);
                }

            }
        }).addOnFailureListener(e -> {
            // Handle failure
            Log.e("Firestore", "Failed to get event: " + e.getMessage());
        });
    }
}