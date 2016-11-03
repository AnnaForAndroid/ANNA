package com.anna;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.support.v4.content.LocalBroadcastManager;

import java.util.Date;


public class NotificationService extends NotificationListenerService {

    Context context;
    public static boolean isNotificationAccessEnabled = false;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

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

            Bundle extras = sbn.getNotification().extras;
            String title = extras.getString("android.title");
            String text = extras.getCharSequence("android.text").toString();
            Bitmap icon = sbn.getNotification().largeIcon;
            long time = sbn.getPostTime();

            NotificationData notificationData = new NotificationData(title, text, icon, new Date(time), appName, pack);

            Intent msgrcv = new Intent("Msg");
            msgrcv.putExtra("notificationData", notificationData);

            LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
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