package com.anna;

import android.graphics.drawable.Icon;

import java.util.Date;

public class NotificationData {
    private String title;
    private String text;
    private Icon icon;
    private Date time;
    private String app;

    NotificationData(String title, String text, Icon icon, Date time, String app) {
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

    public Icon getIcon() {
        return icon;
    }

    public Date getTime() {
        return time;
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

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setApp(String app) {
        this.app = app;
    }
}