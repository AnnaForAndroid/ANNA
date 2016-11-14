package com.anna.notification;

import android.app.Notification;
import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotificationData {
    private String title;
    private CharSequence text;
    private Bitmap icon;
    private Date time;
    private String app;
    private String packageName;
    private Notification notification;

    NotificationData(String title, CharSequence text, Bitmap icon, Date time, String app, Notification notification, String packageName) {
        this.title = title;
        this.text = text;
        this.icon = icon;
        this.time = time;
        this.app = app;
        this.notification = notification;
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getTitle() {
        return title;
    }

    public CharSequence getText() {
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

}