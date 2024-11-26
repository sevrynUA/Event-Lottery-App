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

public class Notification {
    private FirebaseFirestore db;
    ArrayList<String> recipients;
    String message;


    public Notification(String newMessage, ArrayList<String> users) {
        message = newMessage;
        recipients = users;
    }


    public void newNotification() {

        db = FirebaseFirestore.getInstance();
        HashMap<String, Object> notificationData = new HashMap<>();
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

