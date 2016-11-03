package com.anna;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotificationData implements Parcelable {
    private String title;
    private String text;
    private Bitmap icon;
    private Date time;
    private String app;
    private String packageName;

    NotificationData(String title, String text, Bitmap icon, Date time, String app, String packageName) {
        this.title = title;
        this.text = text;
        this.icon = icon;
        this.time = time;
        this.app = app;
        this.packageName = packageName;
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

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setApp(String app) {
        this.app = app;
    }

    // Parcelling part
    public NotificationData(Parcel in) {
        Object[] data = in.readArray(Object.class.getClassLoader());

        this.title = (String) data[0];
        this.text = (String) data[1];
        this.icon = (Bitmap) data[2];
        this.time = (Date) data[3];
        this.app = (String) data[4];
        this.packageName = (String) data[5];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeArray(new Object[]{this.title,
                this.text,
                this.icon,
                this.time,
                this.app, this.packageName});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public NotificationData createFromParcel(Parcel in) {
            return new NotificationData(in);
        }

        public NotificationData[] newArray(int size) {
            return new NotificationData[size];
        }
    };
}