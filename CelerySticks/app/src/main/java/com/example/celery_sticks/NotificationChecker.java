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
import android.app.PendingIntent;
import android.content.Intent;


/**
 * NotificationChecker will check the database for new notifications to display
 */
public class NotificationChecker {
    private FirebaseFirestore db;
    String userID;
    Context context;
    private NotificationManager notificationManager;


    /**
     * Constructor to setup which user to check for, and context
     * @param userID of user to check for new notifications
     * @param context
     */
    public NotificationChecker(String userID, Context context) {this.context = context; this.userID = userID;}


    /**
     * Check for new notifications
     */
    public void checkNotifications() {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelID = "CelerySticksNotification";
        CharSequence channelName = "Celery Sticks";
        String channelDescription = "New Notification from Celery Sticks";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelID, channelName, importance);
            channel.setDescription(channelDescription);
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
                                    String title = document.getString("title");
                                    String message = document.getString("message");

                                    Intent intent = new Intent(context, MainActivity.class); // Change to your desired activity
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(
                                            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


                                    PendingIntent fullScreenIntent = PendingIntent.getActivity(
                                            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID)
                                            .setSmallIcon(R.drawable.notification_icon)
                                            .setContentTitle(title)
                                            .setContentText(message)
                                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                                            .setAutoCancel(true)
                                            .setContentIntent(pendingIntent)
                                            .setFullScreenIntent(fullScreenIntent, true);

                                    // Sends the notification using a unique ID based on the user's hash code
                                    db.collection("users").document(userID).get()
                                                    .addOnSuccessListener(userDocument -> {
                                                        // Send notification ONLY IF they have the setting enabled
                                                        if (userDocument.exists() && userDocument.getBoolean("notificationSetting") == true) {
                                                            notificationManager.notify(userID.hashCode(), builder.build());
                                                        }
                                                    });
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
