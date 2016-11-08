package com.anna;

import android.app.Notification;
import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotificationData {
    private String title;
    private String text;
    private Bitmap icon;
    private Date time;
    private String app;
    private String packageName;
    private Bundle extras;
    private PendingIntent intent;
    private RemoteInput[] remoteInputs;
    private NotificationCompat.WearableExtender wearableExtender;
    private Notification notification;

    NotificationData(String title, String text, Bitmap icon, Date time, String app, String packageName, Notification notification) {
        this.title = title;
        this.text = text;
        this.icon = icon;
        this.time = time;
        this.app = app;
        this.packageName = packageName;
        this.extras = extras;
        this.intent = intent;
        this.remoteInputs = remoteInputs;
        this.wearableExtender = wearableExtender;
        this.notification = notification;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public String getTime() {
        SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm");
        String currentTime = localDateFormat.format(time);
        return currentTime;
    }

    public String getApp() {
        return app;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPackageName() {
        return packageName;
    }

    public Bundle getExtras() {
        return extras;
    }

    public PendingIntent getIntent() {
        return intent;
    }

    public RemoteInput[] getRemoteInputs() {
        return remoteInputs;
    }

    public NotificationCompat.WearableExtender getWearableExtender() {
        return wearableExtender;
    }
}