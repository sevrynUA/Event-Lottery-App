package com.example.celery_sticks;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationChecker {
    private FirebaseFirestore db;
    String userID;
    Context context;
    private NotificationManager notificationManager;


    public NotificationChecker(String userID, Context context) {this.context = context; this.userID = userID;}


    public void checkNotifications() {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "New Notification";
            String description = "New Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("New Notification", name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        db = FirebaseFirestore.getInstance();
        db.collection("notifications")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ArrayList<String> recipients = (ArrayList<String>) document.get("recipients");
                                if(recipients.contains(userID)) {
                                    String message = document.getString("message");

                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "New Notification")
                                            .setSmallIcon(R.drawable.notification_icon)
                                            .setContentTitle("New Notification")
                                            .setContentText(message)
                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                            .setAutoCancel(true);

                                    // Sends the notification using a unique ID based on the user's hash code
                                    notificationManager.notify(userID.hashCode(), builder.build());
                                    recipients.remove(userID);
                                    DocumentReference notificationRef = db.collection("notifications").document(document.getId());
                                    notificationRef
                                            .update("recipients", recipients)
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("Notification Checker", "Error updating", e);
                                                }
                                            });
                                }
                            }
                        } else {
                            Log.d("checker", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
