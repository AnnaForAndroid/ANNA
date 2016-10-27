package com.anna;

import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotificationData {
    private String title;
    private String text;
    private Bitmap icon;
    private Date time;
    private String app;

    NotificationData(String title, String text, Bitmap icon, Date time, String app) {
        this.title = title;
        this.text = text;
        this.icon = icon;
        this.time = time;
        this.app = app;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setApp(String app) {
        this.app = app;
    }
}