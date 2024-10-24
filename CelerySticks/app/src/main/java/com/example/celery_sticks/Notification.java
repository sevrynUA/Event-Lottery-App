package com.example.celery_sticks;

import java.time.LocalDateTime;

public class Notification {
    private String notification_message;
    private LocalDateTime notification_datetime;

    public Notification(String notification_message, LocalDateTime notification_datetime) {
        this.notification_message = notification_message;
        this.notification_datetime = notification_datetime;
    }

    public String getNotification_message() {
        return notification_message;
    }

    public void setNotification_message(String string) {
        this.notification_message = string;
    }

    public LocalDateTime getNotification_datetime() {
        return notification_datetime;
    }

    public void setNotification_datetime(LocalDateTime notification_datetime) {
        this.notification_datetime = notification_datetime;
    }

}
