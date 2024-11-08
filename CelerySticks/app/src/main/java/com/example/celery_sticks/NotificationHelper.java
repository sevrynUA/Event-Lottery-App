package com.example.celery_sticks;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

/**
 * This is a class that builds the notification channel and manages notification format.
 */
public class NotificationHelper {
    private static final String CHANNEL_ID = "lottery_notifications";
    private static final String CHANNEL_NAME = "Lottery Notifications";
    private static final String CHANNEL_DESCRIPTION = "Notifications for lottery winners and losers.";

    private Context context;
    private NotificationManager notificationManager;

    /**
     * Constructs a NotificationHelper with the specified context.
     * Initializes the NotificationManager and creates the notification channel.
     * @param context app context
     */
    public NotificationHelper(@NonNull Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_NAME;
            String description = CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Sends a notification with a specified message to the user identified by userID.
     * The notification is set to auto-cancel once viewed by the user.
     * @param message The message content of the notification.
     * @param userID  The unique identifier for the user, used to generate a unique notification ID.
     */
    public void sendNotification(String message, @NonNull String userID) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Lottery Notification")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        // Sends the notification using a unique ID based on the user's hash code
        notificationManager.notify(userID.hashCode(), builder.build());
    }
}