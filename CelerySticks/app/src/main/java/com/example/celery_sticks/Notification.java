package com.example.celery_sticks;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a notification
 */
public class Notification {
    private FirebaseFirestore db;
    ArrayList<String> recipients;
    String message;
    String title;


    /**
     * Constructor to setup title, message, and recipients
     * @param title
     * @param message
     * @param recipients
     */
    public Notification(String title, String message, ArrayList<String> recipients) {
        this.title = title;
        this.message = message;
        this.recipients = recipients;
    }


    /**
     * Creates a new notification
     */
    public void newNotification() {

        db = FirebaseFirestore.getInstance();
        HashMap<String, Object> notificationData = new HashMap<>();
        notificationData.put("title", title);
        notificationData.put("message", message);
        notificationData.put("recipients", recipients);

        db.collection("notifications").add(notificationData)
                .addOnSuccessListener(documentReference -> {
                    String notificationID = documentReference.getId();
                })
                .addOnFailureListener(e -> {
                    Log.e("Notification", "Failed to make new notification");
                });
    }
}

