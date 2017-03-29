package com.anna.phone;

import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.anna.dashboard.Dashboard;
import com.anna.util.MyApplication;

/**
 * Created by PARSEA on 13.02.2017.
 */

class EndCallListener extends PhoneStateListener {

    private boolean initiated;

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        if (TelephonyManager.CALL_STATE_RINGING == state) {
        }
        if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
            //wait for phone to go offhook (probably set a boolean flag) so you know your app initiated the call.
            initiated = true;
        }
        if (TelephonyManager.CALL_STATE_IDLE == state) {
            //when this state occurs, and your flag is set, restart your app
            if (initiated) {
                MyApplication.application.getDashboard().startActivity(new Intent(MyApplication.application.getApplicationContext(), Dashboard.class));
                initiated = false;
            }
        }
    }
}
