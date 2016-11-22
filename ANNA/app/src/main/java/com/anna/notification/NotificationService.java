package com.anna.notification;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.anna.util.Module;
import com.anna.chat.ChatViewActivity;
import com.anna.util.MyApplication;

import java.util.Date;


public class NotificationService extends NotificationListenerService {

    public static boolean isNotificationAccessEnabled;
    private final String notificationService = MyApplication.getAppContext().NOTIFICATION_SERVICE;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        if (sbn.isClearable()) {
            NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender(sbn.getNotification());
            String pack = sbn.getPackageName();
            String appName = null;
            try {
                PackageManager pm = getApplicationContext().getPackageManager();
                ApplicationInfo ai = pm.getApplicationInfo(pack, 0);
                appName = pm.getApplicationLabel(ai).toString();
            } catch (final PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (Module.enabledAppNames.contains(appName)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cancelNotification(sbn.getKey());
                } else {
                    cancelNotification(pack, sbn.getTag(), sbn.getId());
                }
                if (wearableExtender.getActions().size() > 0) {
                    Bundle extras = sbn.getNotification().extras;
                    String title = extras.getString("android.title");
                    CharSequence text = extras.getCharSequence("android.text");
                    Bitmap icon = sbn.getNotification().largeIcon;
                    long time = sbn.getPostTime();

                    ChatViewActivity.notifications.add(new NotificationData(title, text, icon, new Date(time), appName, sbn.getNotification(), pack));
                }
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg", "Notification Removed");
    }

    @Override
    public IBinder onBind(Intent mIntent) {
        IBinder mIBinder = super.onBind(mIntent);
        isNotificationAccessEnabled = true;
        return mIBinder;
    }

    @Override
    public boolean onUnbind(Intent mIntent) {
        boolean mOnUnbind = super.onUnbind(mIntent);
        isNotificationAccessEnabled = false;
        return mOnUnbind;
    }
}