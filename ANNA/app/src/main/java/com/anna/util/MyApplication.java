package com.anna.util;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.anna.dashboard.Dashboard;
import com.anna.preferences.PreferencesHelper;
import com.anna.voice.VoiceControl;
import com.anna.voice.VoiceOutput;

/**
 * Created by PARSEA on 08.11.2016.
 */

public class MyApplication extends Application {

    private Dashboard dashboard;
    private PreferencesHelper sharedPreferences;
    public static MyApplication application;
    private VoiceOutput voiceOutput;
    private VoiceControl voiceControl;


    public void onCreate() {
        super.onCreate();
        sharedPreferences = new PreferencesHelper(getApplicationContext());
        MyApplication.application=this;
        voiceOutput = new VoiceOutput(this);
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(Dashboard dashboard){
        this.dashboard = dashboard;
        voiceControl = new VoiceControl();
    }

    public PreferencesHelper getSharedPreferences() {
        return sharedPreferences;
    }

    public VoiceOutput getVoiceOutput() {
        return voiceOutput;
    }

    public VoiceControl getVoiceControl() {
        return voiceControl;
    }
}
