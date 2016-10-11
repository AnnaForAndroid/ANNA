package com.anna;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.os.Parcelable;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by PARSEA on 11.10.2016.
 */

public class NotificationAccessibilityService extends AccessibilityService {

    protected void onServiceConnected() {
        Log.d("ANNA", "AccessibilityService Connected");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        info.notificationTimeout = 100;
        setServiceInfo(info);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent e) {
        Log.d("Tortuga","FML");
        if (e.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            Log.d("Tortuga","Recieved event");
            Parcelable data = e.getParcelableData();
            if (data instanceof Notification) {
                Log.d("Tortuga","Recieved notification");
                Notification notification = (Notification) data;
                Log.d("Tortuga","ticker: " + notification.tickerText);
                Log.d("Tortuga","icon: " + notification.icon);
                Log.d("Tortuga", "notification: "+ e.getText());
            }
        }
    }

    @Override
    public void onInterrupt() {
        // TODO Auto-generated method stub
    }

    public void onNotificationPosted(){

    }
}
